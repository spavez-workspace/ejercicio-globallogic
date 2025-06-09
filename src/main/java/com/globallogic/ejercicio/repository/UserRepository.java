package com.globallogic.ejercicio.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.globallogic.ejercicio.model.UserExample;

@Repository
public interface UserRepository extends JpaRepository<UserExample, UUID>{

	Optional<UserExample> findByName(String name);
	
	boolean existsByName(String name);
	
}
