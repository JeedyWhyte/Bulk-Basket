from django.db import models


class Category(models.Model):
    name = models.CharField(max_length=100, unique=True)
    slug = models.SlugField(unique=True)
    icon_url = models.URLField(blank=True)

    class Meta:
        verbose_name_plural = 'categories'

    def __str__(self):
        return self.name


class Product(models.Model):
    """A product listing belonging to a seller."""

    class Unit(models.TextChoices):
        KG = 'kg', 'Kilogram'
        BAG = 'bag', 'Bag'
        BASKET = 'basket', 'Basket'
        PIECE = 'piece', 'Piece'
        BUNDLE = 'bundle', 'Bundle'

    seller = models.ForeignKey(
        'users.User',
        on_delete=models.CASCADE,
        related_name='products',
        limit_choices_to={'role': 'seller'},
    )
    category = models.ForeignKey(
        Category,
        on_delete=models.SET_NULL,
        null=True,
        related_name='products',
    )
    name = models.CharField(max_length=200)
    description = models.TextField(blank=True)
    price = models.DecimalField(max_digits=12, decimal_places=2)
    unit = models.CharField(
        max_length=10,
        choices=Unit.choices,
        default=Unit.KG,
    )
    min_order_qty = models.PositiveIntegerField(default=1)
    stock_quantity = models.PositiveIntegerField(default=0)
    image_url = models.URLField(blank=True)
    is_available = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.name} by {self.seller.username}"

    @property
    def in_stock(self):
        return self.stock_quantity > 0 and self.is_available