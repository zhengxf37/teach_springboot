package org.tutorial.tutorial_platform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
     * 获取AI的回答,输入提问，返回答案
     * @param question
     * @return
     */
    @GetMapping("/ask")
    public ResponseEntity<String> askQuestion(@RequestParam String question) throws JsonProcessingException {
        return ResponseEntity.ok(aiService.chat(question));
    }

    /**
     * 获取AI的回答,输入提问，返回答案
     * @param requestDTO
     * @return
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateFromJson(@RequestBody AiRequestDTO requestDTO) throws JsonProcessingException {
        return ResponseEntity.ok(aiService.chat(requestDTO.getPrompt()));
    }

    /**
     * 带双方信息的提问
     * @param request
     * @return
     * @throws RuntimeException
     * @throws JsonProcessingException
     */
    @GetMapping("/askwithdata")
    public ResponseEntity<String> askQuestionWithData(HttpServletRequest request,
                                                      @RequestParam Long anotherId,
                                                      @RequestParam String Question
                                                      ) throws RuntimeException, JsonProcessingException {

        Long userId = (Long) request.getAttribute("userId");
        if (Question==null||Question.equals("")||Question.length()<5){
            Question = "分析这位学生和老师是否匹配";
        }
//        1.学生，2.老师
        return ResponseEntity.ok(aiService.askWithData(Question,userId,anotherId));
    }

    /**
     * ai评价文本，生成-1号用户给当前用户的评价
     * 异步处理数据，生成用户的评价文本，保存数据到数据库,使用python脚本
     * 这个评价作为-1号用户给当前用户的评价，参考了用户的信息
     * @return
     */
    @GetMapping("/fetch-ai-data")
    public ResponseEntity<String> fetchAidata(HttpServletRequest request) throws RuntimeException, JsonProcessingException {
        // 调用Service层方法，异步处理数据
        Long userId = (Long) request.getAttribute("userId");
        aiService.fetchAiData(userId);
        // 立即返回响应给前端
        return ResponseEntity.ok("yes saving");
    }

}
