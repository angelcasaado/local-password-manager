package com.localpasswordmanager.repository;

import com.localpasswordmanager.model.SecurityTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityTrackerRepository extends JpaRepository<SecurityTracker, Long> {
}
