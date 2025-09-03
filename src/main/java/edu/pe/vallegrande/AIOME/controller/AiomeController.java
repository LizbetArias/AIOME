package edu.pe.vallegrande.AIOME.controller;

import edu.pe.vallegrande.AIOME.model.Aiome;
import edu.pe.vallegrande.AIOME.service.AiomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/aiome")
@RequiredArgsConstructor
@Tag(name = "AIOME Controller", description = "Controlador para gestionar preguntas y respuestas de AI")
public class AiomeController {

    private final AiomeService aiomeService;

    @GetMapping
    @Operation(summary = "Obtener todos los registros activos")
    public Flux<Aiome> getAllActive() {
        return aiomeService.findAllActive();
    }

    @GetMapping("/aitype/{aitype}")
    @Operation(summary = "Listar por tipo de AI")
    public Flux<Aiome> getByAitype(@PathVariable String aitype) {
        return aiomeService.findByAitype(aitype.toUpperCase());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar por estado")
    public Flux<Aiome> getByStatus(@PathVariable String status) {
        return aiomeService.findByStatus(status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener por ID")
    public Mono<Aiome> getById(@PathVariable Integer id) {
        return aiomeService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Crear nueva pregunta")
    public Mono<Aiome> create(@RequestParam String question,
                              @RequestParam String aitype) {
        return aiomeService.create(question, aitype);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar pregunta y respuesta")
    public Mono<Aiome> update(@PathVariable Integer id,
                              @RequestParam String question,
                              @RequestParam String response) {
        return aiomeService.update(id, question, response);
    }

    @DeleteMapping("/soft/{id}")
    @Operation(summary = "Eliminado lógico (status = 'I')")
    public Mono<Void> softDelete(@PathVariable Integer id) {
        return aiomeService.softDelete(id);
    }

    @PatchMapping("/restore/{id}")
    @Operation(summary = "Restaurar registro (status = 'A')")
    public Mono<Void> restore(@PathVariable Integer id) {
        return aiomeService.restore(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminado físico")
    public Mono<Void> permanentDelete(@PathVariable Integer id) {
        return aiomeService.permanentDelete(id);
    }
}