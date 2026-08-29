package com.thepurpleweb.purplelauncher.search

sealed class SearchCommand {
    object OpenAppDrawer : SearchCommand()
    object SwitchProfile : SearchCommand()
    object OpenSettings : SearchCommand()
    data class StartTimer(val durationMs: Long, val label: String) : SearchCommand()
    data class SetCustomNowBar(val message: String) : SearchCommand()
    object None : SearchCommand()
}
