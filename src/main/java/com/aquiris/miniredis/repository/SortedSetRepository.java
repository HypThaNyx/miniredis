package com.aquiris.miniredis.repository;

import com.aquiris.miniredis.entity.SortedSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SortedSetRepository extends JpaRepository<SortedSet, String> {

}
