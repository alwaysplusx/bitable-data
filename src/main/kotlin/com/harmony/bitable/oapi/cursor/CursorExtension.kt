package com.harmony.bitable.oapi.cursor

fun <T> PageCursor<T>.nextSliceOrNull(): PageSlice<T>? {
    return if (hasNext()) next() else null
}

fun <T> PageSlice<T>.firstElement(predicate: (T) -> Boolean = { true }): T {
    return this.stream()
        .filter(predicate)
        .findFirst()
        .orElseThrow { throw IllegalStateException("element not found") }
}

fun <T> PageSlice<T>.firstElementOrNull(predicate: (T) -> Boolean = { true }): T? {
    return this.stream().filter(predicate).findFirst().orElse(null)
}

fun <T> PageCursor<T>.firstElement(predicate: (T) -> Boolean = { true }): T {
    return this.steamOfElements()
        .filter(predicate)
        .findFirst()
        .orElseThrow { throw IllegalStateException("element not found") }
}

fun <T> PageCursor<T>.firstElementOrNull(predicate: (T) -> Boolean = { true }): T? =
    this.steamOfElements().filter(predicate).findFirst().orElse(null)

fun <T> PageCursor<T>.toElementList(): MutableList<T> = this.steamOfElements().toList()

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