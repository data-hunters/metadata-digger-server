package ai.datahunters.md.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {

		SpringApplication.run(ServerApplication.class, args);

		TestWebClient gwc = new TestWebClient();
		System.out.println(gwc.getResult());
	}

}
