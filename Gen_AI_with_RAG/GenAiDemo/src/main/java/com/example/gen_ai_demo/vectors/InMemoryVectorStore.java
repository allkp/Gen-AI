package com.example.gen_ai_demo.vectors;


import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryVectorStore implements VectorStore {

    private final List<Document>  documents = new ArrayList<>();
    private final List<float[]> embeddings = new ArrayList<>();

//    public void add(Document doc, float[] embedding) {
//        documents.add(doc);
//        embeddings.add(embedding);
//    }

    @Override
    public void add(Document document, float[] embedding) {
        documents.add(document);
        embeddings.add(embedding);
    }

    public List<Document> getDocuments() {
        return documents;
    }
    public List<float[]> getEmbeddings() {
        return embeddings;
    }

}
