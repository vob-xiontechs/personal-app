package com.dev.backendapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.backendapi.entity.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity,Long>{

    Optional<UserEntity> findByEmail(String email);
    
}
