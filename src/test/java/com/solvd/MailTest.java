package com.solvd;

import com.solvd.service.YahooMailService;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MailTest {

    @Test
    public void testYahooEmail() {
        String username = "";
        String password = ""; // 3-rd party app access password Yahoo

        YahooMailService yahooMailService = new YahooMailService(username, password);

        // Test https://sendtestemail.com/ email
        Assert.assertTrue(yahooMailService.getLatestMessageText().contains("Congratulations!"), "Could not fetch last message");
    }
}
