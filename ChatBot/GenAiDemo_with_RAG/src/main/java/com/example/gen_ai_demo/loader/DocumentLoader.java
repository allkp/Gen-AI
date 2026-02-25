package com.example.gen_ai_demo.loader;


import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentLoader {

    public List<Document> loadDocuments() {

        List<Document> documents = new ArrayList<>();

        try {
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();

            Resource[] resources =
                    resolver.getResources("classpath:Docs/*.txt");

            for (Resource resource : resources) {

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(resource.getInputStream()));

                StringBuilder content = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                documents.add(new Document(content.toString()));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load documents", e);
        }

        return documents;
    }

}
