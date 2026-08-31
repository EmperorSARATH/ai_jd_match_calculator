package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {


    public static void main(String[] args) throws Exception{

        String text1 = "Java backend developer";
        String text2 = "flutter developer";

        String jobDescription = """
        Java Developer
        """;

        EmbeddingService embeddingService = new EmbeddingService();

//        String resume = """
//                 Systems engineer,admin
//                 dveeloper
//                """;

        float[] embedding1 = embeddingService.generateEmbedding(text1);



        float[] embedding2 = embeddingService.generateEmbedding(text2);

        float[] embedding3 = embeddingService.generateEmbedding(jobDescription);

        DatabaseService databaseService = new DatabaseService();

        databaseService.findSimilarCandidates(embedding3);

        System.out.println("Candidate searched!");



        System.out.println("Embedding 1 size: " + embedding1.length);
        System.out.println("Embedding 2 size: " + embedding2.length);

        double similarity = cosineSimilarity(embedding1, embedding2);

        System.out.println("Cosine similarity: " + similarity);


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

}