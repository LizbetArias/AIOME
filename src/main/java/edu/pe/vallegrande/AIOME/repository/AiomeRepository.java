package edu.pe.vallegrande.AIOME.repository;

import edu.pe.vallegrande.AIOME.model.Aiome;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AiomeRepository extends ReactiveCrudRepository<Aiome, Integer> {

    @Query("SELECT * FROM aiome WHERE aitype = :aitype AND status = 'A'")
    Flux<Aiome> findByAitype(String aitype);

    @Query("SELECT * FROM aiome WHERE status = :status")
    Flux<Aiome> findByStatus(String status);

    @Query("UPDATE aiome SET status = 'A' WHERE id = :id")
    Mono<Integer> restoreById(Integer id);

    @Query("UPDATE aiome SET status = 'I' WHERE id = :id")
    Mono<Integer> softDeleteById(Integer id);

    @Query("UPDATE aiome SET question = :question, response = :response WHERE id = :id")
    Mono<Integer> updateQuestionAndResponse(Integer id, String question, String response);
}