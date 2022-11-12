package com.onoprienko.ioc.entity;

public class UserServiceWithoutSetters implements UserService {
    private IMailService mailService;

    @Override
    public void activateUsers() {

    }
}
