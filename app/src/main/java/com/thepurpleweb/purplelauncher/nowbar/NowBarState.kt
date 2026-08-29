package com.thepurpleweb.purplelauncher.nowbar

data class NowBarState(
    val primary: NowBarItem? = null,
    val secondary: NowBarItem? = null,
    val expanded: Boolean = false
)
