package com.harmony.bitable.convert.bitval.impl

import com.harmony.bitable.convert.bitval.BitvalReader
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
class RecordIdBitvalReader : BitvalReader {

    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.isRecordIdProperty()
    }

    override fun read(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        return record.recordId
    }

}