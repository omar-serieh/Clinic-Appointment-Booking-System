package com.spring.clinic.repository;

import com.spring.clinic.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    @Query("SELECT u FROM Users u WHERE u.role = 'PATIENT' AND u.userId = :userId")
    Users findPatientByUserId(@Param("userId") long userId);
    List<Users> findAllByRole(Users.UerRole role);
}

