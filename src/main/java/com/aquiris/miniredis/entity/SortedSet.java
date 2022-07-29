package com.aquiris.miniredis.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Table(name = "sorted_set")
public class SortedSet extends Ledger {

    @OneToMany(mappedBy = "sorted_set", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ZElement> elementos = new ArrayList<>();

    public SortedSet(String chave, List<ZElement> elementos) {
        this.chave = chave;
        setElementos(elementos);
    }

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