package ln.vps.wxpub.util;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class MailUtil {

    private static final MailUtil UTIL = new MailUtil();

    private String email;
    private Authenticator auth;
    private boolean sessionDebug;

    public static MailUtil getInstance() {
        return UTIL;
    }

    private MailUtil() {
        try {
            Properties sysProp = System.getProperties();
            Properties mailProp = new Properties();
            mailProp.load(MailUtil.class.getClassLoader().getResourceAsStream("mail.properties"));
            mailProp.entrySet().forEach(entry -> sysProp.put(entry.getKey(), entry.getValue()));
            this.email = mailProp.getProperty("mail.acc.email");
            final String user = mailProp.getProperty("mail.acc.user");
            final String password = mailProp.getProperty("mail.acc.password");
            this.sessionDebug = Boolean.getBoolean(mailProp.getProperty("mail.session.debug"));
            auth = new Authenticator() {
                private PasswordAuthentication pauth = new PasswordAuthentication(
                        user, password);

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return pauth;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void mail(String subject, String content, List<String> toList,
                     List<String> ccList) {
        try {
            Session session = Session.getDefaultInstance(
                    System.getProperties(), auth);
            if (sessionDebug) {
                session.setDebug(true);
            }
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.addRecipients(Message.RecipientType.TO, convertToAddresses(toList));
            message.addRecipients(Message.RecipientType.CC, convertToAddresses(ccList));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private Address[] convertToAddresses(List<String> list) {
        return list.stream().map(addr -> {
            InternetAddress address;
            try {
                address = new InternetAddress(addr);
            } catch (AddressException e) {
                address = null;
            }
            return address;
        }).toArray(Address[]::new);
    }

    public void mail(String subject, String content, String toAddress,
                     List<String> ccList) {
        mail(subject, content, Collections.singletonList(toAddress), ccList);
    }

    public void mail(String subject, String content, String toAddress,
                     String ccAddress) {
        mail(subject, content, Collections.singletonList(toAddress),
                Collections.singletonList(ccAddress));
    }

    public void mail(String subject, String content, String toAddress) {
        mail(subject, content, Collections.singletonList(toAddress),
                Collections.emptyList());
    }

}
