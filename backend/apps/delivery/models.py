class Delivery(models.Model):
    order = models.OneToOneField('orders.Order', on_delete=models.CASCADE)
    rider = models.ForeignKey(
        'users.User', on_delete=models.SET_NULL, null=True,
        limit_choices_to={'role': 'rider'},
    )
    picked_up_at = models.DateTimeField(null=True)
    delivered_at = models.DateTimeField(null=True)
    current_latitude = models.DecimalField(
        max_digits=9, decimal_places=6, null=True
    )
    current_longitude = models.DecimalField(
        max_digits=9, decimal_places=6, null=True
    )