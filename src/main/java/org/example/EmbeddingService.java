package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class EmbeddingService {

    private static final String OLLAMA_URL =
            "http://localhost:11434/api/embeddings";

    private static final String MODEL =
            "all-minilm";


    public float[] generateEmbedding(String text) throws Exception
        {

            Map<String, String> ollamaRequest = Map.of(
                    "model", MODEL,
                    "prompt", text
            );

            ObjectMapper objectMapper = new ObjectMapper();

            String jsonBody = objectMapper.writeValueAsString(ollamaRequest);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Ollama response:");
            System.out.println(response.body());

            return extractEmbedding(response.body());

    }

    public float[] extractEmbedding(String response) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        EmbeddingResponse embeddingResponse =
                objectMapper.readValue(response, EmbeddingResponse.class);

        return embeddingResponse.embedding;
    }

     static class EmbeddingResponse {

        public float[] embedding;
    }
}
