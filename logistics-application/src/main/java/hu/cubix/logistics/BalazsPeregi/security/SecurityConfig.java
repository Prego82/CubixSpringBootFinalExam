package hu.cubix.logistics.BalazsPeregi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	public static final String TRANSPORT_MANAGER_NAME = "transportManager";
	public static final String ADDRESS_MANAGER_NAME = "addressManager";
	public static final String PASS = "pass";
	private static final String ADDRESS_MANAGER = "AddressManager";
	private static final String TRANSPORT_MANAGER = "TransportManager";

	@Autowired
	private JwtAuthFilter authFilter;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsService createUsers(PasswordEncoder passwordEncoder) {
		UserBuilder userBuilder = User.builder();

		UserDetails addressManager = userBuilder.username(ADDRESS_MANAGER_NAME).password(passwordEncoder.encode(PASS))
				.authorities(ADDRESS_MANAGER).build();
		UserDetails transportManager = userBuilder.username(TRANSPORT_MANAGER_NAME)
				.password(passwordEncoder.encode(PASS)).authorities(TRANSPORT_MANAGER).build();

		return new InMemoryUserDetailsManager(addressManager, transportManager);
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.POST, "/api/login").permitAll().requestMatchers(HttpMethod.GET, "/error")
				.permitAll().requestMatchers(HttpMethod.POST, "/error").permitAll()
				.requestMatchers(HttpMethod.DELETE, "/error").permitAll().requestMatchers(HttpMethod.PUT, "/error")
				.permitAll().requestMatchers(HttpMethod.GET, "/api/addresses/**").hasAuthority(ADDRESS_MANAGER)
				.requestMatchers(HttpMethod.POST, "/api/addresses/**").hasAuthority(ADDRESS_MANAGER)
				.requestMatchers(HttpMethod.DELETE, "/api/addresses/**").hasAuthority(ADDRESS_MANAGER)
				.requestMatchers(HttpMethod.PUT, "/api/addresses/**").hasAuthority(ADDRESS_MANAGER)
				.requestMatchers(HttpMethod.POST, "/api/transportPlans/**").hasAuthority(TRANSPORT_MANAGER).anyRequest()
				.authenticated()).addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}
