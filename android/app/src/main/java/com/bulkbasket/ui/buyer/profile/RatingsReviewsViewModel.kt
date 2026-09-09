package com.bulkbasket.ui.buyer.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.domain.model.Review
import com.bulkbasket.domain.repository.IReviewRepository
import com.bulkbasket.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RatingsReviewsState(
    val isLoading: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class RatingsReviewsViewModel @Inject constructor(
    private val reviewRepository: IReviewRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RatingsReviewsState())
    val state: StateFlow<RatingsReviewsState> = _state

    init {
        loadReviews()
    }

    fun loadReviews() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = reviewRepository.getMyReviews()) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        reviews = result.data,
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}
