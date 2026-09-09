import pytest
from django.db.models import ProtectedError
from rest_framework.exceptions import NotFound
from apps.common.exceptions import custom_exception_handler


@pytest.mark.django_db
class TestCustomExceptionHandler:

    def test_protected_error_becomes_clean_400(self):
        # Regression test: OrderItem.product is on_delete=PROTECT, but
        # nothing caught the resulting ProtectedError anywhere in the
        # backend, so it would have surfaced as an unhandled 500.
        exc = ProtectedError('Cannot delete', [])

        response = custom_exception_handler(exc, {})

        assert response is not None
        assert response.status_code == 400
        assert response.data['status'] == 'error'

    def test_other_exceptions_still_use_the_default_handler(self):
        response = custom_exception_handler(NotFound(), {})

        assert response is not None
        assert response.status_code == 404
