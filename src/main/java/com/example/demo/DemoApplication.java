package com.example.demo;

import com.example.demo.datasource.dynamic.multi.context.DynamicDataSourceRegister;
import com.example.demo.spring.util.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

//注册动态多数据源

@Import({DynamicDataSourceRegister.class})
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		ApplicationContext app = SpringApplication.run(DemoApplication.class, args);
		SpringContextUtil.setApplicationContext(app);
	}

}
