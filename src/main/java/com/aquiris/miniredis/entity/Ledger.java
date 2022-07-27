package com.aquiris.miniredis.entity;

import javax.persistence.*;

@MappedSuperclass
public abstract class Ledger {
    @Id
    @Column(name = "chave", nullable = false)
    protected String chave;

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }
}