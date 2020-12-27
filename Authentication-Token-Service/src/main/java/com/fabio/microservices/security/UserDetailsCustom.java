package com.fabio.microservices.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fabio.microservices.model.AuthUser;
import com.fabio.microservices.model.Funzione;

public class UserDetailsCustom implements UserDetails{
	
	private AuthUser user;
	
	public UserDetailsCustom() {
		super();
	}
	
	public UserDetailsCustom(AuthUser user) {
		this.user=user;
	}
	
	//stabiliamo in che modo recuperiamo i ruoli
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities=null;
		Set<Funzione> autorizzazioni=null;
		try {
			authorities=new ArrayList<GrantedAuthority>();
			autorizzazioni=user.getRuolo().getFunzioni();
			for(Funzione funzione:autorizzazioni) {
				authorities.add(new SimpleGrantedAuthority(funzione.getNome()));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return authorities;
	}

	public String getPassword() {
		return user.getPassword();
	}

	public String getUsername() {
		return user.getUsername();
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	public AuthUser getUser() {
		return user;
	}

	public void setUser(AuthUser user) {
		this.user = user;
	}

}
