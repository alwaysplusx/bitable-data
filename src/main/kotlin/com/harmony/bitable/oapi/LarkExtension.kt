package com.harmony.bitable.oapi

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.cursor.PageSlice
import com.lark.oapi.core.response.BaseResponse
import com.lark.oapi.service.bitable.v1.model.AppTableFieldForList
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

fun <T> BaseResponse<T>.ensureOk() {
    if (!this.success()) {
        throw LarkException(
            code = this.code,
            message = "lark response not ok, for reason $msg. details $error"
        )
    }
}

fun <T> BaseResponse<T>.ensureData(): T {
    ensureOk()
    return this.data
}

fun <T, R> BaseResponse<T>.ensurePage(converter: (T) -> PageSlice<R>): PageSlice<R> {
    val data = ensureData()
    return converter(data)
}

fun AppTableRecord.getPropertyValue(property: BitablePersistentProperty): Any? = this.fields[property.getBitfieldName()]

fun AppTableFieldForList.getBitableType(): BitfieldType {
    return BitfieldType.entries.firstOrNull { it.value == this.type } ?: BitfieldType.AUTO
}
