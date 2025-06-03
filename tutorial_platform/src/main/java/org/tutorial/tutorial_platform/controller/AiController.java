package org.tutorial.tutorial_platform.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tutorial.tutorial_platform.dto.AiRequestDTO;
import org.tutorial.tutorial_platform.service.AiService;

/**
 * AiController -  AI接口
 * 这是ai接口，调用ai，前两个方法只有传参方法不一样，都是调ai，返回也相同，可能同时给前端后端使用，
 * 但是在特定功能下提示词需要特色配置（后端写），所以以后有修改。
 * @author: zhj
 */
@RestController
@RequestMapping("/api/deepseek")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * 获取AI的回答
     * @param question
     * @return
     */
    @GetMapping("/ask")
    public ResponseEntity<String> askQuestion(@RequestParam String question) {
        return ResponseEntity.ok(aiService.chat(question));
    }

    /**
     * 获取AI的回答
     * @param requestDTO
     * @return
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateFromJson(@RequestBody AiRequestDTO requestDTO) {
        return ResponseEntity.ok(aiService.chat(requestDTO.getPrompt()));
    }
    /**
     * ai评价文本
     * 异步处理数据，生成用户的评价文本，保存数据到数据库,使用python脚本
     * 这个评价作为-1号用户给当前用户的评价，参考了用户的信息
     * @return
     */
    @GetMapping("/fetch-ai-data")
    public ResponseEntity<String> fetchAidata(HttpServletRequest request) {
        // 调用Service层方法，异步处理数据
        Long userId = (Long) request.getAttribute("userId");
        aiService.fetchAiData(userId);
        // 立即返回响应给前端
        return ResponseEntity.ok("数据正在处理中，后端自动保存");
    }

}
