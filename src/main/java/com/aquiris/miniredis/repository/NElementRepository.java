package com.aquiris.miniredis.repository;

import com.aquiris.miniredis.entity.NElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NElementRepository extends JpaRepository<NElement, String> {

}
