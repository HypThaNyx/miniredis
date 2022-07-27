package com.aquiris.miniredis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "n_element")
public class NElement extends Ledger {
    @Column(name = "valor")
    private String valor;

    public NElement() {
    }

    public NElement(String chave, String valor){
        this.chave = chave;
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}