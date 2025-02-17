package com.harmony.bitable.oapi.cursor

import org.springframework.data.util.Streamable
import java.util.stream.Stream

/**
 * 飞书表格数据按 page_token 依次获取下一页(首页 page_token 为空)
 */
interface PageCursor<T> : Iterator<PageSlice<T>> {

    fun steamOfElements(): Stream<T> = Streamable.of(Iterable { this }).stream().flatMap { it.stream() }

}
