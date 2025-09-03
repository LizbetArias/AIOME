package edu.pe.vallegrande.AIOME.service.impl;

import edu.pe.vallegrande.AIOME.model.Aiome;
import edu.pe.vallegrande.AIOME.repository.AiomeRepository;
import edu.pe.vallegrande.AIOME.service.AiomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiomeServiceImpl implements AiomeService {

    private final AiomeRepository aiomeRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${spring.ai.copilot.api-key}")
    private String copilotApiKey;

    @Value("${spring.ai.copilot.api-host}")
    private String copilotApiHost;

    @Value("${spring.ai.copilot.api-url}")
    private String copilotApiUrl;

    @Value("${spring.ai.gemini.api-key}")
    private String geminiApiKey;

    @Value("${spring.ai.gemini.api-url}")
    private String geminiApiUrl;

    @Override
    public Flux<Aiome> findAllActive() {
        return aiomeRepository.findByStatus("A");
    }

    @Override
    public Flux<Aiome> findByAitype(String aitype) {
        return aiomeRepository.findByAitype(aitype);
    }

    @Override
    public Flux<Aiome> findByStatus(String status) {
        return aiomeRepository.findByStatus(status);
    }

    @Override
    public Mono<Aiome> findById(Integer id) {
        return aiomeRepository.findById(id);
    }

    @Override
    public Mono<Aiome> create(String question, String aitype) {
        return getAiResponse(question, aitype)
                .flatMap(response -> {
                    Aiome aiome = new Aiome();
                    aiome.setQuestion(question);
                    aiome.setResponse(response);
                    aiome.setStatus("A");
                    aiome.setDate(LocalDateTime.now());
                    aiome.setAitype(aitype.toUpperCase());
                    return aiomeRepository.save(aiome);
                });
    }

    @Override
    public Mono<Aiome> update(Integer id, String question, String response) {
        return aiomeRepository.updateQuestionAndResponse(id, question, response)
                .then(aiomeRepository.findById(id));
    }

    @Override
    public Mono<Void> softDelete(Integer id) {
        return aiomeRepository.softDeleteById(id)
                .then();
    }

    @Override
    public Mono<Void> restore(Integer id) {
        return aiomeRepository.restoreById(id)
                .then();
    }

    @Override
    public Mono<Void> permanentDelete(Integer id) {
        return aiomeRepository.deleteById(id);
    }

    private Mono<String> getAiResponse(String question, String aitype) {
        return switch (aitype.toUpperCase()) {
            case "COPILOT" -> getCopilotResponse(question);
            case "GEMINI" -> getGeminiResponse(question);
            default -> Mono.just("Tipo de AI no soportado");
        };
    }

    private Mono<String> getCopilotResponse(String question) {
        return webClientBuilder.build()
                .post()
                .uri(copilotApiUrl)
                .header("x-rapidapi-key", copilotApiKey)
                .header("x-rapidapi-host", copilotApiHost)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(Map.of(
                        "messages", new Object[]{
                                Map.of("role", "user", "content", question)
                        }
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    if (response.containsKey("choices") && ((java.util.List<?>) response.get("choices")).size() > 0) {
                        Map<?, ?> choice = (Map<?, ?>) ((java.util.List<?>) response.get("choices")).get(0);
                        Map<?, ?> message = (Map<?, ?>) choice.get("message");
                        return message.get("content").toString();
                    }
                    return "No se pudo obtener respuesta de Copilot";
                })
                .onErrorResume(e -> Mono.just("Error al conectar con Copilot: " + e.getMessage()));
    }

    private Mono<String> getGeminiResponse(String question) {
        return webClientBuilder.build()
                .post()
                .uri(geminiApiUrl + "?key=" + geminiApiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(Map.of(
                        "contents", new Object[]{
                                Map.of("parts", new Object[]{
                                        Map.of("text", question)
                                })
                        }
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    if (response.containsKey("candidates") && ((java.util.List<?>) response.get("candidates")).size() > 0) {
                        Map<?, ?> candidate = (Map<?, ?>) ((java.util.List<?>) response.get("candidates")).get(0);
                        Map<?, ?> content = (Map<?, ?>) candidate.get("content");
                        java.util.List<?> parts = (java.util.List<?>) content.get("parts");
                        if (parts.size() > 0) {
                            Map<?, ?> part = (Map<?, ?>) parts.get(0);
                            return part.get("text").toString();
                        }
                    }
                    return "No se pudo obtener respuesta de Gemini";
                })
                .onErrorResume(e -> Mono.just("Error al conectar con Gemini: " + e.getMessage()));
    }
}