package dev.wlambertz.rallyon.tournamentmgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

@SpringBootApplication
@Modulith
public class TournamentmgmtApplication {

	public static void main(String... args) {
		SpringApplication.run(TournamentmgmtApplication.class, args);
	}

}
