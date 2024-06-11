package com.solvd.service;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.Properties;

public class YahooMailService {

    private final String username;
    private final String password;
    private final Properties properties;

    public YahooMailService(String username, String password) {
        this.username = username;
        this.password = password;
        this.properties = new Properties();
        this.properties.put("mail.store.protocol", "imap");
        this.properties.put("mail.imaps.auth", "true");
        this.properties.put("mail.imaps.host", "imap.mail.yahoo.com");
        this.properties.put("mail.imaps.port", "993");
    }

    private Session createSession() {
        return Session.getInstance(properties);
    }

    private Store connectToStore(Session session) throws MessagingException {
        Store store = session.getStore("imaps");
        store.connect(username, password);
        return store;
    }

    private Folder openInbox(Store store) throws MessagingException {
        Folder folder = store.getFolder("Inbox");
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    private MimeMessage getLatestMessage(Folder folder) throws MessagingException {
        int count = folder.getMessageCount();
        return (MimeMessage) folder.getMessage(count);
    }

    public String getLatestMessageText() {
        String text;
        try {
            Session session = createSession();
            Store store = connectToStore(session);
            Folder folder = openInbox(store);
            MimeMessage msg = getLatestMessage(folder);
            text = getText(msg);

            folder.close(false);
            store.close();
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
        return text;
    }

    private String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }

}
