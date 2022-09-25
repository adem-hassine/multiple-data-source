package com.multidatasource.demo.mongo;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@RestController
@RequestMapping("/documents")
public class MongoInitializer {
    private final DocumentRepository documentRepository;
    @PostConstruct
    // Data initializing with multiple objects
    public void init(){
        this.documentRepository.deleteAll();
        documentRepository.save(MongoDocument.builder().dbIdentifier("MongoDB").unknownObject(new KnownObjectExample(1L,"No property provided")).build());
        documentRepository.save(MongoDocument.builder().dbIdentifier("MongoDB").unknownObject(new DifferentKnownObjectExample(1L,"provided property")).build());

    }
    // Predicate filter
    // support dynamic filter on all different object stored in mongoDocument model example
    @GetMapping
    public List<MongoDocument> findAll(@RequestParam Map<String,String> searchCriteria){
        PathBuilder<MongoDocument> mongoDocumentPathBuilder = new PathBuilder<>(MongoDocument.class,"mongoDocument");
        BooleanBuilder booleanBuilder= new BooleanBuilder();
        for (Map.Entry<String,String> criteria: searchCriteria.entrySet()){
            booleanBuilder.and(mongoDocumentPathBuilder.getString("unknownObject." + criteria.getKey()).containsIgnoreCase(criteria.getValue()));
        }
        assert booleanBuilder.getValue() != null;
        return StreamSupport.stream(documentRepository.findAll(booleanBuilder.getValue()).spliterator(), false)
                .toList();
    }


}