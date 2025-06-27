package com.apartment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Map;

/**
 * 公寓管理系统应用程序入口
 */
@SpringBootApplication
@ServletComponentScan
//@EnableMongoAuditing
public class ApartmentManagementApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ApartmentManagementApplication.class, args);
        
        // 打印所有映射的端点
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        
        System.out.println("=============所有映射的URL路径===============");
        map.forEach((info, method) -> {
            System.out.println("URL: " + info + ", Method: " + method);
        });
        System.out.println("=========================================");
    }
}
