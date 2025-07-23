package com.spring.clinic.repository;

import com.spring.clinic.entity.Notifications;
import com.spring.clinic.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {
    List<Notifications> findByUser(Users userId);
    Optional<Notifications> findByIdAndUser(long id, Users user);
    @Query("SELECT n from Notifications n where n.user=:userId and n.isRead=false ")
    List<Notifications> findByUserAndReadIsFalse(Users userId);
    List<Notifications> findByReferenceId(String referenceId);
}
