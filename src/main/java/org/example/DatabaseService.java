package org.example;

import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseService {

    private static final String URL =
            "jdbc:postgresql://localhost:5433/ai_lab";

    private static final String USER = "ai_user";
    private static final String PASSWORD = "ai_password";

    public void saveCandidate(
            String name,
            String resume,
            float[] embedding
    ) throws SQLException {

        String sql = """
                INSERT INTO candidates (name, resume, embedding)
                VALUES (?, ?, ?)
                """;

        try (Connection connection =
                     DriverManager.getConnection(URL, USER, PASSWORD);

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, resume);

            PGobject vector = new PGobject();
            vector.setType("vector");
            vector.setValue(toVectorString(embedding));

            statement.setObject(3, vector);

            int rows = statement.executeUpdate();

            System.out.println("Rows inserted: " + rows);


        }
    }

    public void findSimilarCandidates(float[] jobEmbedding) throws SQLException {

        String sql = """
            SELECT
                id,
                name,
                resume,
                embedding <=> ? AS distance
            FROM candidates
            ORDER BY embedding <=> ?
            LIMIT 5
            """;

        try (Connection connection =
                     DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            PGobject vector = new PGobject();
            vector.setType("vector");
            vector.setValue(toVectorString(jobEmbedding));

            statement.setObject(1, vector);
            statement.setObject(2, vector);

            try (var resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String resume = resultSet.getString("resume");
                    double distance = resultSet.getDouble("distance");

                    System.out.println(
                            id + " | " +
                                    name + " | " +
                                    distance
                    );
                }
            }
        }
    }

    private String toVectorString(float[] embedding) {

        StringBuilder builder = new StringBuilder("[");

        for (int i = 0; i < embedding.length; i++) {

            if (i > 0) {
                builder.append(",");
            }

            builder.append(embedding[i]);
        }

        builder.append("]");

        return builder.toString();
    }
}
