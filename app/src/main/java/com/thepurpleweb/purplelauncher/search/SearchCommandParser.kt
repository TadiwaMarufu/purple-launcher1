package com.thepurpleweb.purplelauncher.search

class SearchCommandParser {

    fun parse(query: String): SearchCommand {

        return when (
            query
                .trim()
                .lowercase()
        ) {
            "drawer",
            "app drawer",
            "apps",
            "applications" ->
                SearchCommand.OPEN_APP_DRAWER

            "focus",
            "switch focus" ->
                SearchCommand.SWITCH_PROFILE

            "settings",
            "launcher settings" ->
                SearchCommand.OPEN_SETTINGS

            else ->
                SearchCommand.NONE
        }
    }
}
