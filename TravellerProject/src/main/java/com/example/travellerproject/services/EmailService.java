package com.example.travellerproject.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService{

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String mailAuth;
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.properties.mail.smtp.stattls.enable}")
    private String ttlEnabled;
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Value("${spring.mail.password}")
    private String senderPassword;





    public void sendEmailNew(String recipient, String subject , String msg) {
        Message message = prepareMessage(recipient, subject, msg);
        try {
            Transport.send(message);
        } catch (MessagingException e){
            e.printStackTrace();
            System.out.println("Email send failed - " + e.getMessage());
        }
    }
    private Message prepareMessage(String recipient, String subject, String msg){
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", mailAuth);
        properties.put("mail.smtp.starttls.enable", ttlEnabled);
        properties.put("mail.smtp.host",host);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(senderEmail,senderPassword);
            }
        });

        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(msg);
            return message;
            }
        catch(MessagingException e ){
            e.printStackTrace();
            return null;
        }
    }

}
