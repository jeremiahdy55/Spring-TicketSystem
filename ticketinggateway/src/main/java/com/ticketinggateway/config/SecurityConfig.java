package com.ticketinggateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	UserDetailsService userDetailsService;

	private final String[] loggedInEndpoints = { "/landing", "/getHistory/*","/getTicket/*", "/ticketDetails/*", "/postTicket*", "/ticketForm"};
	private final String[] managerEndpoints = {"/approveTicket/*", "/rejectTicket/*", "/getOpenTickets", "/getAllTickets", "/managerDashboard"};
	private final String[] userEndpoints = {"/reopenTicket/*", "/closeTicket/*", "/getUserTickets/*", "/getActiveUserTickets/*", "/userDashboard"};
	private final String[] adminEndpoints = {"/resolveTicket/*", "/getAssignedTickets/*", "/getActiveAssignedTickets/*", "/adminDashboard"};
	private final String deleteEndpoint = "/deleteTicket/*";

	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean 
	public SecurityFilterChain apiFilterChain2(HttpSecurity http) throws Exception {
		http
			.apply(MyCustomDsl.customDsl())
			.flag(true).and()
			.authorizeRequests().requestMatchers("/").permitAll().and()
			      //.exceptionHandling().accessDeniedPage("/accessDeniedPage").and()
			.authorizeRequests().requestMatchers(loggedInEndpoints).hasAnyAuthority("ADMIN", "USER", "MANAGER").and()
			.authorizeRequests().requestMatchers(managerEndpoints).hasAnyAuthority("MANAGER").and()
			.authorizeRequests().requestMatchers(userEndpoints).hasAnyAuthority("USER").and()
			.authorizeRequests().requestMatchers(deleteEndpoint).hasAnyAuthority("USER", "MANAGER").and()
			.authorizeRequests().requestMatchers(adminEndpoints).hasAnyAuthority("ADMIN").and()

		.formLogin()
			.loginPage("/login")
			.defaultSuccessUrl("/landing").permitAll().and()
		.logout()
			.logoutSuccessUrl("/")
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
        .permitAll();
		
		return http.build();
	}
}
