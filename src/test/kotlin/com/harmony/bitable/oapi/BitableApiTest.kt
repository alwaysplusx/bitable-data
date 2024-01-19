package com.harmony.bitable.oapi

import com.harmony.bitable.oapi.bitable.BitableApi
import com.lark.oapi.Client
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author wuxin
 */
class BitableApiTest {

    private lateinit var bitableApi: BitableApi
    private lateinit var client: Client

    @BeforeEach
    fun setUp() {
        this.client = LarkClientBuilder()
            .setAppId("cli_9f87e42e7c72900e")
            .setAppSecret("xx")
            .build()
        this.bitableApi = BitableApi(client)
    }

    @Test
    fun testGetTables() {
        val bitable = bitableApi.getBitable("F8jlbf6Q1aa108sxnDCcPZS2nge", "数据表")
        println(bitable)
    }

}


