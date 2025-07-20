package com.joysky.ice.easyhttp.app;

import com.github.vizaizai.boot.annotation.EnableEasyHttp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEasyHttp("com.joysky.ice.easyhttp.auth.start.client")
public class EasyhttpAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyhttpAppApplication.class, args);
	}

}
