package com.depazsotelo.matricula.services;

import com.depazsotelo.matricula.dtos.AplicarPermisosRequest;
import com.depazsotelo.matricula.dtos.PermisoItemRequest;
import com.depazsotelo.matricula.models.*;
import com.depazsotelo.matricula.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermisoService {

    private final RolFuncionalidadRepository rolFuncionalidadRepository;
    private final RolRepository rolRepository;
    private final FuncionalidadRepository funcionalidadRepository;
    private final UsuarioRepository usuarioRepository;


    @Transactional
    public void aplicarPermisos(AplicarPermisosRequest request) throws Exception {
        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new Exception("Rol no encontrado"));

        for (PermisoItemRequest item : request.getPermisos()) {
            Funcionalidad funcionalidad = funcionalidadRepository.findById(item.getIdFuncionalidad())
                    .orElseThrow(() -> new Exception("Funcionalidad no encontrada: " + item.getIdFuncionalidad()));


            RolFuncionalidad rf = rolFuncionalidadRepository
                    .findByRolIdRolAndFuncionalidadIdFuncionalidad(rol.getIdRol(), funcionalidad.getIdFuncionalidad())
                    .orElse(new RolFuncionalidad());

            rf.setRol(rol);
            rf.setFuncionalidad(funcionalidad);
            rf.setVer(item.getVer());
            rf.setCrear(item.getCrear());
            rf.setEditar(item.getEditar());
            rf.setEliminar(item.getEliminar());
            rf.setImprimir(item.getImprimir());

            rolFuncionalidadRepository.save(rf);
        }
    }

    // MEJORA: usado por el @PreAuthorize para validar permisos finos por checkbox
    public boolean tienePermiso(String username, String nombreFuncionalidad, String accion) {
        Usuario usuario = usuarioRepository.findByUsuario(username).orElse(null);
        if (usuario == null) return false;


        if ("SUPERUSUARIO".equalsIgnoreCase(usuario.getRol().getNombreRol())) return true;

        return rolFuncionalidadRepository.findByRolIdRol(usuario.getRol().getIdRol()).stream()
                .filter(rf -> rf.getFuncionalidad().getNombre().equalsIgnoreCase(nombreFuncionalidad))
                .findFirst()
                .map(rf -> switch (accion.toLowerCase()) {
                    case "ver" -> rf.getVer();
                    case "crear" -> rf.getCrear();
                    case "editar" -> rf.getEditar();
                    case "eliminar" -> rf.getEliminar();
                    case "imprimir" -> rf.getImprimir();
                    default -> false;
                })
                .orElse(false);
    }
}