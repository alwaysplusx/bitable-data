package com.harmony.bitable

/**
 * [飞书多维表格的字段类型](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/bitable-v1/app-table-field/guide)
 * @param value 飞书多维表格的值
 */
enum class BitfieldType(val value: Int) {

    /**
     * 自动识别类型
     */
    AUTO(0),

    TEXT(1),

    NUMBER(2),

    SINGLE_SELECT(3),

    MULTI_SELECT(4),

    DATE_TIME(5),

    CHECKBOX(7),

    PERSON(11),

    PHONE_NUMBER(13),

    URL(15),

    ATTACHMENT(17),

    ASSOCIATION(18),

    LOOKUP(19),

    FORMULA(20),

    LOCATION(22),

    GROUP(23),

    CREATED_AT(1001),

    UPDATED_AT(1002),

    CREATED_BY(1003),

    UPDATED_BY(1004),

    AUTO_SERIAL(1005),

}
