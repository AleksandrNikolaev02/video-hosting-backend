package com.example.business.controller;

import com.example.business.service.generator.Generator;
import com.example.business.service.generator.Swappable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/generate")
public class GenerateController {
    private Generator reactionGenerator;
    private Swappable swapperChannels;

    @PostMapping("/reactions")
    public ResponseEntity<Void> generateReactions(@RequestParam("limit") int limit) {
        reactionGenerator.generate(limit);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/swap-channels")
    public ResponseEntity<Void> swapChannelOfUsers() {
        swapperChannels.swap();
        return ResponseEntity.ok().build();
    }

    @Autowired
    @Qualifier(value = "reactionGenerator")
    public void setGenerator(Generator generator) {
        this.reactionGenerator = generator;
    }

    @Autowired
    @Qualifier(value = "swapper")
    public void setSwapperChannels(Swappable swapperChannels) {
        this.swapperChannels = swapperChannels;
    }
}
