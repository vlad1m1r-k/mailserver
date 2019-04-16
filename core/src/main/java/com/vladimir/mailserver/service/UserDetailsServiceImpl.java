package com.vladimir.mailserver.service;

import com.vladimir.mailserver.domain.MailUser;
import com.vladimir.mailserver.domain.UserStatus;
import com.vladimir.mailserver.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        MailUser user = userRepository.findMailUserByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("User not found.");
        }
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority(user.getRole().toString()));
        return new User(user.getLogin(), user.getPassword(), true,
                true, true, user.getStatus() == UserStatus.ACTIVE, roles);
    }
}
