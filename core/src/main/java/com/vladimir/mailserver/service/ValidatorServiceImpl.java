package com.vladimir.mailserver.service;

import org.springframework.stereotype.Service;

@Service
public class ValidatorServiceImpl implements ValidatorService {
    @Override
    public boolean validateUserData(String name, String surname, String login, String password) {
        return validateData(name)
                && validateData(surname)
                && validateLogin(login)
                && validateData(password);
    }

    @Override
    public boolean validateUserData(String name, String surname) {
        return validateData(name) && validateData(surname);
    }

    @Override
    public boolean validateOrgName(String name) {
        return validateString25(name);
    }

    @Override
    public boolean validateAliasValue(String value) {
        return validateString25(value);
    }

    @Override
    public boolean validateEmailAddress(String email) {
        return email.matches("^(?: *(?:[\\w]|[.])+?@(?:[\\w]|[.])+? *?(?:,|$))+");
    }

    @Override
    public boolean validateAddressName(String name) {
        return validateString25(name);
    }

    @Override
    public boolean validateUserData(String name, String surname, String newPassword) {
        return validateData(name) && validateData(surname) && validateData(newPassword);
    }

    private boolean validateData(String data) {
        return data != null && data.length() >= 3 && data.length() <= 15;
    }

    private boolean validateLogin(String login) {
        return login != null && login.length() >= 3 && login.length() <= 15 && login.matches("^[\\d\\w]+\\.?[\\d\\w]+$");
    }

    private boolean validateString25(String value) {
        return value != null && value.length() > 1 && value.length() <= 25;
    }
}
