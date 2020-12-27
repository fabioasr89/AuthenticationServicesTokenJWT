package com.fabio.microservices.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fabio.microservices.model.AuthUser;
import com.fabio.microservices.repository.AuthUserRepository;
@Service
public class UserDetailsCustomServices implements UserDetailsService{
	
	@Autowired
	private AuthUserRepository authRepository;
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails user=null;
		AuthUser utente=null;
		try {
			utente=authRepository.getAuthForUsername(username);
			if(utente==null) {
				throw new UsernameNotFoundException("Utenza non censita");
			}
			user=new UserDetailsCustom(utente);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return user;
	}

}
