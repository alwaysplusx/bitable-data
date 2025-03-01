package com.harmony.bitable

import com.harmony.bitable.convert.BitvalConverter
import com.lark.oapi.service.bitable.v1.model.AppTableFieldForList
import org.springframework.data.mapping.model.Property
import kotlin.reflect.KClass

data class BitityField(
    val fieldName: String,
    val fieldType: BitfieldType,
    val isRecordId: Boolean,
    val isReadonly: Boolean,
    val customizeConverter: KClass<BitvalConverter>,
    val property: Property,
    val appField: AppTableFieldForList?,
) {

    override fun toString(): String {
        return "BitityField(name=${fieldName}, type=${fieldType})"
    }

}
