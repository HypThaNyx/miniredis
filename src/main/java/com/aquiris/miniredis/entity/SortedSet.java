package com.aquiris.miniredis.entity;


import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    private List<ZElement> elementos;

    public List<ZElement> getElementos() {
        return elementos;
    }

    public void setElementos(List<ZElement> elementos) {
        this.elementos = elementos;
    }


}