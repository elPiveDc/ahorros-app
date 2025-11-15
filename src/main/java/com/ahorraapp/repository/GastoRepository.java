package com.ahorraapp.repository;

import com.ahorraapp.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByUsuarioIdUsuario(Long idUsuario);

    List<Gasto> findByUsuarioIdUsuarioOrderByFechaRegistroDesc(Long idUsuario);

    // Necesario para LogroServiceImpl (primer gasto)
    long countByUsuarioIdUsuario(Long idUsuario);

    // Para contar gastos multimedia (foto o voz)
    long countByUsuarioIdUsuarioAndTipoRegistroIn(Long idUsuario, List<String> tipos);

    // Chequear si ya tiene gasto multimedia
    boolean existsByUsuarioIdUsuarioAndTipoRegistroIgnoreCase(Long idUsuario, String tipoRegistro);

}
