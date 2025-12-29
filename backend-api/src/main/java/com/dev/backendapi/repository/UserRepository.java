package com.dev.backendapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.backendapi.entity.UserEntity;


@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>{

    Optional<UserEntity> findByEmail(String email);
    
}
