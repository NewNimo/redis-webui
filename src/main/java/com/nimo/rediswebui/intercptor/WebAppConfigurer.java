package com.nimo.rediswebui.intercptor;


import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**  
* @author nimo
* @date 2017年11月23日 
* @version V1.0  
* @Description: WebMvcConfigurer
*/
@Configuration
public class WebAppConfigurer implements  WebMvcConfigurer {
	
	


	@Autowired
	SessionInterceptor sessionInterceptor;
	//注册拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(sessionInterceptor)
		.excludePathPatterns("/static/**")
		.excludePathPatterns("/favicon.ico")
		.excludePathPatterns("/login")
		.excludePathPatterns("/index")
		.excludePathPatterns("/reg")
		.excludePathPatterns("/register")
		.excludePathPatterns("/")
		.addPathPatterns("/**");
	}

		

	//静态资源处理
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**","favicon.ico")
				.addResourceLocations("classpath:/static/");
	}

	@Bean
	public HttpMessageConverter<String> responseBodyStringConverter() {
		return new StringHttpMessageConverter(StandardCharsets.UTF_8);
	}



	

}
