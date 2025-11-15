package com.ahorraapp.repository;

import com.ahorraapp.model.Meta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Long> {

    List<Meta> findByUsuarioIdUsuario(Long idUsuario);

    List<Meta> findByCumplidaTrueAndUsuarioIdUsuario(Long idUsuario);
}
