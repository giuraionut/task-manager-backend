package com.example.api.auth;

import com.example.api.security.UserRole;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository(value = "fake")
public class AppUserDaoService implements AppUserDao {


    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserDaoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Optional<AppUser> selectAppUserByUserName(String userName) {
        return getAppUsers().stream().filter(appUser -> userName.equals(appUser.getUsername())).findFirst();

    }

    private List<AppUser> getAppUsers() {
        List<AppUser> appUsers = Lists.newArrayList(
                new AppUser(
                        UserRole.MEMBER.getGrantedAuthorities(),
                        passwordEncoder.encode("password"),
                        "anna",
                        true,
                        true,
                        true,
                        true
                ),
                new AppUser(
                        UserRole.LEADER.getGrantedAuthorities(),
                        passwordEncoder.encode("password"),
                        "linda",
                        true,
                        true,
                        true,
                        true
                ),
                new AppUser(
                        UserRole.TEMP_LEADER.getGrantedAuthorities(),
                        passwordEncoder.encode("password"),
                        "mark",
                        true,
                        true,
                        true,
                        true
                )
        );

        return appUsers;
    }

}
