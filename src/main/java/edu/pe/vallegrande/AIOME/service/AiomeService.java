package edu.pe.vallegrande.AIOME.service;

import edu.pe.vallegrande.AIOME.model.Aiome;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AiomeService {
    Flux<Aiome> findAllActive();
    Flux<Aiome> findByAitype(String aitype);
    Flux<Aiome> findByStatus(String status);
    Mono<Aiome> findById(Integer id);
    Mono<Aiome> create(String question, String aitype);
    Mono<Aiome> update(Integer id, String question, String response);
    Mono<Void> softDelete(Integer id);
    Mono<Void> restore(Integer id);
    Mono<Void> permanentDelete(Integer id);
}