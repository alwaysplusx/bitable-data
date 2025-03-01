package com.harmony.bitable;

import com.harmony.bitable.annotations.BitId;
import com.harmony.bitable.annotations.Bitdex;
import com.harmony.bitable.annotations.Bitfield;

public class Author {

    @BitId
    @Bitdex
    private String id;

    @Bitfield("作者")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
