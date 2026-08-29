package com.thepurpleweb.purplelauncher.nowbar

data class NowBarItem(
    val type: NowBarType,
    val title: String,
    val subtitle: String? = null,
    val progress: Int? = null,
    val isPersistent: Boolean = false
)
