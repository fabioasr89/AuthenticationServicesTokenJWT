package com.fabio.microservices.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fabio.microservices.exception.JwtAuthTokenException;
import com.fabio.microservices.jwt.JwtAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private UserDetailsCustomServices userDetailsCustomServices;
	
	@Autowired
	private JwtAuthTokenException jwtAuthTokenException;
	
	@Bean
	public UserDetails getUserDetails() {
		return new UserDetailsCustom();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
	    return super.authenticationManagerBean();
	}
	
	@Bean
	public JwtAuthenticationFilter authenticationJwtFilter() {
		return new JwtAuthenticationFilter();
	}
	
	//diciamo a spring di analizzare le password passate secondo un algoritmo di crittografia (SHA-1,MDC5 ECC)
	 @Bean
	 public PasswordEncoder passwordEncoder() {
	     return new BCryptPasswordEncoder();
	  }
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) {
		try {
			auth.userDetailsService(userDetailsCustomServices).passwordEncoder(passwordEncoder());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void configure(HttpSecurity httpSecurity) {
		try {
			httpSecurity.csrf().disable().exceptionHandling()
			.authenticationEntryPoint(this.jwtAuthTokenException)
			.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and().authorizeRequests()
			.antMatchers(
			        "/",
			        "/*.html",
			        "/favicon.ico",
			        "/**/*.html",
			        "/**/*.css",
			        "/**/*.js"
			).permitAll().antMatchers("/Api/authenticationtoken")
			.permitAll().anyRequest().authenticated();
			//prima dei filtri di spring, controlliamo che non sia già presente un token valido
			httpSecurity.addFilterBefore(authenticationJwtFilter(), UsernamePasswordAuthenticationFilter.class);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
