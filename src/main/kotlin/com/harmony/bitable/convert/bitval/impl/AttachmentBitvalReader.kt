package com.harmony.bitable.convert.bitval.impl

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.bitval.BitvalReader
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.Attachment

/**
 * @author wuxin
 */
class AttachmentBitvalReader : BitvalReader {

    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.ATTACHMENT
    }

    override fun read(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.getPropertyValue(property)
        val attachments = ValueConverters.convertArray(value, Attachment::class.java) ?: return null
        return when {
            property.type.isArray -> attachments
            property.type == List::class.java -> attachments.toList()
            property.type == Attachment::class.java -> attachments.firstOrNull()
            else -> {
                throw IllegalArgumentException("Unsupported Attachment type: ${property.type}")
            }
        }
    }

}
