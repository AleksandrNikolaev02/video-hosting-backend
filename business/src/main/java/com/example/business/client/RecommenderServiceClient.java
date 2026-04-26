package com.example.business.client;

import com.example.business.config.ClientConfig;
import com.example.business.dto.RecommenderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "recommender", url = "api:8000", configuration = ClientConfig.class)
public interface RecommenderServiceClient {
    @RequestMapping(method = RequestMethod.GET, value = "recommend/user/{id}")
    List<RecommenderDTO> getRecommenderVideos(@PathVariable Long id);
}
