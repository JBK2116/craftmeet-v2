package live.craftmeet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CraftmeetBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CraftmeetBackendApplication.class, args);
	}

}
