package com.ahorraapp.service.impl;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ahorraapp.dto.tienda.CosmeticoDTO;
import com.ahorraapp.model.Cosmetico;
import com.ahorraapp.model.Usuario;
import com.ahorraapp.model.UsuarioCosmetico;
import com.ahorraapp.repository.CosmeticoRepository;
import com.ahorraapp.repository.UsuarioCosmeticoRepository;
import com.ahorraapp.repository.UsuarioRepository;
import com.ahorraapp.service.CosmeticoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CosmeticoServiceImpl implements CosmeticoService {

    private final CosmeticoRepository cosmeticoRepo;
    private final UsuarioCosmeticoRepository usuarioCosRepo;
    private final UsuarioRepository usuarioRepo;

    @Override
    public List<CosmeticoDTO> listarCosmeticos() {
        return cosmeticoRepo.findAll().stream().map(this::mapToDto).toList();
    }

    @Override
    public String aplicarCosmetico(Long idCosmetico) {
        Usuario usuario = obtenerUsuario();

        UsuarioCosmetico uc = usuarioCosRepo.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .filter(x -> x.getCosmetico().getIdCosmetico().equals(idCosmetico))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No tienes este cosmético"));

        // desactivar otros
        usuarioCosRepo.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .forEach(c -> c.setAplicado(false));

        uc.setAplicado(true);
        usuarioCosRepo.save(uc);

        return "Cosmético aplicado";
    }

    @Override
    public List<CosmeticoDTO> misCosmeticos() {
        Usuario usuario = obtenerUsuario();

        return usuarioCosRepo.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(c -> mapToDto(c.getCosmetico()))
                .toList();
    }

    private Usuario obtenerUsuario() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepo.findByCorreo(correo).orElseThrow();
    }

    private CosmeticoDTO mapToDto(Cosmetico c) {
        return CosmeticoDTO.builder()
                .idCosmetico(c.getIdCosmetico())
                .nombre(c.getNombre())
                .tipo(c.getTipo())
                .archivoUrl(c.getArchivoUrl())
                .build();
    }
}
