import pytest
from django.contrib.auth import get_user_model

User = get_user_model()


@pytest.mark.django_db
class TestUserModel:

    def test_create_buyer(self):
        user = User.objects.create_user(
            username='testbuyer',
            email='buyer@test.com',
            password='testpass123',
            role='buyer',
        )
        assert user.username == 'testbuyer'
        assert user.role == 'buyer'
        assert user.is_verified is False

    def test_create_seller(self):
        user = User.objects.create_user(
            username='testseller',
            email='seller@test.com',
            password='testpass123',
            role='seller',
        )
        assert user.role == 'seller'

    def test_create_rider(self):
        user = User.objects.create_user(
            username='testrider',
            email='rider@test.com',
            password='testpass123',
            role='rider',
        )
        assert user.role == 'rider'

    def test_user_str(self):
        user = User.objects.create_user(
            username='testuser',
            email='test@test.com',
            password='testpass123',
            role='buyer',
        )
        assert str(user) == 'testuser (buyer)'