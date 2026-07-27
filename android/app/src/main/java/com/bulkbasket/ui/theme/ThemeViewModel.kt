package com.bulkbasket.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bulkbasket.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ThemeMode { LIGHT, DARK, SYSTEM }

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val prefs: PreferencesManager,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = prefs.themeMode
        .map { stored ->
            when (stored) {
                "light"  -> ThemeMode.LIGHT
                "dark"   -> ThemeMode.DARK
                else     -> ThemeMode.SYSTEM
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM,
        )

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            prefs.saveThemeMode(
                when (mode) {
                    ThemeMode.LIGHT  -> "light"
                    ThemeMode.DARK   -> "dark"
                    ThemeMode.SYSTEM -> "system"
                }
            )
        }
    }
}
