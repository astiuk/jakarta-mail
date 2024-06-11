package com.solvd;

import com.solvd.enums.EmailService;
import com.solvd.service.YahooMailService;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MailTest {

    @DataProvider
    public Object[][] emailProvider() {
        return new Object[][] {
                {EmailService.YAHOO, "test@yahoo.com", "password"},
                {EmailService.GMAIL, "test@gmail.com", "password"}
        };
    }

    @Test(dataProvider = "emailProvider")
    public void testYahooEmail(EmailService emailService, String username, String password) {
        YahooMailService yahooMailService = new YahooMailService(emailService, username, password);

        // Test https://sendtestemail.com/ email
        Assert.assertTrue(yahooMailService.waitForNewEmailByTitle("SendTestEmail", 5000, 1).contains("Congratulations!"), "Could not fetch last message");
    }
}
