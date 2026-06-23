from rest_framework.response import Response


def success_response(data=None, message="Success", status=200):
    return Response({
        "status": "success",
        "message": message,
        "data": data,
    }, status=status)


def error_response(message="Error", errors=None, status=400):
    return Response({
        "status": "error",
        "message": message,
        "errors": errors,
    }, status=status)