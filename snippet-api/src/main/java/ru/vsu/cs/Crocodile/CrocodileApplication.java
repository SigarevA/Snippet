package ru.vsu.cs.Crocodile;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CrocodileApplication {
	public static void main(String[] args) {
		SpringApplication.run(CrocodileApplication.class, args);
	}

	@Bean
	public OpenAPI springShopOpenAPI() {
		return new OpenAPI()
			.info(new Info().title("Snippet API")
					.description("Snippet application")
					.version("v0.0.1")
					.license(new License().name("Apache 2.0"))
			)
			/*.components(new Components()
				.addSecuritySchemes(
					"bearer",
					new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
					)
			)*/
		;
	}
}
