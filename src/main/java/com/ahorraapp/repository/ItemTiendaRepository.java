package com.ahorraapp.repository;

import com.ahorraapp.model.ItemTienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemTiendaRepository extends JpaRepository<ItemTienda, Long> {

}
