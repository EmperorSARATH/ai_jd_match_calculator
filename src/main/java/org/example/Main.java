package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final String OLLAMA_URL =
            "http://localhost:11434/api/embeddings";

    private static final String MODEL =
            "all-minilm";


    public static void main(String[] args) throws Exception{

        String text1 = "Java backend developer";
        String text2 = "flutter developer";

        float[] embedding1 = getEmbedding(text1);
        float[] embedding2 = getEmbedding(text2);

        System.out.println("Embedding 1 size: " + embedding1.length);
        System.out.println("Embedding 2 size: " + embedding2.length);

        double similarity = cosineSimilarity(embedding1, embedding2);

        System.out.println("Cosine similarity: " + similarity);


    }

    private static float[] getEmbedding(String text) throws Exception {

        String jsonBody = """
                {
                    "model": "%s",
                    "prompt": "%s"
                }
                """.formatted(MODEL, text);

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

    private static float[] extractEmbedding(String response) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        EmbeddingResponse embeddingResponse =
                objectMapper.readValue(response, EmbeddingResponse.class);

        return embeddingResponse.embedding;
    }
    private static double cosineSimilarity(float[] a, float[] b) {

        if (a.length != b.length) {
            throw new IllegalArgumentException(
                    "Vectors must have the same dimensions"
            );
        }

        double dotProduct = 0;
        double magnitudeA = 0;
        double magnitudeB = 0;

        for (int i = 0; i < a.length; i++) {

            dotProduct += a[i] * b[i];

            magnitudeA += a[i] * a[i];
            magnitudeB += b[i] * b[i];
        }

        magnitudeA = Math.sqrt(magnitudeA);
        magnitudeB = Math.sqrt(magnitudeB);

        return dotProduct / (magnitudeA * magnitudeB);
    }

    static class EmbeddingResponse {

        public float[] embedding;
    }
}