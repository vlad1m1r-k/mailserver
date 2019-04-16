package com.vladimir.mailserver.repository;

import com.vladimir.mailserver.domain.MailboxAlias;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailAliasRepository extends JpaRepository<MailboxAlias, Long> {
}
