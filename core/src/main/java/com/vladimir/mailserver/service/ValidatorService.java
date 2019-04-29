package com.vladimir.mailserver.service;

public interface ValidatorService {
    boolean validateUserData(String name, String surname, String login, String password);
    boolean validateUserData(String name, String surname, String newPassword);
    boolean validateUserData(String name, String surname);
    boolean validateOrgName(String name);
    boolean validateAliasValue(String value);
    boolean validateEmailAddress(String email);
    boolean validateAddressName(String name);
}
