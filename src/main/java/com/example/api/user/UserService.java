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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

    public User getByRefreshToken(String refreshToken) {
        Optional<User> userByRefreshToken = this.userRepository.findUserByRefreshToken(refreshToken);
        return userByRefreshToken.orElse(null);
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
        newUser.setTeamId(null);
        newUser.setRefreshToken(UUID.randomUUID().toString());
        newUser.setAvatar("../../assets/user_avatar/default_male_avatar.jpg");
        this.userRepository.insert(newUser);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void setTeamId(String userId, String teamId) {
        User user = getUserById(userId);
        user.setTeamId(teamId);
        this.userRepository.save(user);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void setAvatar(String userId, String path) {
        User user = getUserById(userId);
        user.setAvatar(path);
        this.userRepository.save(user);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void setGrantedAuthorities(String userId, Set<SimpleGrantedAuthority> authorities) {
        User user = getUserById(userId);
        user.setGrantedAuthorities(authorities);
        this.userRepository.save(user);
    }

    //------------------------------------------------------------------------------------------------------------------
    public void deleteTeam(String userId) {
        User user = getUserById(userId);
        user.setTeamId(null);
        user.setGrantedAuthorities(UserRole.USER.getGrantedAuthorities());
        this.userRepository.save(user);
    }

    //------------------------------------------------------------------------------------------------------------------
    public User getUserById(String userId) {
        Optional<User> userById = this.userRepository.findById(userId);
        return userById.orElse(null);

    }

    //------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void changeName(User user, String userId) {
        User changedUser = getUserById(userId);
        changedUser.setUsername(user.getUsername());
        this.userRepository.save(changedUser);
    }

    //------------------------------------------------------------------------------------------------------------------
    public String checkUserReg(User user) {
        if (user.getUsername().isEmpty()) {
            return "no username";
        }
        if (user.getEmail().isEmpty()) {
            return "no email";

        }
        if (user.getPassword().isEmpty()) {
            return "no password";
        }
        return "ok";
    }

    public void updateUser(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        this.userRepository.save(user);
    }
}
