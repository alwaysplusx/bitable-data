package com.harmony.bitable

/**
 * 飞书多维表格在线地址
 */
data class BitableAddress(val appToken: String, val tableId: String) {

    override fun toString(): String {
        return "https://feishu.cn/base/${appToken}?table=${tableId}"
    }

}
