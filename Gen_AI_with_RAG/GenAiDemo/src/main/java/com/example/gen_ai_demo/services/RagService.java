package com.example.gen_ai_demo.services;

import com.example.gen_ai_demo.vectors.InMemoryVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final EmbeddingService embeddingService;
    private final ChatService chatService;
    private final InMemoryVectorStore vectorStore;

    public RagService(
            EmbeddingService embeddingService,
            ChatService chatService,
            InMemoryVectorStore vectorStore
    ) {
        this.embeddingService = embeddingService;
        this.chatService = chatService;
        this.vectorStore = vectorStore;

        loadInitialDocuments();
    }

    private void loadInitialDocuments() {
        addDocument("Roaming charges apply when using services outside your home network.");
        addDocument("Data usage above 10GB per month will incur additional charges.");
        addDocument("International SMS costs are higher than local SMS.");
    }

    private void addDocument(String text) {
        float[] embedding = embeddingService.embed(text);
        vectorStore.add(new Document(text), embedding);
    }

    public String processQtn(String question) {
        float[] questionEmbedding = embeddingService.embed(question);

        int bestIndex = findMostSimilar(questionEmbedding);
        String context = vectorStore.getDocuments().get(bestIndex).getText();

        return chatService.ask(context, question);
    }
    private int findMostSimilar(float[] questionEmbedding) {
        List<float[]> storedEmbeddings = vectorStore.getEmbeddings();

        double highestScore = -1;
        int bestIndex = 0;
        for (int i = 0; i < storedEmbeddings.size(); i++) {
            double similarity = cosineSimilarity(questionEmbedding, storedEmbeddings.get(i));

            if (similarity > highestScore) {
                highestScore = similarity;
                bestIndex = i;
            }
        }

        return bestIndex;
    }

    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += Math.pow(a[i], 2);
            normB += Math.pow(b[i], 2);
        }
        return dot/(Math.sqrt(normA) *  Math.sqrt(normB));
    }

}
