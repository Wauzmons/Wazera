package eu.wauz.wazera;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import eu.wauz.wazera.service.AuthDataService;

@Configuration
@EnableWebSecurity
public class WazeraSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private WazeraAuthenticationProvider authProvider;
	
	@Autowired
	private AuthDataService authService;

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider);
	}
	
	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.csrf().disable()
			.authorizeRequests()
			.anyRequest().authenticated()
			.and()
			.formLogin()
			.loginProcessingUrl("/perform_login")
			.defaultSuccessUrl("/dashboard.xhtml", true)
			.and()
			.logout()
			.deleteCookies("JSESSIONID")
			.logoutUrl("/perform_logout")
	        .and()
	        .rememberMe().key("uniqueAndSecret");
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Component
	private class WazeraAuthenticationProvider implements AuthenticationProvider {
		
		@Override
		public boolean supports(Class<?> authentication) {
			return authentication.equals(UsernamePasswordAuthenticationToken.class);
		}
		
		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {
			String username = authentication.getName();
	        String password = authentication.getCredentials().toString();
	        if(authService.authenticate(username, password)) {
	        	return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
	        }
	        throw new BadCredentialsException("Wrong Username or Password!");
		}
		
	}
	
}
