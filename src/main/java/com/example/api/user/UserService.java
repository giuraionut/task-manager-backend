package com.example.api.user;

import com.example.api.security.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(@Qualifier("user") UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));
    }

    public void userRegister(User newUser) {
        userRepository.findUserByEmail(newUser.getEmail())
                .ifPresentOrElse(
                        user -> {
                            System.out.println(newUser + " tried to register, but email already exists");
                        },
                        () -> {
                            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
                            newUser.setAccountNonExpired(true);
                            newUser.setAccountNonLocked(true);
                            newUser.setCredentialsNonExpired(true);
                            newUser.setEnabled(true);
                            newUser.setGrantedAuthorities(UserRole.MEMBER.getGrantedAuthorities());
                            userRepository.insert(newUser);
                            System.out.println(newUser + " registered successfully");
                        }
                );
    }
}
