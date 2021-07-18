package com.example.api.auth;

import java.util.Optional;

public interface AppUserDao {

     Optional<AppUser> selectAppUserByUserName(String userName);

}
