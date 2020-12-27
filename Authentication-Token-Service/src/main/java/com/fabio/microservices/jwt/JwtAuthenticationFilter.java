package com.fabio.microservices.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fabio.microservices.configuration.JwtConfigurationProperties;
import com.fabio.microservices.model.AuthUser;
import com.fabio.microservices.security.UserDetailsCustomServices;
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	@Autowired
	private UserDetailsCustomServices userDetailsCustomServices;
	
	@Autowired
	private JwtConfigurationProperties configJWT;
	
	@Autowired
	private JwtUtilsMethod jwtUtilsMethod;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token=null;
		String username=null;
		UserDetails userDetails=null;
		try {
			//verifichiamo che sia presente un token
			token=request.getHeader(configJWT.getHeader());
			if(token!=null) {
				// se il token è presente, ricostruiamo l'utente dallo stesso, e settiamolo
				//nel contesto di autenticazione di spring
				username=jwtUtilsMethod.getUsernameFromToken(token, configJWT.getSecret());
				if(username!=null) {
					userDetails=userDetailsCustomServices.loadUserByUsername(username);
					if(jwtUtilsMethod.isTokenValid(token, configJWT.getSecret(), username, configJWT.getExpiration())) {
						//autentichiamo l'utente nel contesto di spring
						 UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			                SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		filterChain.doFilter(request, response);
	}
	
	
	
}
