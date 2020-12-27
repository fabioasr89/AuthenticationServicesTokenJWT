package com.fabio.microservices.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fabio.microservices.configuration.JwtConfigurationProperties;
import com.fabio.microservices.jwt.JwtUtilsMethod;
import com.fabio.microservices.response.JsonResponse;
import com.fabio.microservices.response.ResponseError;
import com.fabio.microservices.security.UserDetailsCustomServices;

@RestController
@RequestMapping(value="/Api/")
public class AuthResourcesController {
		@Autowired
		PasswordEncoder pswencoder;
	 
		@Autowired
		private AuthenticationManager authenticationManager;
	 
		@Autowired
		private JwtConfigurationProperties configJWT;
		
		@Autowired
		private JwtUtilsMethod jwtUtilsMethod;
	 
	 @Autowired
	 private UserDetailsCustomServices userDetailsServices;
	 
	 @RequestMapping(value="authenticationtoken", method=RequestMethod.GET)
	 public JsonResponse getAuthenticationToken(HttpServletRequest request,HttpServletResponse response){
		JsonResponse responseJson=new JsonResponse();
		Authentication auth=null;
		String username=null;
		String password=null;
		UserDetails userDetails=null;
		String token=null;
		ResponseError error=null;
		try {
			username=request.getHeader("Auth-username");
			password=request.getHeader("Auth-password");
			//gli passiamo UsernamePasswordAuthenticationToken, che richiamrrà UserDetailsCustomServices
			//per recuperare dal db l'utente con quell'username e confrontare quella settata nel token di autorizzazione
			auth=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
		}catch(DisabledException e) {
			e.printStackTrace();
			error=new ResponseError();
			error.setMessageError("Account disabilitato");
		}catch(LockedException e) {
			e.printStackTrace();
			error=new ResponseError();
			error.setMessageError("Non autorizzato");
		}catch(BadCredentialsException e) {
			e.printStackTrace();
			error=new ResponseError();
			error.setMessageError("Credenziali errate. Non esiste un utente con queste credenziali");
		}
		catch(Exception e) {
			e.printStackTrace();
			error=new ResponseError();
			error.setMessageError(e.getMessage());
		}
		if(error!=null) {
			responseJson.setError(error);
		}
		SecurityContextHolder.getContext().setAuthentication(auth);
		//generiamo il token
		userDetails=userDetailsServices.loadUserByUsername(username);
		token=jwtUtilsMethod.generateToken(userDetails,configJWT.getSecret(),configJWT.getExpiration());
		responseJson.setResponse(token);
		
		return responseJson;
	} 
	 
	
}
