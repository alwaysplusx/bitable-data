package com.harmony.bitable

/**
 * 在限定的 [app_token](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/bitable/notification#8121eebe)下, 获取提供 bitable 元数据
 * @see com.harmony.bitable.oapi.bitable.BitableApiImpl
 */
interface BitableSource {

    /**
     * 依据表名获取飞书表格信息
     *
     * @param name table name
     */
    fun getBitable(name: String): Bitable

}
