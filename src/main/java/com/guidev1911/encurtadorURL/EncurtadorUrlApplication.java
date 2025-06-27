package com.guidev1911.encurtadorURL;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
public class EncurtadorUrlApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		SpringApplication app = new SpringApplication(EncurtadorUrlApplication.class);

		Map<String, Object> props = new HashMap<>();
		dotenv.entries().forEach(entry -> props.put(entry.getKey(), entry.getValue()));
		app.setDefaultProperties(props);

		app.run(args);
	}
}
