package com.harmony.bitable.oapi.cursor

import org.springframework.data.util.Streamable

/**
 * 依据 page_token 获取到的其中一段分页数据
 */
interface PageSlice<T> : Iterable<T>, Streamable<T> {

    /**
     * 是否有下一段数据
     */
    fun hasNextSlice(): Boolean

    /**
     * 用户获取下一段数据的 token
     */
    fun nextToken(): String?

    /**
     * slice 中的数据个数
     */
    fun size(): Int

    /**
     * 总数量
     */
    fun total(): Int

}
