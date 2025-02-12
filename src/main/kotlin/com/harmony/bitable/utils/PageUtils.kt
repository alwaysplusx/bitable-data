package com.harmony.bitable.utils

import com.harmony.bitable.oapi.cursor.PageCursor
import com.harmony.bitable.oapi.cursor.PageSlice
import com.harmony.bitable.oapi.Pageable

/**
 * @author wuxin
 */
object PageUtils {

    /**
     * 基于 loader 从首页开始构建 PageIterator
     */
    fun <T> first(pageSize: Int, loader: (Pageable) -> PageSlice<T>) = scan(pageSize, null, loader)

    /**
     * 基于 loader 构建 PageIterator
     */
    fun <T> scan(pageSize: Int, pageToken: String?, loader: (Pageable) -> PageSlice<T>): PageCursor<T> {
        return PageCursorImpl(pageSize, pageToken, loader)
    }

    private class PageCursorImpl<T>(
        private val pageSize: Int,
        pageToken: String?,
        private val loader: (Pageable) -> PageSlice<T>,
    ) : PageCursor<T> {

        private var hasNext: Boolean = true
        private var nextToken: String? = pageToken

        override fun hasNext(): Boolean {
            return hasNext
        }

        override fun next(): PageSlice<T> {
            if (!hasNext()) {
                throw NoSuchElementException("no more next page slice")
            }
            val nextSlice = loader(Pageable(pageSize, nextToken))
            this.nextToken = nextSlice.nextToken()
            this.hasNext = nextSlice.hasNextSlice()
            return nextSlice
        }

    }

}
