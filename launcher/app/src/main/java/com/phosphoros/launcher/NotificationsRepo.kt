package com.phosphoros.launcher

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NotificationLine(
    val title: String,
    val text: String,
    val from: String,
    val atEpochMs: Long,
)

object NotificationsRepo {
    private const val MAX = 50

    private val _items = MutableStateFlow<List<NotificationLine>>(emptyList())
    val items: StateFlow<List<NotificationLine>> = _items.asStateFlow()

    fun push(item: NotificationLine) {
        val next = (listOf(item) + _items.value).take(MAX)
        _items.value = next
    }

    fun clear() {
        _items.value = emptyList()
    }
}

