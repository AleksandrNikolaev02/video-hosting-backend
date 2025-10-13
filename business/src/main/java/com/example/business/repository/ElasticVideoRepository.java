package com.example.business.repository;

import com.example.business.model.ElasticVideo;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ElasticVideoRepository extends ElasticsearchRepository<ElasticVideo, Long> {
    @Query(value = """
            {
                "multi_match": {
                    "query": "?0",
                    "fields": [ "name^2", "description", "names" ],
                    "fuzziness": "1"
                }
            }
            """)
    List<ElasticVideo> findVideosByInfo(String name);
}
