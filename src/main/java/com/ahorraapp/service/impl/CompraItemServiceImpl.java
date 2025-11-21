package com.ahorraapp.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ahorraapp.dto.tienda.CompraItemDTO;
import com.ahorraapp.dto.tienda.ItemTiendaDTO;
import com.ahorraapp.model.CompraItem;
import com.ahorraapp.model.ItemTienda;
import com.ahorraapp.model.TransaccionMoneda;
import com.ahorraapp.model.Usuario;
import com.ahorraapp.repository.CompraItemRepository;
import com.ahorraapp.repository.ItemTiendaRepository;
import com.ahorraapp.repository.TransaccionMonedaRepository;
import com.ahorraapp.repository.UsuarioRepository;
import com.ahorraapp.service.CompraItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompraItemServiceImpl implements CompraItemService {

    private final ItemTiendaRepository itemRepo;
    private final CompraItemRepository compraRepo;
    private final UsuarioRepository usuarioRepo;
    private final TransaccionMonedaRepository transRepo;

    @Override
    public List<CompraItemDTO> listarMisCompras() {
        Usuario usuario = obtenerUsuario();

        return compraRepo.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::mapCompraToDto)
                .toList();
    }

    @Override
    public CompraItemDTO comprarItem(Long idItem) {

        Usuario usuario = obtenerUsuario();
        ItemTienda item = itemRepo.findById(idItem)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        // 1) verificar si ya fue comprado
        if (compraRepo.existsByUsuarioIdUsuarioAndItemIdItem(usuario.getIdUsuario(), idItem)) {
            throw new RuntimeException("Ya compraste este item");
        }

        // 2) verificar monedas
        if (usuario.getMonedas() < item.getCosto()) {
            throw new RuntimeException("No tienes monedas suficientes");
        }

        // 3) descontar monedas
        usuario.setMonedas(usuario.getMonedas() - item.getCosto());
        usuarioRepo.save(usuario);

        // 4) registrar compra
        CompraItem compra = CompraItem.builder()
                .usuario(usuario)
                .item(item)
                .costoPagado(item.getCosto())
                .fechaCompra(LocalDateTime.now())
                .build();

        compraRepo.save(compra);

        // 5) registrar transacción
        TransaccionMoneda t = TransaccionMoneda.builder()
                .usuario(usuario)
                .tipo("gasto")
                .motivo("Compra item: " + item.getNombre())
                .cantidad(item.getCosto())
                .fecha(LocalDateTime.now())
                .build();

        transRepo.save(t);

        return mapCompraToDto(compra);
    }

    private Usuario obtenerUsuario() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepo.findByCorreo(correo).orElseThrow();
    }

    private ItemTiendaDTO mapItemToDto(ItemTienda item) {
        return ItemTiendaDTO.builder()
                .idItem(item.getIdItem())
                .nombre(item.getNombre())
                .descripcion(item.getDescripcion())
                .costo(item.getCosto())
                .tipo(item.getTipo())
                .imagenUrl(item.getImagenUrl())
                .build();
    }

    private CompraItemDTO mapCompraToDto(CompraItem compra) {
        return CompraItemDTO.builder()
                .idCompra(compra.getIdCompra())
                .costoPagado(compra.getCostoPagado())
                .fechaCompra(compra.getFechaCompra())
                .item(mapItemToDto(compra.getItem()))
                .build();
    }
}
