package com.vladimir.mailserver.repository;

import com.vladimir.mailserver.domain.AcceptedDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcceptedDomainRepository extends JpaRepository<AcceptedDomain, Long> {
    AcceptedDomain findAcceptedDomainById(Long id);
    AcceptedDomain findAcceptedDomainByName(String name);
}
