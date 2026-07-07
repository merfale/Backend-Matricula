package com.depazsotelo.matricula.services;

import com.depazsotelo.matricula.models.AnioAcademico;
import com.depazsotelo.matricula.models.Concepto;
import com.depazsotelo.matricula.repositories.AnioAcademicoRepository;
import com.depazsotelo.matricula.repositories.ConceptoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConceptoService {

    private final ConceptoRepository conceptoRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;

    @Transactional(rollbackFor = Exception.class)
    public List<Concepto> clonarConceptos(Integer codAnioOrigen, Integer codAnioDestino) throws Exception {

        if (codAnioOrigen.equals(codAnioDestino)) {
            throw new Exception("El año de origen y destino no pueden ser el mismo.");
        }

        AnioAcademico anioOrigen = anioAcademicoRepository.findById(codAnioOrigen)
                .orElseThrow(() -> new Exception("Año académico de origen no encontrado"));
        AnioAcademico anioDestino = anioAcademicoRepository.findById(codAnioDestino)
                .orElseThrow(() -> new Exception("Año académico de destino no encontrado"));

        List<Concepto> conceptosOrigen = conceptoRepository
                .findByAnioAcademicoCodAnioAcademicoOrderByOrdenPagoAsc(codAnioOrigen);

        if (conceptosOrigen.isEmpty()) {
            throw new Exception("El año " + anioOrigen.getAnio() + " no tiene conceptos para clonar.");
        }

        List<String> nombresYaExistentes = conceptoRepository
                .findByAnioAcademicoCodAnioAcademicoOrderByOrdenPagoAsc(codAnioDestino)
                .stream()
                .map(Concepto::getNombreConcepto)
                .toList();

        List<Concepto> nuevos = new ArrayList<>();

        for (Concepto original : conceptosOrigen) {
            if (nombresYaExistentes.contains(original.getNombreConcepto())) {
                continue;
            }

            Concepto clon = new Concepto();
            clon.setAnioAcademico(anioDestino);
            clon.setTipoConcepto(original.getTipoConcepto());
            clon.setNombreConcepto(original.getNombreConcepto());
            clon.setMonto(original.getMonto());
            clon.setOrdenPago(original.getOrdenPago());
            clon.setObligatorio(original.getObligatorio());
            clon.setEstado(true);

            nuevos.add(conceptoRepository.save(clon));
        }

        if (nuevos.isEmpty()) {
            throw new Exception("Todos los conceptos del año " + anioOrigen.getAnio()
                    + " ya existen en el año " + anioDestino.getAnio() + ". No se clonó nada.");
        }

        return nuevos;
    }
}