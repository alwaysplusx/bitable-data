package com.harmony.bitable.convert

import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
interface BitvalConverter {

    fun canRead(property: BitablePersistentProperty): Boolean

    fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any?

    // fun write(property: BitablePersistentProperty, value: Any?): Any?
}