package com.harmony.bitable.example

import com.harmony.bitable.example.domain.Book
import com.harmony.bitable.repository.BitableRepository
import com.harmony.bitable.repository.config.EnableBitableRepositories
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Repository

@EnableBitableRepositories
@SpringBootApplication
class BookApplication

fun main(vararg args: String) {
    runApplication<BookApplication>(*args)
}

@Repository
interface BookRepository : BitableRepository<Book, String>
