package com.vladimir.mailserver.repository;

import com.vladimir.mailserver.domain.Address;
import com.vladimir.mailserver.domain.MailUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Page<Address> findAllByUser(MailUser user, Pageable pageable);
    Address findAddressById(Long id);
}
