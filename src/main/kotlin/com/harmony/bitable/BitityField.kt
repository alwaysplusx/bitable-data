package com.harmony.bitable

import com.harmony.bitable.convert.BitvalConverter
import org.springframework.data.mapping.model.Property
import kotlin.reflect.KClass

data class BitityField(
    @Deprecated("will replace by appField")
    val fieldId: String?,
    val fieldName: String,
    val fieldType: BitfieldType,
    val property: Property,
    val isRecordIdField: Boolean,
    val isReadonly: Boolean,
    val customizeConverter: KClass<BitvalConverter>
) {

    constructor(fieldId: String, source: BitityField) : this(
        fieldId = fieldId,
        fieldName = source.fieldName,
        fieldType = source.fieldType,
        property = source.property,
        isRecordIdField = source.isRecordIdField,
        isReadonly = source.isReadonly,
        customizeConverter = source.customizeConverter
    )

    override fun toString(): String {
        return "BitityField(name=${fieldName}, type=${fieldType})"
    }

}
