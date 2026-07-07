package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsuario(String usuario);
    List<Usuario> findByRolIdRol(Integer idRol);
}