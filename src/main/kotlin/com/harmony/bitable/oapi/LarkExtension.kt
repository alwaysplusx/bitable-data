package com.harmony.bitable.oapi

import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.cursor.PageSlice
import com.lark.oapi.core.response.BaseResponse
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

fun <T> BaseResponse<T>.ensureOk() {
    if (!this.success()) {
        throw LarkException("lark response not success, for reason $msg. error details $error")
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

fun AppTableRecord.getFieldValue(name: String): Any? = this.fields[name]

fun AppTableRecord.getPropertyValue(property: BitablePersistentProperty): Any? = this.fields[property.getBitfieldName()]
