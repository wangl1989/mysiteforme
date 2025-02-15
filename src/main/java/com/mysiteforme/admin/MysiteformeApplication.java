/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:42:05
 * @ Description: 主启动类
 */

package com.mysiteforme.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MysiteformeApplication extends SpringBootServletInitializer {


	public static void main(String[] args) {
		SpringApplication.run(MysiteformeApplication.class, args);
	}
}
