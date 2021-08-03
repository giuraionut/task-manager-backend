package com.example.api.user;

import com.example.api.security.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));
    }

    //------------------------------------------------------------------------------------------------------------------
    public boolean emailExists(User user) {
        return this.userRepository.findUserByEmail(user.getEmail()).isPresent();
    }
    //------------------------------------------------------------------------------------------------------------------
    public boolean usernameExists(User user) {
        return this.userRepository.findUserByUsername(user.getUsername()).isPresent();
    }
    //------------------------------------------------------------------------------------------------------------------
    public boolean exists(String userId) {
        return this.userRepository.findById(userId).isPresent();
    }
    //------------------------------------------------------------------------------------------------------------------
    public void add(User newUser) {
        newUser.setPassword(this.passwordEncoder.encode(newUser.getPassword()));
        newUser.setAccountNonExpired(true);
        newUser.setAccountNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        newUser.setEnabled(true);
        newUser.setGrantedAuthorities(UserRole.USER.getGrantedAuthorities());
        newUser.setTasksId(new HashSet<>());
        newUser.setTeamId(null);
        this.userRepository.insert(newUser);
    }
    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void update(User user)
    {
        this.userRepository.save(user);
    }
    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void setTeamId(String userId, String teamId) {
        User user = getUserById(userId);
        user.setTeamId(teamId);
        userRepository.save(user);
    }
    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void setGrantedAuthorities(String userId, Set<SimpleGrantedAuthority> authorities) {
        User user = getUserById(userId);
        user.setGrantedAuthorities(authorities);
        userRepository.save(user);
    }
    //------------------------------------------------------------------------------------------------------------------
    public void deleteTeam(String userId) {
        User user = getUserById(userId);
        user.setTeamId(null);
        userRepository.save(user);
    }
    //------------------------------------------------------------------------------------------------------------------
    public User getUserById(String userId) {
        return this.userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User with id " + userId + " not found!"));
    }
    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void changeName(User user, String userId) {
        User changedUser = getUserById(userId);
        changedUser.setUsername(user.getUsername());
        userRepository.save(changedUser);
    }
    //------------------------------------------------------------------------------------------------------------------
}
