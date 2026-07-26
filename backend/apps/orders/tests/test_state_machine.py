import pytest
from apps.common.exceptions import BusinessLogicError
from apps.orders.state_machine import transition_order, TRANSITIONS


@pytest.mark.django_db
class TestOrderStateMachine:

    def test_valid_transitions_defined(self):
        assert 'pending' in TRANSITIONS
        assert 'confirmed' in TRANSITIONS
        assert 'delivered' in TRANSITIONS
        assert 'cancelled' in TRANSITIONS

    def test_pending_can_move_to_confirmed(self):
        assert 'confirmed' in TRANSITIONS['pending']

    def test_pending_can_be_cancelled(self):
        assert 'cancelled' in TRANSITIONS['pending']

    def test_delivered_has_no_transitions(self):
        assert TRANSITIONS['delivered'] == []

    def test_cancelled_has_no_transitions(self):
        assert TRANSITIONS['cancelled'] == []

    def test_in_transit_can_only_move_to_delivered(self):
        assert TRANSITIONS['in_transit'] == ['delivered']