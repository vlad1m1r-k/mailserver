package com.vladimir.mailserver.repository;

import com.vladimir.mailserver.domain.MailUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<MailUser, Long> {
    MailUser findMailUserByLogin(String login);
}
