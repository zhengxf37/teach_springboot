package org.tutorial.tutorial_platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tutorial.tutorial_platform.dto.AiRequestDTO;
import org.tutorial.tutorial_platform.service.AiService;

@RestController
@RequestMapping("/api/deepseek")
public class AiController {

    @Autowired
    private AiService aiService;

    @GetMapping("/ask")
    public ResponseEntity<String> askQuestion(@RequestParam String question) {
        return ResponseEntity.ok(aiService.chat(question));
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateFromJson(@RequestBody AiRequestDTO requestDTO) {
        return ResponseEntity.ok(aiService.chat(requestDTO.getPrompt()));
    }
}
