package com.aquiris.miniredis.repository;

import com.aquiris.miniredis.entity.SortedSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SortedSetRepository extends JpaRepository<SortedSet, String> {

}
