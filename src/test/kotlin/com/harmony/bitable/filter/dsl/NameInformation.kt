package com.harmony.bitable.filter.dsl

import com.harmony.bitable.utils.NameFunctionUtils.getNameInformation
import org.springframework.data.mapping.model.Property
import kotlin.reflect.KMutableProperty1

/**
 * @param R name type
 */
class NameInformation<R>(
    private val owner: Class<*>,
    val property: Property,
) {

    companion object {

        fun <T, R> from(nameFunction: KMutableProperty1<T, R>) = getNameInformation(nameFunction)

    }

    override fun toString(): String {
        return "NameInformation(${owner.simpleName}.${property.name})"
    }

}
