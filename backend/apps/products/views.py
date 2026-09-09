from django.db.models import Q
from rest_framework import viewsets, permissions
from apps.common.permissions import IsSeller
from .models import Product, Category
from .serializers import ProductSerializer, CategorySerializer
from .filters import ProductFilter


class CategoryViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Category.objects.all()
    serializer_class = CategorySerializer
    permission_classes = [permissions.AllowAny]


class ProductViewSet(viewsets.ModelViewSet):
    serializer_class = ProductSerializer
    filterset_class = ProductFilter
    search_fields = ['name', 'description']
    ordering_fields = ['price', 'created_at']

    def get_queryset(self):
        base = Product.objects.select_related('seller', 'category')

        # Write actions may only ever touch the caller's own products.
        if self.action in ['update', 'partial_update', 'destroy']:
            return base.filter(seller=self.request.user)

        # `?seller=me` — a seller managing their inventory sees all of
        # their own products, including unavailable ones.
        if (
            self.request.query_params.get('seller') == 'me'
            and self.request.user.is_authenticated
        ):
            return base.filter(seller=self.request.user)

        # A seller can always fetch a single product of their own (e.g. to
        # edit one they've soft-deleted), even without passing ?seller=me.
        if self.action == 'retrieve' and self.request.user.is_authenticated:
            return base.filter(
                Q(is_available=True) | Q(seller=self.request.user)
            )

        # Public catalogue: available products only.
        return base.filter(is_available=True)

    def get_permissions(self):
        if self.action in ['create', 'update', 'partial_update', 'destroy']:
            return [IsSeller()]
        return [permissions.AllowAny()]

    def perform_create(self, serializer):
        serializer.save(seller=self.request.user)

    def perform_destroy(self, instance):
        # Soft delete: past orders reference this product, so removing the
        # row would corrupt order history. Hiding it removes it from the
        # catalogue while keeping history intact.
        instance.is_available = False
        instance.save(update_fields=['is_available', 'updated_at'])
