package com.harmony.bitable.oapi.cursor

class SimplePageSlice<T>(
    private val items: List<T>,
    private val total: Int,
    private val nextToken: String?,
    private val hasNextSlice: Boolean = false
) : PageSlice<T> {

    override fun hasNextSlice(): Boolean {
        return hasNextSlice
    }

    override fun nextToken(): String? {
        return nextToken
    }

    override fun iterator(): MutableIterator<T> {
        return items.toMutableList().iterator()
    }

    override fun size(): Int {
        return items.size
    }

    override fun total(): Int {
        return total
    }

}
