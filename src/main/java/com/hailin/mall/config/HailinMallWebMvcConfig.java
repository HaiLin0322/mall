package com.hailin.mall.config;

import com.hailin.mall.common.Constant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class HailinMallWebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations("file:"+ Constant.FILE_UPLOAD_DIR);
        //路径文件还没有，暂时注释掉
        /**registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars");*/
    }
}
