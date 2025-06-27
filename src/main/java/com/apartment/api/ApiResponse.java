package com.apartment.api;

import lombok.Data;

import java.io.Serializable;

/**
 * API响应类，统一返回格式
 */
@Data
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 构造函数
     */
    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 消息
     */
    public ApiResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     */
    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功
     *
     * @param <T> 数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "操作成功");
    }

    /**
     * 成功
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, message);
    }

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    /**
     * 成功
     *
     * @param message 消息
     * @param data    数据
     * @param <T>     数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * 失败
     *
     * @param <T> 数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> error() {
        return new ApiResponse<>(500, "操作失败");
    }

    /**
     * 失败
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message);
    }

    /**
     * 失败
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message);
    }
} 