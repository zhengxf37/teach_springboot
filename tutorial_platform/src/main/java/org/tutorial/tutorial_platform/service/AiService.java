package org.tutorial.tutorial_platform.service;

public interface AiService {

    /**
     * 获取AI的回答
     * @param question 问题
     * @return  AI的回答
     */
    String chat(String question);
}
