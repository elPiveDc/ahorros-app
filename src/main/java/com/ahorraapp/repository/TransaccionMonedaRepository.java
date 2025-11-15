package com.ahorraapp.repository;

import com.ahorraapp.model.TransaccionMoneda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransaccionMonedaRepository extends JpaRepository<TransaccionMoneda, Long> {

    List<TransaccionMoneda> findByUsuarioIdUsuario(Long idUsuario);

    List<TransaccionMoneda> findByTipoAndUsuarioIdUsuario(String tipo, Long idUsuario);
}
