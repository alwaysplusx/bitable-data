package com.harmony.bitable

/**
 * 在限定的 [app_token](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/bitable/notification#8121eebe)下, 获取提供 bitable 元数据
 * @see com.harmony.bitable.oapi.bitable.BitableApiImpl
 */
interface BitableSource {

    /**
     * 数据源飞书多维表格 appToken
     */
    fun getAppToken(): String

    /**
     * 将 type 以实体的方式解析
     * @param domainType must be annotated with @Bitable
     */
    fun <T> getBitity(domainType: Class<T>): Bitity<T>

}
