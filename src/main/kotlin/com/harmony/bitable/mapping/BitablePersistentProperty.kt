package com.harmony.bitable.mapping

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.BitvalConverter
import org.springframework.data.mapping.PersistentProperty
import kotlin.reflect.KClass

/**
 * 与多维表格中的列对应
 */
interface BitablePersistentProperty : PersistentProperty<BitablePersistentProperty> {

    /**
     * 多维表列ID(如果 recordId 则为空)
     */
    fun getBitfieldId(): String?

    /**
     * 实体的字段名称
     */
    fun getSimpleName(): String

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
    fun isRecordId(): Boolean

    /**
     * 是否是只读列
     */
    fun isReadonly(): Boolean

    /**
     * 获取位于[com.harmony.bitable.annotations.Bitfield.converter]的自定义converter
     */
    fun getCustomizeConverterType(): KClass<BitvalConverter>

}
