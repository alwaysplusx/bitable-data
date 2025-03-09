package com.harmony.bitable.core

import com.harmony.bitable.repository.UpdateCustomizer
import kotlin.reflect.KFunction1
import kotlin.reflect.KMutableProperty1

/**
 * @author wuxin
 */
class UpdateDslBuilder<T> : UpdateBuilder<T>() {

    fun <R> set(field: KFunction1<T, R>, value: Any?): UpdateDslBuilder<T> {
        nameValueMap[NameInformation.of(field)] = value
        return this
    }

    fun <R> set(field: KMutableProperty1<T, R>, value: Any?): UpdateDslBuilder<T> {
        nameValueMap[NameInformation.of(field)] = value
        return this
    }

    fun withCustomizer(customizer: (req: UpdateRequestBuilder, body: UpdateBodyBuilder) -> Unit) {
        super.withCustomizer(UpdateCustomizer.wrap(customizer))
    }

}