package com.harmony.bitable.dsl

import com.harmony.bitable.Book


fun main() {

    SearchFilterBuilder(Book::class.java).where() {
        Book::name.`is`("Title")
    }

}