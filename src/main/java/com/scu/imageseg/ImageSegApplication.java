package com.scu.imageseg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
//@ServletComponentScan(basePackages = "com.scu.imageseg.filter") // 为了使过滤器生效 需要添加注解使其被Servlet容器扫描到
public class ImageSegApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageSegApplication.class, args);
	}

}
