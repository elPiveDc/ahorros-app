package com.ahorraapp.repository;

import com.ahorraapp.model.UsuarioCosmetico;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioCosmeticoRepository extends JpaRepository<UsuarioCosmetico, Long> {
    List<UsuarioCosmetico> findByUsuarioIdUsuario(Long idUsuario);

    boolean existsByUsuarioIdUsuarioAndCosmeticoIdCosmetico(Long idUsuario, Long idCosmetico);

}
