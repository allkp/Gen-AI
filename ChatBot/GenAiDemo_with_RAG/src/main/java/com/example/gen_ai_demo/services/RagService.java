package com.example.gen_ai_demo.services;

import com.example.gen_ai_demo.loader.DocumentLoader;
import com.example.gen_ai_demo.vectors.InMemoryVectorStore;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RagService {

    private final DocumentLoader documentLoader;
    private final EmbeddingService embeddingService;
    private final ChatService chatService;
    private final InMemoryVectorStore vectorStore;

    public RagService(
            EmbeddingService embeddingService,
            ChatService chatService,
            InMemoryVectorStore vectorStore,
            DocumentLoader documentLoader
    ) {
        this.embeddingService = embeddingService;
        this.chatService = chatService;
        this.vectorStore = vectorStore;
        this.documentLoader = documentLoader;
    }

    @PostConstruct
    public void init() {
        loadInitialDocuments();
    }

    private void loadInitialDocuments() {

        List<Document> loadedDocs = documentLoader.loadDocuments();

        if (loadedDocs == null || loadedDocs.isEmpty()) {
            return;
        }

        for (Document doc : loadedDocs) {
            float[] embedding = embeddingService.embed(doc.getText());
            vectorStore.add(doc, embedding);
        }
    }

    public String processQtn(String question) {

        if (question == null || question.isBlank()) {
            return "Question cannot be empty.";
        }

        if (vectorStore.isEmpty()) {
            return "Knowledge base not initialized.";
        }

        float[] questionEmbedding = embeddingService.embed(question);

        List<Integer> topMatches = findTopK(questionEmbedding, 3);

        StringBuilder contextBuilder = new StringBuilder();

        List<Document> documents = vectorStore.getDocuments();

        for (int index : topMatches) {
            contextBuilder
                    .append(documents.get(index).getText())
                    .append("\n");
        }

        return chatService.ask(contextBuilder.toString(), question);
    }

    private List<Integer> findTopK(float[] questionEmbedding, int k) {

        List<float[]> storedEmbeddings = vectorStore.getEmbeddings();

        List<Integer> indices = new ArrayList<>();
        List<Double> scores = new ArrayList<>();

        for (int i = 0; i < storedEmbeddings.size(); i++) {

            double similarity =
                    cosineSimilarity(questionEmbedding, storedEmbeddings.get(i));

            indices.add(i);
            scores.add(similarity);
        }

        indices.sort(Comparator.comparingDouble(scores::get).reversed());

        return indices.subList(0, Math.min(k, indices.size()));
    }

    private double cosineSimilarity(float[] a, float[] b) {

        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) {
            return 0;
        }

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
