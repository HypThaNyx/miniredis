package com.aquiris.miniredis.entity;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "sorted_set")
public class SortedSet extends Ledger {
    @OneToMany(mappedBy = "sorted_set")
    private List<ZElement> elements;

    public List<ZElement> getElements() {
        return elements;
    }

    public void setElements(List<ZElement> elements) {
        this.elements = elements;
    }
}