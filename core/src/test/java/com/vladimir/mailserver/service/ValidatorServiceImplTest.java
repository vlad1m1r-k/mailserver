package com.vladimir.mailserver.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ValidatorServiceImpl.class)
public class ValidatorServiceImplTest {
    @Autowired
    private ValidatorServiceImpl vsi;

    @Test
    public void testValidateUserData4argsSuccess() {
        Assert.assertTrue(vsi.validateUserData("name", "surname", "login", "pass"));
    }

    @Test
    public void testValidateUserData4argsNulls() {
        Assert.assertFalse(vsi.validateUserData(null, null, null, null));
        Assert.assertFalse(vsi.validateUserData(null, "surname", "login", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", null, "login", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "surname", null, "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "surname", "login", null));
    }

    @Test
    public void testValidateUserData4argsShort() {
        Assert.assertFalse(vsi.validateUserData("", "", "", ""));
        Assert.assertFalse(vsi.validateUserData("", "surname", "login", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "", "login", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "surname", "", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "surname", "login", ""));
    }

    @Test
    public void testValidateUserData4argsLong() {
        Assert.assertFalse(vsi.validateUserData("1234567890123456", "1234567890123456",
                "1234567890123456", "1234567890123456"));
        Assert.assertFalse(vsi.validateUserData("1234567890123456", "surname", "login", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "1234567890123456", "login", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "surname", "1234567890123456", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "surname", "login", "1234567890123456"));
    }

    @Test
    public void testValidateUserData3argsSuccess() {
        Assert.assertTrue(vsi.validateUserData("name", "surname", "pass"));
    }

    @Test
    public void testValidateUserData3argsNulls() {
        Assert.assertFalse(vsi.validateUserData(null, null, null));
        Assert.assertFalse(vsi.validateUserData(null, "surname", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", null, "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "surname", null));
    }

    @Test
    public void testValidateUserData3argsShort() {
        Assert.assertFalse(vsi.validateUserData("", "", ""));
        Assert.assertFalse(vsi.validateUserData("", "surname", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "surname", ""));
    }

    @Test
    public void testValidateUserData3argsLong() {
        Assert.assertFalse(vsi.validateUserData("1234567890123456", "1234567890123456", "1234567890123456"));
        Assert.assertFalse(vsi.validateUserData("1234567890123456", "surname", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "1234567890123456", "pass"));
        Assert.assertFalse(vsi.validateUserData("name", "surname", "1234567890123456"));
    }

    @Test
    public void testValidateUserData2argsSuccess() {
        Assert.assertTrue(vsi.validateUserData("name", "surname"));
    }

    @Test
    public void testValidateUserData2argsNulls() {
        Assert.assertFalse(vsi.validateUserData(null, null));
        Assert.assertFalse(vsi.validateUserData(null, "surname"));
        Assert.assertFalse(vsi.validateUserData("name", null));
    }

    @Test
    public void testValidateUserData2argsShort() {
        Assert.assertFalse(vsi.validateUserData("", ""));
        Assert.assertFalse(vsi.validateUserData("", "surname"));
        Assert.assertFalse(vsi.validateUserData("name", ""));
    }

    @Test
    public void testValidateUserData2argsLong() {
        Assert.assertFalse(vsi.validateUserData("1234567890123456", "1234567890123456"));
        Assert.assertFalse(vsi.validateUserData("1234567890123456", "surname"));
        Assert.assertFalse(vsi.validateUserData("name", "1234567890123456"));
    }

    @Test
    public void testValidateOrgNameSuccess() {
        Assert.assertTrue(vsi.validateOrgName("name"));
    }

    @Test
    public void testValidateOrgNameNulls() {
        Assert.assertFalse(vsi.validateOrgName(null));
    }

    @Test
    public void testValidateOrgNameShort() {
        Assert.assertFalse(vsi.validateOrgName(""));
    }

    @Test
    public void testValidateOrgNameLong() {
        Assert.assertFalse(vsi.validateOrgName("1234567890123456789012345678"));
    }

    @Test
    public void testValidateAliasValueSuccess() {
        Assert.assertTrue(vsi.validateAliasValue("name"));
    }

    @Test
    public void testValidateAliasValueNulls() {
        Assert.assertFalse(vsi.validateAliasValue(null));
    }

    @Test
    public void testValidateAliasValueShort() {
        Assert.assertFalse(vsi.validateAliasValue(""));
    }

    @Test
    public void testValidateAliasValueLong() {
        Assert.assertFalse(vsi.validateAliasValue("1234567890123456789012345678"));
    }

    @Test
    public void testValidateEmailAddressSuccess() {
        Assert.assertTrue(vsi.validateEmailAddress("asd@aswe"));
        Assert.assertTrue(vsi.validateEmailAddress("asd@aswe, asdx@asdf"));
        Assert.assertTrue(vsi.validateEmailAddress("asd@aswe, asdx@asdf, sdds@asdfff"));
    }

    @Test
    public void testValidateEmailAddressFail() {
        Assert.assertFalse(vsi.validateEmailAddress("**^&$@aswe"));
        Assert.assertFalse(vsi.validateEmailAddress("adfs@#$%^&"));
        Assert.assertFalse(vsi.validateEmailAddress("as d@aswe"));
        Assert.assertFalse(vsi.validateEmailAddress("@aswe"));
        Assert.assertFalse(vsi.validateEmailAddress("adsf@"));
        Assert.assertFalse(vsi.validateEmailAddress("@"));
        Assert.assertFalse(vsi.validateEmailAddress("adsf@sdd, ,"));
        Assert.assertFalse(vsi.validateEmailAddress("adsf@sdd, @,"));
        Assert.assertFalse(vsi.validateEmailAddress("adsf@sdd, @asdf,"));
        Assert.assertFalse(vsi.validateEmailAddress("adsf@sdd, #@!!@asdf,"));
        Assert.assertFalse(vsi.validateEmailAddress("adsf@sdd, asdf@%^&*&,"));
    }

    @Test
    public void testValidateAddressNameSuccess() {
        Assert.assertTrue(vsi.validateAddressName("name"));
    }

    @Test
    public void testValidateAddressNameNulls() {
        Assert.assertFalse(vsi.validateAddressName(null));
    }

    @Test
    public void testValidateAddressNameShort() {
        Assert.assertFalse(vsi.validateAddressName(""));
    }

    @Test
    public void testValidateAddressNameLong() {
        Assert.assertFalse(vsi.validateAddressName("1234567890123456789012345678"));
    }
}