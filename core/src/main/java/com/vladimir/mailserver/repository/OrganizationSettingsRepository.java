package com.vladimir.mailserver.repository;

import com.vladimir.mailserver.domain.OrganizationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrganizationSettingsRepository extends JpaRepository<OrganizationSettings, Long> {
    @Query("SELECT o FROM OrganizationSettings o")
    OrganizationSettings findOne();
}
