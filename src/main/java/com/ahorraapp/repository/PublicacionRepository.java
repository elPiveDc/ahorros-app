package com.ahorraapp.repository;

import com.ahorraapp.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    List<Publicacion> findByUsuarioIdUsuario(Long idUsuario);

    List<Publicacion> findAllByOrderByFechaPublicacionDesc();
}
