package com.harmony.bitable

import com.lark.oapi.service.bitable.v1.model.Location
import com.lark.oapi.service.bitable.v1.model.Person
import com.lark.oapi.service.bitable.v1.model.Url

/**
 * [飞书多维表格的字段类型](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/bitable-v1/app-table-field/guide)
 * @param value 飞书多维表格的值
 * @param type 提交飞书服务时的目标值类型
 */
enum class BitfieldType(val value: Int, val type: Class<*>) {

    /**
     * 自动识别类型
     */
    AUTO(0, Void::class.java),

    TEXT(1, String::class.java),

    NUMBER(2, Number::class.java),

    SINGLE_SELECT(3, String::class.java),

    MULTI_SELECT(4, Array<String>::class.java),

    DATE_TIME(5, Long::class.java),

    CHECKBOX(7, Boolean::class.java),

    PERSON(11, Array<Person>::class.java),

    URL(15, Url::class.java),

    ATTACHMENT(17, List::class.java),

    ASSOCIATION(18, String::class.java),

    FORMULA(20, String::class.java),

    CREATED_AT(1001, Long::class.java),

    UPDATED_AT(1002, Long::class.java),

    CREATED_BY(1003, Person::class.java),

    UPDATED_BY(1004, Person::class.java),

    AUTO_SERIAL(1005, String::class.java),

    PHONE_NUMBER(13, String::class.java),

    LOCATION(22, Location::class.java);

}
