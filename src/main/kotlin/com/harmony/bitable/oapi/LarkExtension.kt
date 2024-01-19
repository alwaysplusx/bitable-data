package com.harmony.bitable.oapi

import com.lark.oapi.core.response.BaseResponse
import org.springframework.data.util.Streamable
import java.util.stream.Stream

fun <T> PageCursor<T>.first(predicate: (T) -> Boolean = { true }): T {
    return this.stream()
        .filter(predicate)
        .findFirst()
        .orElseThrow { throw IllegalStateException("item not found") }
}

fun <T> PageCursor<T>.firstOrNull(predicate: (T) -> Boolean = { true }) =
    this.stream().filter(predicate).findFirst().orElse(null)

fun <T> PageCursor<T>.firstNotNullOf(predicate: (T) -> Boolean = { true }): T {
    return first { it != null && predicate(it) }
}

fun <T> PageCursor<T>.stream(): Stream<T> = Streamable.of(Iterable { this }).stream().flatMap { it.stream() }

fun <T> PageCursor<T>.toList(): MutableList<T> = this.stream().toList()


fun <T, R> PageCursor<T>.convert(converter: (T) -> R): PageCursor<R> {
    val cursor: PageCursor<T> = this
    return object : PageCursor<R> {
        override fun hasNext(): Boolean {
            return cursor.hasNext()
        }

        override fun next(): PageSlice<R> {
            return cursor.next().convert(converter)
        }
    }
}

fun <T, R> PageSlice<T>.convert(converter: (T) -> R): PageSlice<R> {
    return SimplePageSlice(
        items = this.stream().map(converter).toList(),
        total = this.total(),
        nextToken = this.nextToken(),
        hasNextSlice = this.hasNextSlice()
    )
}

fun <T> BaseResponse<T>.ensureOk() {
    if (!this.success()) {
        throw LarkException("lark response not success, reason $msg")
    }
}

fun <T> BaseResponse<T>.ensureData(): T {
    ensureOk()
    return this.data
}

fun <T, R> BaseResponse<T>.ensurePage(converter: (T) -> PageSlice<R>): PageSlice<R> {
    val data = ensureData()
    return converter(data)
}
