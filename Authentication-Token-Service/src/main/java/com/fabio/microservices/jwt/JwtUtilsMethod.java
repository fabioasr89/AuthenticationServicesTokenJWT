package com.fabio.microservices.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fabio.microservices.model.AuthUser;
import com.fabio.microservices.model.Funzione;
import com.fabio.microservices.security.UserDetailsCustom;

import java.util.Map.Entry;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
@Component
public class JwtUtilsMethod {
	
	
	//oggetto del jwt
	private static final String CLAIM_KEY_USER="sub";
	//mittente
	private static final String CLAIM_KEY_MITTENTE="iss";
	//ruolo dell'oggetto jwt
	private static final String CLAIM_KEY_ROLE="role";
	//proprietà custom che definisce le funzionalità del ruolo associato
	private static final String CLAIM_KEY_AUTHORITIES="permessi";
	//destinatario del token
	private static final String CLAIM_KEY_AUDIENCE = "audience";
	//momento di creazione del token
	private static final String CLAIM_KEY_CREATED = "iat";
	
	public String getUsernameFromToken(String token,String secret) {
		Claims claims=null;
		String username=null;
		try {
			claims=getClaimsFromToken(token, secret);
			username=claims.getSubject();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return username;
	}

	public Claims getClaimsFromToken(String token,String secret) {
		Claims claims=null;
		try {
			claims=Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return claims;
	}
	/**Recupera la data di creazione dal token*/
	public Date getDataCreazioneFromToken(String token,String secret) {
		Claims claims=null;
		Date creazione=null;
		try {
			claims=getClaimsFromToken(token, secret);
			creazione=(Date) claims.get(CLAIM_KEY_CREATED);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return creazione;
	}
	/**Recupera a data di scadenza del token dal token stesso*/
	 public Date getExpDateFromToken(String token,String secret) {
	        Date expiration=null;
            Claims claims=null;
	        try {
	        	claims= getClaimsFromToken(token,secret);
	            expiration = claims.getExpiration();
	        } catch (Exception e) {
	            expiration = null;
	        }
	        return expiration;
	    }
	 
	 /**Confronta la data di scadenza con quella corrente. Torna true se il token è scaduto*/
	 public boolean isExpiration(String token,String secret,Integer expiration) {
		 Date exp=null;
		 try {
			 exp=getExpDateFromToken(token, secret);
			 return exp.before(new Date());
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		 return false;
	 }
	 
	 public boolean isTokenValid(String token,String secret,String username,Integer expiration) {
		 String usernameToken=null;
		 Claims claims=null;
		 try {
			 usernameToken=getUsernameFromToken(usernameToken, secret);
			 return (username.equals(usernameToken) && ! isExpiration(usernameToken, secret, expiration));
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		 return false;
	 }
	 
	 public Map<String,Object> generaMappaAttributiClaims(UserDetails userDetails){
		 Map<String,Object> map=new LinkedHashMap<String,Object>();
		 UserDetailsCustom userDetailsCustom=(UserDetailsCustom)userDetails;
		 map.put(CLAIM_KEY_CREATED, new Date());
		 map.put(CLAIM_KEY_MITTENTE,"Authentication-Token-Service");
		 List<String> auth=new ArrayList<String>();
		 for(Funzione funzione:userDetailsCustom.getUser().getRuolo().getFunzioni()) {
			 auth.add(funzione.getNome());
		 }  
		 map.put(CLAIM_KEY_AUTHORITIES, auth);
		 map.put(CLAIM_KEY_USER, userDetailsCustom.getUsername());
		 map.put(CLAIM_KEY_ROLE, userDetailsCustom.getUser().getRuolo().getNome());
		 return map;
	 }
	 private Date generateExpirationDate(Integer exipration) {
	        return new Date(System.currentTimeMillis() + exipration * 1000);
	    }
	 /**Metodo che genera il token utilizzando l'algoritmo ES256 e firmato con la chiave passata*/
	 public String generateToken(UserDetails userDetails,String secret,Integer exipration) {
		 Map<String,Object> attributiClaims=generaMappaAttributiClaims(userDetails);
		 Date d=generateExpirationDate(exipration);
		 return Jwts.builder().setClaims(attributiClaims).setExpiration(d).signWith(SignatureAlgorithm.HS256, secret).compact();
	 }
}
