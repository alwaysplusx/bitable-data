package com.harmony.bitable.convert.bitval.impl

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.bitval.BitvalReader
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getFieldValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
class TextBitvalReader : BitvalReader {

    override fun canRead(property: BitablePersistentProperty) =
        property.getBitfieldType() == BitfieldType.TEXT && CharSequence::class.java.isAssignableFrom(property.type)

    override fun read(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.getFieldValue(property.getBitfieldName()) ?: return null
        return if (value is List<*>) {
            (value[0] as Map<String, String>)["text"]
        } else {
            value as String
        }
    }

}