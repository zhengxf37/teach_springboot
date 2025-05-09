package org.tutorial.tutorial_platform.pojo;

/**
 * Response - 统一响应封装类
 *
 * 用于规范后端返回的JSON数据结构，包含：
 * - 成功响应：返回业务数据 + 成功状态
 * - 失败响应：返回错误信息 + 失败状态
 *
 * 核心功能：
 * - newSuccess() 快速构建成功响应（默认code=200）
 * - newFail() 快速构建失败响应（需指定code）
 *
 * 泛型说明：
 * @param <T> 响应数据的类型
 *
 * 元信息：
 * @author zxf
 * @version 1.0
 * @since 2025-05-09
 */
public class Response <T>{
    private T data;
    private boolean success;
    private String errorMsg;
    private int code;


    /**
     * 构建成功响应
     *
     * @param data 要返回的业务数据
     * @param <K> 响应数据的泛型类型
     * @return 封装好的成功响应对象
     */
    public static <K> Response<K> newSuccess(K data){
        Response<K> response = new Response<K>();
        response.setData(data);
        response.setSuccess(true);
        response.setCode(200); // 默认成功状态码
        return response;
    }

    /**
     * 构建失败响应（需指定状态码）
     *
     * @param errorMsg 错误提示信息
     * @param code 错误状态码（如400、404、500等）
     * @param <K> 响应数据的泛型类型
     * @return 封装好的失败响应对象
     * @throws IllegalArgumentException 当code为2xx时抛出
     */
    public static <K> Response<K> newFail(String errorMsg, int code) {
        if (code >= 200 && code < 300) {
            throw new IllegalArgumentException("失败响应的状态码不能为2xx");
        }
        Response<K> response = new Response<>();
        response.setErrorMsg(errorMsg);
        response.setSuccess(false);
        response.setCode(code);
        return response;
    }

    // ================== Getter/Setter ==================
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
