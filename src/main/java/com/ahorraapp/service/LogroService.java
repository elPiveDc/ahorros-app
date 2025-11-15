package com.ahorraapp.service;

import com.ahorraapp.dto.logro.LogroDTO;
import com.ahorraapp.model.Gasto;
import com.ahorraapp.model.Meta;
import com.ahorraapp.model.Usuario;

import java.util.List;

public interface LogroService {

    List<LogroDTO> listarLogrosUsuario();

    // para logros manuales (admin)
    LogroDTO crearLogroManual(LogroDTO dto);

    // automatización
    void procesarLogrosPorGasto(Usuario usuario, Gasto gasto);

    void procesarLogrosPorMeta(Usuario usuario, Meta meta);

    boolean logroYaObtenido(Usuario usuario, String nombreLogro);
}
