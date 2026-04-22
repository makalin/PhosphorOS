package com.phosphoros.launcher

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

fun <T> Flow<T>.firstValue(): T = kotlinx.coroutines.runBlocking { first() }

