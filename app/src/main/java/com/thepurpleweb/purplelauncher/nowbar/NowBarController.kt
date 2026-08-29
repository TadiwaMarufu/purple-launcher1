package com.thepurpleweb.purplelauncher.nowbar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NowBarController {

    private val _state =
        MutableStateFlow(
            NowBarState()
        )

    val state: StateFlow<NowBarState> =
        _state.asStateFlow()

    fun setPrimary(
        item: NowBarItem?
    ) {

        _state.value =
            _state.value.copy(
                primary = item
            )
    }

    fun setSecondary(
        item: NowBarItem?
    ) {

        _state.value =
            _state.value.copy(
                secondary = item
            )
    }

    fun setItems(
        primary: NowBarItem?,
        secondary: NowBarItem? = null
    ) {

        _state.value =
            _state.value.copy(
                primary = primary,
                secondary = secondary
            )
    }

    fun toggleExpanded() {

        _state.value =
            _state.value.copy(
                expanded =
                    !_state.value.expanded
            )
    }

    fun setExpanded(
        expanded: Boolean
    ) {

        _state.value =
            _state.value.copy(
                expanded = expanded
            )
    }

    fun clear() {

        _state.value =
            NowBarState()
    }
}
