package com.harmony.bitable

import com.lark.oapi.service.bitable.v1.enums.SearchAppTableRecordUserIdTypeEnum
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = [
        "debug=true",
        "lark.client.app-id=xxx",
        "lark.client.app-secret=xxx",
        "bitable.app-token=xxx",
    ]
)
class BookApplicationTests {

    @Autowired
    lateinit var bookRepository: BookRepository

    @Test
    fun test() {
        bookRepository.search().streamOfElements().forEach {
            println("Book: id=${it.id} name=${it.name}")
        }
    }

    @Test
    fun testDsl() {
        bookRepository.search {
            select(Book::name)
            where {
                Book::name `is` "Spring Boot"
                Book::name isEmpty false
                and {
                    Book::price `is` 100.0
                    Book::name isNot "Spring Boot"
                }
            }
            paging(pageSize = 10, offset = "")
            desc(Book::name, Book::author)

            withCustomizer { req, _ ->
                req.userIdType(SearchAppTableRecordUserIdTypeEnum.USER_ID)
            }
        }.streamOfElements().forEach { println("Book: id=${it.id} name=${it.name}") }
    }

    @Test
    fun testSearch() {
        bookRepository.search {
            where {
                Book::author `is` "张三"
            }
        }.streamOfElements().forEach { println("Book: id=${it.id} name=${it.name}") }
    }

    @Test
    fun testUpdate() {
        bookRepository.updateById("recuEUJx0PgTWH") {
            set(Book::name, "《Spring Boot 3.0》")
        }
    }

}