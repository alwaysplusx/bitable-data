package com.harmony.bitable.mapping

import com.harmony.bitable.BitfieldType
import org.springframework.data.mapping.PersistentProperty

/**
 * 与多维表格中的列对应
 */
interface BitablePersistentProperty : PersistentProperty<BitablePersistentProperty> {

    /**
     * 多维表给列ID
     */
    fun getBitfieldId(): String?

    /**
     * 多维表格列名
     */
    fun getBitfieldName(): String

    /**
     * 多维表格列类型
     */
    fun getBitfieldType(): BitfieldType

    /**
     * 判断是否是多维表格的 [RecordId](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/bitable/notification#15d8db94) 字段
     *
     */
    fun isRecordIdProperty(): Boolean

}
