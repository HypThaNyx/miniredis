package com.aquiris.miniredis.entity;

import javax.persistence.*;

@MappedSuperclass
public abstract class Ledger {
    @Id
    @Column(name = "key", nullable = false)
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}