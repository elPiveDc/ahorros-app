package com.ahorraapp.repository;

import com.ahorraapp.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByUsuarioIdUsuario(Long idUsuario);

    List<Gasto> findByUsuarioIdUsuarioOrderByFechaRegistroDesc(Long idUsuario);

    // dia
    @Query("SELECT g FROM Gasto g WHERE g.usuario.idUsuario = :id AND DATE(g.fechaRegistro) = :dia ORDER BY g.fechaRegistro DESC")
    List<Gasto> findByDia(Long id, LocalDate dia);

    // SEMANA
    @Query("SELECT g FROM Gasto g WHERE g.usuario.idUsuario = :id AND g.fechaRegistro BETWEEN :inicio AND :fin ORDER BY g.fechaRegistro DESC")
    List<Gasto> findByRango(Long id, LocalDateTime inicio, LocalDateTime fin);

    // mes
    @Query("SELECT g FROM Gasto g WHERE g.usuario.idUsuario = :id AND MONTH(g.fechaRegistro) = :mes AND YEAR(g.fechaRegistro) = :anio ORDER BY g.fechaRegistro DESC")
    List<Gasto> findByMes(Long id, int mes, int anio);

    // Necesario para LogroServiceImpl (primer gasto)
    long countByUsuarioIdUsuario(Long idUsuario);

    // Para contar gastos multimedia (foto o voz)
    long countByUsuarioIdUsuarioAndTipoRegistroIn(Long idUsuario, List<String> tipos);

    // Chequear si ya tiene gasto multimedia
    boolean existsByUsuarioIdUsuarioAndTipoRegistroIgnoreCase(Long idUsuario, String tipoRegistro);

}
