package com.harmony.bitable.mapping

/**
 * 用户获取飞书多维表格的行数据的 [RecordId](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/bitable/notification#15d8db94)
 */
interface RecordIdAccessor {

    fun getRecordId(): String?

    fun getRequiredRecordId(): String {
        val recordId = getRecordId()
        if (recordId != null) {
            return recordId
        }
        throw IllegalStateException("Could not obtain recordId!")
    }

}
