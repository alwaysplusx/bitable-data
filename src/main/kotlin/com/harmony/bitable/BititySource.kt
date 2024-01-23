package com.harmony.bitable

/**
 * 解析当前工程的实体, 并得出 bitity 的基础数据提供
 */
interface BititySource {

    fun <T : Any> getBitity(type: Class<T>): Bitity<T>

}
