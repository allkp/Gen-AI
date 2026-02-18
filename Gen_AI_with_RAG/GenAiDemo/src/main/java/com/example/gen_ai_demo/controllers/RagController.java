package com.example.gen_ai_demo.controllers;

import com.example.gen_ai_demo.services.RagService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping
    public String ask(@RequestParam String question){
        return ragService.processQtn(question);
    }

//    private final ChatClient chatClient;
//    private final EmbeddingModel embeddingModel;
//
//    private final List<Document> documents = new ArrayList<>();
//    private final List<float[]> documentEmbeddings = new ArrayList<>();
//
//    public RagController(ChatClient.Builder builder,
//                         EmbeddingModel embeddingModel) {
//
//        this.chatClient = builder.build();
//        this.embeddingModel = embeddingModel;
//
//        // Sample telecom docs
//        documents.add(new Document("Roaming charges apply when using services outside your home network."));
//        documents.add(new Document("Data usage above 10GB per month will incur additional charges."));
//        documents.add(new Document("International SMS costs are higher than local SMS."));
//
//        // Precompute embeddings
//        for (Document doc : documents) {
//            float[] embedding = embeddingModel.embed(doc.getText());
//            documentEmbeddings.add(embedding);
//        }
//    }
//
//    @GetMapping
//    public String ask(@RequestParam String question) {
//
//        // 1️⃣ Embed question
//        float[] questionEmbedding = embeddingModel.embed(question);
//
//        // 2️⃣ Find most similar document
//        int bestIndex = findMostSimilar(questionEmbedding);
//        String context = documents.get(bestIndex).getText();
//
//        // 3️⃣ Send to LLM
//        return chatClient.prompt()
//                .user("""
//                        Answer using ONLY the context below.
//
//                        Context:
//                        %s
//
//                        Question:
//                        %s
//                        """.formatted(context, question))
//                .call()
//                .content();
//    }
//
//    private int findMostSimilar(float[] questionEmbedding) {
//
//        double highestScore = -1;
//        int bestIndex = 0;
//
//        for (int i = 0; i < documentEmbeddings.size(); i++) {
//
//            double similarity =
//                    cosineSimilarity(questionEmbedding, documentEmbeddings.get(i));
//
//            if (similarity > highestScore) {
//                highestScore = similarity;
//                bestIndex = i;
//            }
//        }
//
//        return bestIndex;
//    }
//
//    private double cosineSimilarity(float[] a, float[] b) {
//
//        double dot = 0.0;
//        double normA = 0.0;
//        double normB = 0.0;
//
//        for (int i = 0; i < a.length; i++) {
//            dot += a[i] * b[i];
//            normA += Math.pow(a[i], 2);
//            normB += Math.pow(b[i], 2);
//        }
//
//        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
//    }
}
