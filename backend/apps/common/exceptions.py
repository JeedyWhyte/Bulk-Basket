from rest_framework.exceptions import APIException


class BusinessLogicError(APIException):
    status_code = 400
    default_detail = 'A business rule was violated.'
    default_code = 'business_error'


class ResourceNotFoundError(APIException):
    status_code = 404
    default_detail = 'The requested resource was not found.'
    default_code = 'not_found'