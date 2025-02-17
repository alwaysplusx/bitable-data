package com.harmony.bitable.repository

import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReqBody

/**
 * @author wuxin
 */
interface FilterCustomizer {

    companion object {
        val NoOpCustomizer: FilterCustomizer = object : FilterCustomizer {
            override fun customize(req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) {
            }
        }
    }

    fun customize(req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder)

}