package com.aquiris.miniredis.entity;

import javax.persistence.*;

@Entity
@Table(name = "z_element")
public class ZElement {

    @Id
    @Column(name = "member", nullable = false)
    private String member;

    @Column(name = "score")
    private Long score;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "sorted_set_key", nullable = false)
    private SortedSet sorted_set;

    public SortedSet getSorted_set() {
        return sorted_set;
    }

    public void setSorted_set(SortedSet sorted_set) {
        this.sorted_set = sorted_set;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}