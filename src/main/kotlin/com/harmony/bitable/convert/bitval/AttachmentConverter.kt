package com.harmony.bitable.convert.bitval

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.Attachment

/**
 * @author wuxin
 */
class AttachmentConverter : BitvalConverter {

    override fun canHandle(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.ATTACHMENT
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.getPropertyValue(property)
        val attachments = ValueConverters.convertToArray(value, Attachment::class.java) ?: return null
        return when {
            property.type.isArray -> attachments
            property.type == List::class.java -> attachments.toList()
            property.type == Attachment::class.java -> attachments.firstOrNull()
            else -> {
                throw IllegalArgumentException("Unsupported Attachment type: ${property.type}")
            }
        }
    }

    override fun convertAndWrite(value: Any?, property: BitablePersistentProperty, record: AppTableRecord) {
        record.fields[property.getBitfieldName()] = value
    }

}
