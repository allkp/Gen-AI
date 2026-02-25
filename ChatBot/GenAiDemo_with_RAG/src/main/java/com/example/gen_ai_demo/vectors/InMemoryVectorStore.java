package com.example.gen_ai_demo.vectors;


import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class InMemoryVectorStore implements VectorStore {

    private final List<Document>  documents = new ArrayList<>();
    private final List<float[]> embeddings = new ArrayList<>();

    @Override
    public void add(Document document, float[] embedding) {
        if (document == null || embedding == null) {
            return;
        }

        documents.add(document);
        embeddings.add(embedding);
    }

    public List<Document> getDocuments() {
        return Collections.unmodifiableList(documents);
    }
    public List<float[]> getEmbeddings() {
        return Collections.unmodifiableList(embeddings);
    }

    public boolean isEmpty() {
        return documents.isEmpty();
    }

}
