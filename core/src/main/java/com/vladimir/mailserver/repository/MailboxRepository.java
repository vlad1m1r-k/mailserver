package com.vladimir.mailserver.repository;

import com.vladimir.mailserver.domain.Mailbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailboxRepository extends JpaRepository<Mailbox, Long> {
}
