package com.udayan.tallyapp;

import com.udayan.tallyapp.auth.AuthService;
import com.udayan.tallyapp.auth.AuthUser;
import com.udayan.tallyapp.user.GenderType;
import com.udayan.tallyapp.user.role.Role;
import com.udayan.tallyapp.user.role.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.LocalDate;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@Slf4j
public class TallyAppApplication implements  CommandLineRunner{

	@Autowired
	AuthService authService;

	public static void main(String[] args) {
		SpringApplication.run(TallyAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByName("ADMIN").isEmpty()) {
				roleRepository.save(Role.builder().name("ADMIN").build());
			}
			if (roleRepository.findByName("MANAGER").isEmpty()) {
				roleRepository.save(Role.builder().name("MANAGER").build());
			}
			if (roleRepository.findByName("USER").isEmpty()) {
				roleRepository.save(Role.builder().name("USER").build());
			}
		};
	}

	@Override
	public void run(String... args) throws Exception {
		if(args==null || args.length<3)
			throw new IllegalStateException("Admin user initiation failed");

		AuthUser.UserRequest authUser = new AuthUser.UserRequest();

		authUser.setEmail(args[0]);
		authUser.setUsername(args[0]);
		authUser.setMobileNo(args[1]);
		authUser.setPassword(args[2]);
		authUser.setGender(GenderType.RATHER_NOT_SAY);
		authUser.setFullName("Admin User");
		authUser.setDateOfBirth (LocalDate.of(1970, 1, 1));

		authService.initiateAdmin(authUser);

	}
}
