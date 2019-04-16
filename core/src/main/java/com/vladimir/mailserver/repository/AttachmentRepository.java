package com.vladimir.mailserver.repository;

import com.vladimir.mailserver.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Attachment findAttachmentById(Long id);
}
