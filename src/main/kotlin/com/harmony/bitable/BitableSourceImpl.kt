package com.harmony.bitable

import com.harmony.bitable.oapi.BitableApi


class BitableSourceImpl(
    private val appToken: String,
    private val bitableApi: BitableApi,
) : BitableSource {

    override fun getBitable(name: String) = bitableApi.getBitable(appToken, name)

}
