package com.ahorraapp.repository;

import com.ahorraapp.model.Cosmetico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CosmeticoRepository extends JpaRepository<Cosmetico, Long> {

}
