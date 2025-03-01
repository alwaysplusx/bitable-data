package com.harmony.bitable

import com.harmony.bitable.dsl.SearchFilterBuilder
import com.harmony.bitable.repository.BitableDslRepository

fun main() {
    SearchFilterBuilder(Author::class.java).where {
        Author::getName `is` "张三"
    }
}

interface AuthorRepository : BitableDslRepository<Author>