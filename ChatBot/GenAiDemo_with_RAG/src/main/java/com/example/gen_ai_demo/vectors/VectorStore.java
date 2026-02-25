package com.example.gen_ai_demo.vectors;

import org.springframework.ai.document.Document;

import java.util.List;

public interface VectorStore {

    void add(Document document, float[] embedding);

    List<Document> getDocuments();

    List<float[]> getEmbeddings();

}
