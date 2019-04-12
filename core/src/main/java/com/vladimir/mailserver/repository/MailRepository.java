package com.vladimir.mailserver.repository;

import com.vladimir.mailserver.domain.Mail;
import com.vladimir.mailserver.domain.MailType;
import com.vladimir.mailserver.domain.Mailbox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MailRepository extends JpaRepository<Mail, Long> {
    Page<Mail> findAllByMailboxAndTypeAndIsDeleted(Mailbox mailbox, MailType mailType, Boolean isDeleted, Pageable pageable);

    @Query("SELECT m FROM Mail m WHERE m.mailbox = ?1 AND m.isDeleted = ?2 AND (m.type = ?3 OR m.type = ?4)")
    Page<Mail> findDeleted(Mailbox mailbox, Boolean isDeleted, MailType mailType, MailType otherType, Pageable pageable);
    Mail findMailById(Long id);
}
