package com.aquiris.miniredis.entity;


import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Table(name = "sorted_set")
public class SortedSet extends Ledger {

    public SortedSet(String chave, List<ZElement> elementos) {
        this.chave = chave;
        this.elementos = elementos;
    }
    @OneToMany(mappedBy = "sorted_set")
    private List<ZElement> elementos = new ArrayList<ZElement>();

    public List<ZElement> getElementos() {
        return elementos;
    }

    public void setElementos(List<ZElement> elementos) {
        elementos.forEach(elemento -> elemento.setSorted_set(this));
        this.elementos = elementos;
    }

    public void addToElementos(ZElement elemento) {
        elemento.setSorted_set(this);
        this.elementos.add(elemento);
    }

}