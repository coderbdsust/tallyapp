package com.udayan.tallyapp.security;

import com.udayan.tallyapp.user.User;
import com.udayan.tallyapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String param) throws UsernameNotFoundException {
        Optional<User> userOptional = repository.findByUsernameOrEmailOrMobileNo(param);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("Username or Email is incorrect");

        User user = userOptional.get();

        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User not verified yet, Please check email");
        }

        if (!user.isAccountNonLocked()) {
            throw new UsernameNotFoundException("User account is locked");
        }
        return user;
    }
}
