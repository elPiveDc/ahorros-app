package com.ahorraapp.repository;

import com.ahorraapp.model.CompraItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraItemRepository extends JpaRepository<CompraItem, Long> {
    List<CompraItem> findByUsuarioIdUsuario(Long idUsuario);

    boolean existsByUsuarioIdUsuarioAndItemIdItem(Long idUsuario, Long idItem);

}
