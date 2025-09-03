package com.scu.imageseg.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName: MyWebMvcConfig
 * Package: com.zrb.scampus.confug
 * Description:
 *
 * @author wlt
 * @Create: 2024/6/13
 * @Version: v1.0
 */

@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 匹配到resourceHandler,将URL映射至accessFilePath（即本地文件夹）
        registry.addResourceHandler("/img/**").addResourceLocations("file:py/img/");
        registry.addResourceHandler("/pdf/**").addResourceLocations("file:py/pdf/");
//        registry.addResourceHandler(resourceHandler).addResourceLocations("file:///" + accessFilePath);
    }

}
