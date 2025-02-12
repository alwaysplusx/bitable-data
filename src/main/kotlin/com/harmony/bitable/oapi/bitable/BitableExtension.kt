// auto codegen
package com.harmony.bitable.oapi.bitable

import com.harmony.bitable.oapi.cursor.PageCursor
import com.harmony.bitable.oapi.cursor.PageSlice
import com.harmony.bitable.oapi.cursor.SimplePageSlice
import com.harmony.bitable.oapi.ensurePage
import com.harmony.bitable.utils.PageUtils.scan

import com.lark.oapi.core.request.RequestOptions

import com.lark.oapi.service.bitable.v1.model.AppDashboard
import com.lark.oapi.service.bitable.v1.model.AppRole
import com.lark.oapi.service.bitable.v1.model.AppRoleMember
import com.lark.oapi.service.bitable.v1.model.AppTable
import com.lark.oapi.service.bitable.v1.model.AppTableFieldForList
import com.lark.oapi.service.bitable.v1.model.AppTableFormField
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.AppTableView
import com.lark.oapi.service.bitable.v1.model.ListAppDashboardReq
import com.lark.oapi.service.bitable.v1.model.ListAppDashboardRespBody
import com.lark.oapi.service.bitable.v1.model.ListAppRoleMemberReq
import com.lark.oapi.service.bitable.v1.model.ListAppRoleMemberRespBody
import com.lark.oapi.service.bitable.v1.model.ListAppRoleReq
import com.lark.oapi.service.bitable.v1.model.ListAppRoleRespBody
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
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordRespBody

fun com.lark.oapi.service.bitable.v1.resource.AppDashboard.listCursor(
    req: ListAppDashboardReq,
    options: RequestOptions = RequestOptions(),
): PageCursor<AppDashboard> {
    return scan(req.pageSize, req.pageToken) { pageable ->
        req.pageToken = pageable.pageToken
        req.pageSize = pageable.pageSize
        this.list(req, options).ensurePage { it.toPageSlice() }
    }
}

fun com.lark.oapi.service.bitable.v1.resource.AppRole.listCursor(
    req: ListAppRoleReq,
    options: RequestOptions = RequestOptions(),
): PageCursor<AppRole> {
    return scan(req.pageSize, req.pageToken) { pageable ->
        req.pageToken = pageable.pageToken
        req.pageSize = pageable.pageSize
        this.list(req, options).ensurePage { it.toPageSlice() }
    }
}

fun com.lark.oapi.service.bitable.v1.resource.AppRoleMember.listCursor(
    req: ListAppRoleMemberReq,
    options: RequestOptions = RequestOptions(),
): PageCursor<AppRoleMember> {
    return scan(req.pageSize, req.pageToken) { pageable ->
        req.pageToken = pageable.pageToken
        req.pageSize = pageable.pageSize
        this.list(req, options).ensurePage { it.toPageSlice() }
    }
}

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

fun com.lark.oapi.service.bitable.v1.resource.AppTableRecord.searchCursor(
    req: SearchAppTableRecordReq,
    options: RequestOptions = RequestOptions(),
): PageCursor<AppTableRecord> {
    return scan(req.pageSize, req.pageToken) { pageable ->
        req.pageToken = pageable.pageToken
        req.pageSize = pageable.pageSize
        this.search(req, options).ensurePage { it.toPageSlice() }
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


fun ListAppDashboardRespBody.toPageSlice(): PageSlice<AppDashboard> {
    return SimplePageSlice(
        items = this.dashboards.toList(),
        nextToken = this.pageToken,
        hasNextSlice = this.hasMore,
        total = -1
    )
}

fun ListAppRoleRespBody.toPageSlice(): PageSlice<AppRole> {
    return SimplePageSlice(
        items = this.items.toList(),
        nextToken = this.pageToken,
        hasNextSlice = this.hasMore,
        total = this.total
    )
}

fun ListAppRoleMemberRespBody.toPageSlice(): PageSlice<AppRoleMember> {
    return SimplePageSlice(
        items = this.items.toList(),
        nextToken = this.pageToken,
        hasNextSlice = this.hasMore,
        total = this.total
    )
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

fun SearchAppTableRecordRespBody.toPageSlice(): PageSlice<AppTableRecord> {
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

