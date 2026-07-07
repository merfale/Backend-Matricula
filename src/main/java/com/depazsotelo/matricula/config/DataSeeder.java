package com.depazsotelo.matricula.config;

import com.depazsotelo.matricula.models.*;
import com.depazsotelo.matricula.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;
    private final NivelRepository nivelRepository;
    private final GradoRepository gradoRepository;
    private final AulaRepository aulaRepository;
    private final TipoConceptoRepository tipoConceptoRepository;
    private final ConceptoRepository conceptoRepository;
    private final PasswordEncoder passwordEncoder;
    private final FuncionalidadRepository funcionalidadRepository;
    private final RolFuncionalidadRepository rolFuncionalidadRepository;

    @Override
    public void run(String... args) throws Exception {

        // 1. Sembrar Tipo de Documento
        if (tipoDocumentoRepository.count() == 0) {
            TipoDocumento dni = new TipoDocumento();
            dni.setNombre("DNI");
            dni.setEstado(true);
            tipoDocumentoRepository.save(dni);
            System.out.println("✅ Documento DNI creado (ID: 1)");
        }

        // 2. Sembrar Roles y Usuarios del sistema
        if (rolRepository.count() == 0) {

            // MEJORA: los 3 roles que exige el spec (antes solo existía "ADMIN")
            Rol rolSuperusuario = new Rol();
            rolSuperusuario.setNombreRol("SUPERUSUARIO");
            rolSuperusuario.setEstado(true);
            rolSuperusuario = rolRepository.save(rolSuperusuario);

            Rol rolDirector = new Rol();
            rolDirector.setNombreRol("DIRECTOR");
            rolDirector.setEstado(true);
            rolDirector = rolRepository.save(rolDirector);

            Rol rolSecretaria = new Rol();
            rolSecretaria.setNombreRol("SECRETARIA");
            rolSecretaria.setEstado(true);
            rolSecretaria = rolRepository.save(rolSecretaria);

            // Usuario Superusuario (acceso total, no se puede eliminar)
            Usuario superusuario = new Usuario();
            superusuario.setUsuario("admin");
            superusuario.setPassword(passwordEncoder.encode("admin123"));
            superusuario.setRol(rolSuperusuario);
            superusuario.setEstado(true);
            usuarioRepository.save(superusuario);
            System.out.println("✅ Usuario SUPERUSUARIO creado (ID: 1)");

            // Usuario Director (solo consulta)
            Usuario director = new Usuario();
            director.setUsuario("director");
            director.setPassword(passwordEncoder.encode("director123"));
            director.setRol(rolDirector);
            director.setEstado(true);
            usuarioRepository.save(director);
            System.out.println("✅ Usuario DIRECTOR creado (ID: 2)");

            // Usuario Secretaria (todas las operaciones)
            Usuario secretaria = new Usuario();
            secretaria.setUsuario("secretaria");
            secretaria.setPassword(passwordEncoder.encode("secretaria123"));
            secretaria.setRol(rolSecretaria);
            secretaria.setEstado(true);
            usuarioRepository.save(secretaria);
            System.out.println("✅ Usuario SECRETARIA creado (ID: 3)");
        }

        // 3. Sembrar Estructura de Aula (Año, Nivel, Grado)
        if (anioAcademicoRepository.count() == 0) {
            AnioAcademico anio2026 = new AnioAcademico();
            anio2026.setAnio("2026");
            anio2026.setEstado(true);
            anio2026 = anioAcademicoRepository.save(anio2026);

            Nivel secundaria = new Nivel();
            secundaria.setNombre("Secundaria");
            secundaria.setEstado(true);
            secundaria = nivelRepository.save(secundaria);

            Grado primero = new Grado();
            primero.setNombre("1° Grado");
            primero.setEstado(true);
            primero = gradoRepository.save(primero);

            // Creamos el Aula 1: 1° de Secundaria - Sección A - Año 2026
            Aula aula1 = new Aula();
            aula1.setAnioAcademico(anio2026);
            aula1.setNivel(secundaria);
            aula1.setGrado(primero);
            aula1.setSeccion("A");
            aula1.setCapacidadMaxima((short) 30);
            aula1.setEstado(true);
            aulaRepository.save(aula1);
            System.out.println("✅ Aula 1° Secundaria 'A' 2026 creada (ID: 1)");

            // 4. Sembrar Conceptos de Pago para este año académico
            TipoConcepto fijo = new TipoConcepto();
            fijo.setNombre("FIJO");
            fijo = tipoConceptoRepository.save(fijo);

            TipoConcepto mensual = new TipoConcepto();
            mensual.setNombre("MENSUAL");
            mensual = tipoConceptoRepository.save(mensual);

            // Concepto: Derecho de Matrícula (Se paga primero)
            Concepto matriculaConcepto = new Concepto();
            matriculaConcepto.setAnioAcademico(anio2026);
            matriculaConcepto.setTipoConcepto(fijo);
            matriculaConcepto.setNombreConcepto("Matrícula 2026");
            matriculaConcepto.setMonto(new BigDecimal("450.00"));
            matriculaConcepto.setOrdenPago((short) 1);
            matriculaConcepto.setObligatorio(true);
            conceptoRepository.save(matriculaConcepto);

            // Concepto: Pensión de Marzo (Se paga segundo)
            Concepto marzoConcepto = new Concepto();
            marzoConcepto.setAnioAcademico(anio2026);
            marzoConcepto.setTipoConcepto(mensual);
            marzoConcepto.setNombreConcepto("Pensión - Marzo");
            marzoConcepto.setMonto(new BigDecimal("600.00"));
            marzoConcepto.setOrdenPago((short) 2);
            marzoConcepto.setObligatorio(true);
            conceptoRepository.save(marzoConcepto);
            System.out.println("✅ Tarifario y Conceptos de Pago 2026 sembrados con éxito");
        }

        // 5. Sembrar Funcionalidades base + permisos por defecto para Director y Secretaria.
// Superusuario no necesita filas: PermisoService.tienePermiso() ya lo deja pasar siempre.
        if (funcionalidadRepository.count() == 0) {

            String[] nombresFuncionalidades = {
                    "Alumnos", "Aulas", "AniosAcademicos", "Conceptos", "TiposConcepto",
                    "TiposDocumento", "Grados", "Niveles", "Usuarios", "Roles",
                    "Funcionalidades", "Permisos", "Matriculas", "Cuotas", "Pagos", "Auditoria"
            };

            Rol rolDirector = rolRepository.findByNombreRol("DIRECTOR")
                    .orElseThrow(() -> new RuntimeException("Rol DIRECTOR no encontrado"));
            Rol rolSecretaria = rolRepository.findByNombreRol("SECRETARIA")
                    .orElseThrow(() -> new RuntimeException("Rol SECRETARIA no encontrado"));

            for (String nombre : nombresFuncionalidades) {
                Funcionalidad funcionalidad = new Funcionalidad();
                funcionalidad.setNombre(nombre);
                funcionalidad = funcionalidadRepository.save(funcionalidad);

                // DIRECTOR: solo consulta
                RolFuncionalidad permisoDirector = new RolFuncionalidad();
                permisoDirector.setRol(rolDirector);
                permisoDirector.setFuncionalidad(funcionalidad);
                permisoDirector.setVer(true);
                permisoDirector.setCrear(false);
                permisoDirector.setEditar(false);
                permisoDirector.setEliminar(false);
                permisoDirector.setImprimir(false);
                rolFuncionalidadRepository.save(permisoDirector);

                // SECRETARIA: todas las operaciones
                RolFuncionalidad permisoSecretaria = new RolFuncionalidad();
                permisoSecretaria.setRol(rolSecretaria);
                permisoSecretaria.setFuncionalidad(funcionalidad);
                permisoSecretaria.setVer(true);
                permisoSecretaria.setCrear(true);
                permisoSecretaria.setEditar(true);
                permisoSecretaria.setEliminar(true);
                permisoSecretaria.setImprimir(true);
                rolFuncionalidadRepository.save(permisoSecretaria);
            }

            System.out.println("✅ Funcionalidades y permisos por defecto (Director/Secretaria) sembrados con éxito");
        }
    }
}