package com.harmony.bitable

import com.google.gson.reflect.TypeToken
import com.harmony.bitable.oapi.BitableApi
import com.harmony.bitable.oapi.bitable.BitableApiImpl
import com.harmony.bitable.oapi.ratelimiter.RateLimiterInvocationHandler
import com.lark.oapi.Client
import com.lark.oapi.core.utils.Jsons
import com.lark.oapi.service.bitable.v1.model.Attachment
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import org.junit.jupiter.api.Test
import java.lang.reflect.Proxy

/**
 * @author wuxin
 */
fun main() {
    val text = """
        [
              {
                "file_token": "ReOKbxxe1om20rxELYscOo07nOb",
                "name": "alipay_record_20250209_235300.csv",
                "size": 188568,
                "tmp_url": "https://open.feishu.cn/open-apis/drive/v1/medias/batch_get_tmp_download_url?file_tokens=ReOKbxxe1om20rxELYscOo07nOb",
                "type": "text/csv; charset=utf-8",
                "url": "https://open.feishu.cn/open-apis/drive/v1/medias/ReOKbxxe1om20rxELYscOo07nOb/download"
              }
            ]
    """.trimIndent()
    val value = Jsons.DEFAULT.fromJson(text, List::class.java)
    println(value)

    val valueAsJsonElement = Jsons.DEFAULT.toJsonTree(value)
    val result0 = Jsons.DEFAULT.fromJson(valueAsJsonElement, TypeToken.getArray(Attachment::class.java))
    val result1 = Jsons.DEFAULT.fromJson(valueAsJsonElement, object : TypeToken<List<Attachment>>() {})
    println(result0)
}
