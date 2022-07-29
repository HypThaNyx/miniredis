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
    @JoinColumn(name = "sortedSet_key", nullable = false)
    @JsonBackReference
    private SortedSet sortedSet;

    public SortedSet getSortedSet() {
        return sortedSet;
    }

    public void setSortedSet(SortedSet sortedSet) {
        this.sortedSet = sortedSet;
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
    public int hashCode() {
        return 31;
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
            return other.member == null;
        } else return member.equals(other.member);
    }

    @Override
    public String toString() {
        return "{ \"score\": " + score + ", \"member\": \"" + member + "\" }";
    }
}