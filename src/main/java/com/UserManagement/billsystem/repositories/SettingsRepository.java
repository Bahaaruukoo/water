package com.UserManagement.billsystem.repositories;


import com.UserManagement.billsystem.entities.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {
}
