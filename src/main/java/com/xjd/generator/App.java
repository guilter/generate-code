package com.xjd.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xjd.generator.utils.Start;

/**
 * 启动类
 */
@SpringBootApplication
public class App {
	
	public static void main(String[] args) {
		System.out.println("启动前");
		Start.start();
		//SpringApplication.run(App.class, args);
		System.out.println("启动后");
	}
	
}
