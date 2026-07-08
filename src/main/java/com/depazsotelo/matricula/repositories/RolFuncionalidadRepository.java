package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.RolFuncionalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RolFuncionalidadRepository extends JpaRepository<RolFuncionalidad, Integer> {

    List<RolFuncionalidad> findByRolIdRol(Integer idRol);


    Optional<RolFuncionalidad> findByRolIdRolAndFuncionalidadIdFuncionalidad(Integer idRol, Integer idFuncionalidad);
}