package com.harmony.bitable.oapi.filter

import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReqBody

/**
 * @author wuxin
 */
interface RecordSearchCustomizer {

    fun customize(requestBuilder: SearchAppTableRecordReq.Builder, bodyBuilder: SearchAppTableRecordReqBody.Builder)

}