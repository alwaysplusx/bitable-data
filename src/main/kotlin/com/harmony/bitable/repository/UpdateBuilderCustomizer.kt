package com.harmony.bitable.repository

import com.harmony.bitable.core.UpdateBuilder

interface UpdateBuilderCustomizer<T> {

    fun customize(setter: UpdateBuilder<T>)

}