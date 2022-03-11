package com.learning.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.learning.security.jwt.AuthEntryPointJwt;
import com.learning.security.jwt.AuthTokenFilter;
import com.learning.security.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;
	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;
	@Bean

	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// core part security ---> we can restrict the access of end points through this config.
		//we can set unauthorized access through this.
		//we can provide direct go access for signup and signin (authorizing the res).
		// applying token validation for end points.
		// CORS: 
		http.cors().and().csrf().disable()
		.exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
		.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and().authorizeRequests().antMatchers("/api/customer/register").permitAll()
		.and().authorizeRequests().antMatchers("/api/customer/authenticate").permitAll()
		.and().authorizeRequests().antMatchers("/api/admin/authenticate").permitAll()
		.and().authorizeRequests().antMatchers("/api/staff/authenticate").permitAll()
		.antMatchers("/api/admin/**").hasRole("ADMIN")
		.antMatchers("/api/staff/**").hasRole("STAFF")
		.antMatchers("/api/customer/**").authenticated().anyRequest().permitAll();
		
		http.addFilterBefore(authenticationJwtTokenFilter(), 
				UsernamePasswordAuthenticationFilter.class);
	}

}
