from django.db.models import ProtectedError
from rest_framework.exceptions import APIException
from rest_framework.response import Response
from rest_framework.views import exception_handler as drf_exception_handler


class BusinessLogicError(APIException):
    status_code = 400
    default_detail = 'A business rule was violated.'
    default_code = 'business_error'


class ResourceNotFoundError(APIException):
    status_code = 404
    default_detail = 'The requested resource was not found.'
    default_code = 'not_found'


def custom_exception_handler(exc, context):
    """Wraps DRF's default handler so deleting a row that's still
    referenced through an on_delete=PROTECT FK (e.g. a Product referenced
    by an OrderItem) comes back as a clean 400 instead of an unhandled 500.
    """
    if isinstance(exc, ProtectedError):
        return Response(
            {
                "status": "error",
                "message": "This item cannot be deleted because it is still referenced by other records.",
            },
            status=400,
        )
    return drf_exception_handler(exc, context)