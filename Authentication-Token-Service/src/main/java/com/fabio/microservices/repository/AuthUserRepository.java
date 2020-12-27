package com.fabio.microservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fabio.microservices.model.AuthUser;

public interface AuthUserRepository extends JpaRepository<AuthUser,Integer>{
	
	@Query(value="FROM AuthUser au WHERE au.username=:username")
	AuthUser getAuthForUsername(@Param("username")String username);
}
