package com.harmony.bitable

import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableFieldForList

/**
 * 对应一个飞书表格
 */
data class Bitable(
    val name: String,
    val address: BitableAddress,
    val fields: List<AppTableFieldForList>,
) {

    private val fieldCache: Map<String, AppTableFieldForList> = fields.associateBy { it.fieldName }

    fun getField(name: String): AppTableFieldForList? {
        return fieldCache[name]
    }

    fun getRequiredField(property: BitablePersistentProperty): AppTableFieldForList {
        return fieldCache[name] ?: throw IllegalArgumentException("$name field not found in table ${this.name}")
    }

}

