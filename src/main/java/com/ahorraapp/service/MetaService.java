package com.ahorraapp.service;

import com.ahorraapp.dto.meta.MetaCrearDTO;
import com.ahorraapp.dto.meta.MetaDTO;

import java.util.List;

public interface MetaService {

    MetaDTO crearMeta(MetaCrearDTO dto);

    List<MetaDTO> listarMetasUsuario();

    MetaDTO obtenerMeta(Long idMeta);

    MetaDTO actualizarMeta(Long idMeta, MetaCrearDTO dto);

    void eliminarMeta(Long idMeta);

    MetaDTO procesarProgresoPorGasto(Long idGasto); // <<< IMPORTANTE

    MetaDTO marcarComoCumplida(Long idMeta);
}
