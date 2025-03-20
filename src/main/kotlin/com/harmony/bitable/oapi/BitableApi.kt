package com.harmony.bitable.oapi

import com.harmony.bitable.Bitable
import com.harmony.bitable.BitableAddress

/**
 * @author wuxin
 */
interface BitableApi {

    /**
     * 从 [appToken](https://open.feishu.cn/document/server-docs/docs/bitable-v1/notification) 下获取与入参名称相同的多维表格(多维表格中表格名唯一)
     * @see com.lark.oapi.service.bitable.v1.resource.AppTable.list
     */
    fun getBitable(appToken: String, tableName: String): Bitable

    /**
     * @see com.lark.oapi.service.bitable.v1.resource.AppTable.list
     */
    fun getBitable(address: BitableAddress): Bitable

}