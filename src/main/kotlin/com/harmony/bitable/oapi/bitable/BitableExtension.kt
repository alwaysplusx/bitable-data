// auto codegen
package com.harmony.bitable.oapi.bitable

import com.harmony.bitable.oapi.PageCursor
import com.harmony.bitable.oapi.PageSlice
import com.harmony.bitable.oapi.SimplePageSlice
import com.harmony.bitable.oapi.ensurePage
import com.harmony.bitable.utils.PageUtils.scan

import com.lark.oapi.core.request.RequestOptions

import com.lark.oapi.service.bitable.v1.model.AppTable
import com.lark.oapi.service.bitable.v1.model.AppTableFieldForList
import com.lark.oapi.service.bitable.v1.model.AppTableFormField
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.AppTableView
import com.lark.oapi.service.bitable.v1.model.ListAppTableFieldReq
import com.lark.oapi.service.bitable.v1.model.ListAppTableFieldRespBody
import com.lark.oapi.service.bitable.v1.model.ListAppTableFormFieldReq
import com.lark.oapi.service.bitable.v1.model.ListAppTableFormFieldRespBody
import com.lark.oapi.service.bitable.v1.model.ListAppTableRecordReq
import com.lark.oapi.service.bitable.v1.model.ListAppTableRecordRespBody
import com.lark.oapi.service.bitable.v1.model.ListAppTableReq
import com.lark.oapi.service.bitable.v1.model.ListAppTableRespBody
import com.lark.oapi.service.bitable.v1.model.ListAppTableViewReq
import com.lark.oapi.service.bitable.v1.model.ListAppTableViewRespBody

fun com.lark.oapi.service.bitable.v1.resource.AppTable.listCursor(
    req: ListAppTableReq,
    options: RequestOptions = RequestOptions(),
): PageCursor<AppTable> {
    return scan(req.pageSize, req.pageToken) { pageable ->
        req.pageToken = pageable.pageToken
        req.pageSize = pageable.pageSize
        this.list(req, options).ensurePage { it.toPageSlice() }
    }
}

fun com.lark.oapi.service.bitable.v1.resource.AppTableField.listCursor(
    req: ListAppTableFieldReq,
    options: RequestOptions = RequestOptions(),
): PageCursor<AppTableFieldForList> {
    return scan(req.pageSize, req.pageToken) { pageable ->
        req.pageToken = pageable.pageToken
        req.pageSize = pageable.pageSize
        this.list(req, options).ensurePage { it.toPageSlice() }
    }
}

fun com.lark.oapi.service.bitable.v1.resource.AppTableFormField.listCursor(
    req: ListAppTableFormFieldReq,
    options: RequestOptions = RequestOptions(),
): PageCursor<AppTableFormField> {
    return scan(req.pageSize, req.pageToken) { pageable ->
        req.pageToken = pageable.pageToken
        req.pageSize = pageable.pageSize
        this.list(req, options).ensurePage { it.toPageSlice() }
    }
}

fun com.lark.oapi.service.bitable.v1.resource.AppTableRecord.listCursor(
    req: ListAppTableRecordReq,
    options: RequestOptions = RequestOptions(),
): PageCursor<AppTableRecord> {
    return scan(req.pageSize, req.pageToken) { pageable ->
        req.pageToken = pageable.pageToken
        req.pageSize = pageable.pageSize
        this.list(req, options).ensurePage { it.toPageSlice() }
    }
}

fun com.lark.oapi.service.bitable.v1.resource.AppTableView.listCursor(
    req: ListAppTableViewReq,
    options: RequestOptions = RequestOptions(),
): PageCursor<AppTableView> {
    return scan(req.pageSize, req.pageToken) { pageable ->
        req.pageToken = pageable.pageToken
        req.pageSize = pageable.pageSize
        this.list(req, options).ensurePage { it.toPageSlice() }
    }
}


fun ListAppTableRespBody.toPageSlice(): PageSlice<AppTable> {
    return SimplePageSlice(
        items = this.items.toList(),
        nextToken = this.pageToken,
        hasNextSlice = this.hasMore,
        total = this.total
    )
}

fun ListAppTableFieldRespBody.toPageSlice(): PageSlice<AppTableFieldForList> {
    return SimplePageSlice(
        items = this.items.toList(),
        nextToken = this.pageToken,
        hasNextSlice = this.hasMore,
        total = this.total
    )
}

fun ListAppTableFormFieldRespBody.toPageSlice(): PageSlice<AppTableFormField> {
    return SimplePageSlice(
        items = this.items.toList(),
        nextToken = this.pageToken,
        hasNextSlice = this.hasMore,
        total = this.total
    )
}

fun ListAppTableRecordRespBody.toPageSlice(): PageSlice<AppTableRecord> {
    return SimplePageSlice(
        items = this.items.toList(),
        nextToken = this.pageToken,
        hasNextSlice = this.hasMore,
        total = this.total
    )
}

fun ListAppTableViewRespBody.toPageSlice(): PageSlice<AppTableView> {
    return SimplePageSlice(
        items = this.items.toList(),
        nextToken = this.pageToken,
        hasNextSlice = this.hasMore,
        total = this.total
    )
}
