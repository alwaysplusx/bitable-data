package com.harmony.bitable.example

import com.harmony.bitable.oapi.stream
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = [
        "debug=true",
        "bitable.client.app-id=xx",
        "bitable.client.app-secret=xx",
        "bitable.app-token=xx"
    ]
)
class BookApplicationTests {

    @Autowired
    lateinit var bookRepository: BookRepository

    @Test
    fun test() {
        bookRepository.scan().stream().forEach {
            println("Book: id=${it.id} name=${it.name}")
        }
    }

}
