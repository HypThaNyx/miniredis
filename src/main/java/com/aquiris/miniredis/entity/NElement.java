package com.aquiris.miniredis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "n_element")
public class NElement extends Ledger {
    @Column(name = "valor")
    private String valor;

    @Column(name = "expiryDate")
    @JsonIgnore
    private LocalDateTime expiryDate;

    public NElement() {
    }

    public NElement(String chave, String valor) {
        this.chave = chave;
        this.valor = valor;
    }

    public NElement(String chave, String valor, LocalDateTime expiryDate) {
        this.chave = chave;
        this.valor = valor;
        this.expiryDate = expiryDate;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public int hashCode() {
        return 17;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NElement other = (NElement) obj;
        if (chave == null) {
            return other.chave == null;
        } else return chave.equals(other.chave);
    }

    @Override
    public String toString() {
        return "{ \"chave\": \"" + chave + "\", \"valor\": \"" + valor + "\" }";
    }
}