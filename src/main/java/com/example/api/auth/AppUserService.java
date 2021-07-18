package com.example.api.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class AppUserService implements UserDetailsService {

    private final AppUserDao appUserDao;

    @Autowired
    public AppUserService(@Qualifier("fake") AppUserDao appUserDao) {
        this.appUserDao = appUserDao;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return appUserDao.selectAppUserByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", userName)));
    }
}
