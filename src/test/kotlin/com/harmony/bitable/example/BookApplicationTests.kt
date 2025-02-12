package com.harmony.bitable.example

import com.harmony.bitable.oapi.cursor.steamOfElements
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = [
        "debug=true",
        "lark.client.app-id=xxx",
        "lark.client.app-secret=xxx",
        "bitable.app-token=xxx"
    ]
)
class BookApplicationTests {

    @Autowired
    lateinit var bookRepository: BookRepository

    @Test
    fun test() {
        bookRepository.scan().steamOfElements().forEach {
            println("Book: id=${it.id} name=${it.name}")
        }
    }

}
