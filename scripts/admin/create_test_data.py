"""
Run this script to create test data for development.
Usage: python scripts/admin/create_test_data.py

Run from the backend directory with venv activated.
"""

import os
import sys
import django

# Add backend to path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', '..', 'backend'))
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'config.settings')
django.setup()

from apps.users.models import User
from apps.products.models import Category, Product


def create_test_data():
    print("Creating test data...")

    # Create test buyer
    buyer, created = User.objects.get_or_create(
        username='testbuyer',
        defaults={
            'email': 'buyer@test.com',
            'role': 'buyer',
            'phone_number': '08012345678',
        }
    )
    if created:
        buyer.set_password('testpass123')
        buyer.save()
        print("Created test buyer")

    # Create test seller
    seller, created = User.objects.get_or_create(
        username='testseller',
        defaults={
            'email': 'seller@test.com',
            'role': 'seller',
            'phone_number': '08087654321',
        }
    )
    if created:
        seller.set_password('testpass123')
        seller.save()
        print("Created test seller")

    # Create test rider
    rider, created = User.objects.get_or_create(
        username='testrider',
        defaults={
            'email': 'rider@test.com',
            'role': 'rider',
            'phone_number': '08011111111',
        }
    )
    if created:
        rider.set_password('testpass123')
        rider.save()
        print("Created test rider")

    # Create categories
    categories = ['Vegetables', 'Grains', 'Proteins', 'Fruits', 'Household']
    for name in categories:
        Category.objects.get_or_create(
            name=name,
            defaults={'slug': name.lower()}
        )
    print("Created categories")

    print("Test data created successfully.")
    print("Credentials: username/testpass123 for all accounts")


if __name__ == '__main__':
    create_test_data()