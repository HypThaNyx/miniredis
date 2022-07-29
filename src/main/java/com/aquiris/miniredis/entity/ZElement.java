package com.aquiris.miniredis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "z_element")
public class ZElement {

    @Id
    @Column(name = "member", nullable = false)
    @JsonProperty("member")
    private String member;

    @Column(name = "score")
    @JsonProperty("score")
    private Long score;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sorted_set_key", nullable = false)
    @JsonBackReference
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ZElement other = (ZElement) obj;
        if (member == null) {
            if (other.member != null)
                return false;
        } else if (!member.equals(other.member))
            return false;
        return true;
    }
}