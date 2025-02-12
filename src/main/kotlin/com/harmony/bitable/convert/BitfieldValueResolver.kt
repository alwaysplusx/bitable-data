package com.harmony.bitable.convert

import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
interface BitfieldValueResolver {

    fun readValue(property: BitablePersistentProperty, record: AppTableRecord): Any?

}