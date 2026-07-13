package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    List<Pago> findByCuotaCodCuota(Integer codCuota);

    Optional<Pago> findByRecibo(String recibo);

    List<Pago> findByCuota_Matricula_CodMatricula(Integer codMatricula);

    List<Pago> findByCuota_Matricula_Alumno_CodAlumno(Integer codAlumno);

    List<Pago> findByCuota_Matricula_AnioAcademico_CodAnioAcademico(Integer codAnioAcademico);

    List<Pago> findByUsuarioRegistroIdUsuario(Integer idUsuario);

    List<Pago> findByMetodoPago(String metodoPago);

    List<Pago> findByFechaPagoBetween(LocalDateTime desde, LocalDateTime hasta);
}