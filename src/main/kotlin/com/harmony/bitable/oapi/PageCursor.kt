package com.harmony.bitable.oapi

/**
 * 飞书表格数据按 page_token 依次获取下一页(首页 page_token 为空)
 */
interface PageCursor<T> : Iterator<PageSlice<T>> {

    companion object {

        fun <T> empty(): PageCursor<T> = object : PageCursor<T> {
            override fun hasNext() = false

            override fun next() = PageSlice.empty<T>()
        }

    }

}
