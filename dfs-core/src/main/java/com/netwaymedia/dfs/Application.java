package com.netwaymedia.dfs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.util.Locale;
import java.util.TimeZone;

/**
 *dfs-fastdfs-client-api
 */
@SpringBootApplication
@ImportResource(locations = "classpath:beans/beans-*.xml")
public class Application {
	public static void main(String[] args) {
		Locale.setDefault(Locale.CHINA);
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		SpringApplication.run(Application.class, args);
	}
}
