package com.thepurpleweb.purplelauncher.nowbar

data class NowBarEvent(
    val item: NowBarItem,
    val priority: Int,
    val expiresAt: Long? = null
)
