package com.vladimir.mailserver.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class MailboxTest {
    private Mailbox mailbox;

    @Before
    public void init() {
        mailbox = new Mailbox();
    }

    @Test
    public void testGetDefaultAliasSuccess() throws NoSuchFieldException, IllegalAccessException {
        Field aliases = Mailbox.class.getDeclaredField("aliases");
        aliases.setAccessible(true);
        Set<MailboxAlias> aliasSet = new HashSet<>();
        aliasSet.add(new MailboxAlias(null, null, false, null));
        aliasSet.add(new MailboxAlias(null, null, false, null));
        aliasSet.add(new MailboxAlias(null, null, true, null));
        aliasSet.add(new MailboxAlias(null, null, false, null));
        aliasSet.add(new MailboxAlias(null, null, false, null));
        aliases.set(mailbox, aliasSet);
        MailboxAlias defaultAlias = mailbox.getDefaultAlias();
        Assert.assertTrue(defaultAlias.isDefault());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetDefaultAliasNotFound() throws NoSuchFieldException, IllegalAccessException {
        Field aliases = Mailbox.class.getDeclaredField("aliases");
        aliases.setAccessible(true);
        Set<MailboxAlias> aliasSet = new HashSet<>();
        aliasSet.add(new MailboxAlias(null, null, false, null));
        aliasSet.add(new MailboxAlias(null, null, false, null));
        aliasSet.add(new MailboxAlias(null, null, false, null));
        aliasSet.add(new MailboxAlias(null, null, false, null));
        aliasSet.add(new MailboxAlias(null, null, false, null));
        aliases.set(mailbox, aliasSet);
        MailboxAlias defaultAlias = mailbox.getDefaultAlias();
    }
}