package com.thepurpleweb.purplelauncher.profile

sealed class Profile(val id: String, val displayName: String) {
    object Fluid : Profile("fluid", "Fluid")
    object Premium : Profile("premium", "Premium")
    object Calm : Profile("calm", "Calm")
    object Focus : Profile("focus", "Focus")
    object Expressive : Profile("expressive", "Expressive")

    companion object {
        val all: List<Profile> by lazy {
            listOf(Fluid, Premium, Calm, Focus, Expressive)
        }

        fun fromId(id: String): Profile =
            all.find { it.id == id } ?: Calm // safe default
    }
}
