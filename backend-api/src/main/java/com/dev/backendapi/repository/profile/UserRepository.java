package com.dev.backendapi.repository.profile;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dev.backendapi.entity.profile.UserEntity;


@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>{

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUserId(String userId);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE " +
           "(:search IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:verified IS NULL OR u.isAccountVerified = :verified)")
    Page<UserEntity> findBySearchAndVerified(@Param("search") String search, @Param("verified") Boolean verified, Pageable pageable);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE " +
           "(:search IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:verified IS NULL OR u.isAccountVerified = :verified)")
    long countBySearchAndVerified(@Param("search") String search, @Param("verified") Boolean verified);
}
