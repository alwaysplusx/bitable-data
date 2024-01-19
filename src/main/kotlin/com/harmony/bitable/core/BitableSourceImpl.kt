package com.harmony.bitable.core

import com.harmony.bitable.oapi.bitable.BitableApi


class BitableSourceImpl(
    private val appToken: String,
    private val bitableApi: BitableApi,
) : BitableSource {

    override fun getTable(name: String) = bitableApi.getBitable(appToken, name)

}
