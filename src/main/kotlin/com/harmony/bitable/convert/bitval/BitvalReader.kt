package com.harmony.bitable.convert.bitval

import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
interface BitvalReader {

    fun canRead(property: BitablePersistentProperty): Boolean

    fun read(property: BitablePersistentProperty, record: AppTableRecord): Any?

}