package com.harmony.bitable.oapi.filter

import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReqBody

/**
 * @author wuxin
 */
interface RecordSearchCustomizer {

    companion object {
        val NoOpCustomizer: RecordSearchCustomizer = object : RecordSearchCustomizer {
            override fun customize(req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) {
            }
        }
    }

    fun customize(req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder)

}