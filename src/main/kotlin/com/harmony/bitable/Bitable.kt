package com.harmony.bitable

import com.lark.oapi.service.bitable.v1.model.AppTableFieldForList

/**
 * 对应一个飞书表格
 */
data class Bitable(
    val name: String,
    val address: BitableAddress,
    val fields: List<AppTableFieldForList>,
) {

    private val fieldCache: Map<String, Int>

    init {
        this.fieldCache = mutableMapOf()
        fields.forEachIndexed { index, field ->
            fieldCache[field.fieldName] = index
        }
    }

    fun getField(index: Int): AppTableFieldForList {
        return fields[index]
    }

    fun getField(name: String): AppTableFieldForList {
        val index = fieldCache[name] ?: throw IllegalArgumentException("$name field not found in table ${this.name}")
        return getField(index)
    }

}

