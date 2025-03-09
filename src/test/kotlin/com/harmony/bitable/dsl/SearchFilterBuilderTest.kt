package com.harmony.bitable.dsl

import com.harmony.bitable.Book
import com.harmony.bitable.core.SearchFilterBuilder


fun main() {

    SearchFilterBuilder(Book::class.java).where() {
        Book::name.`is`("Title")
    }

}