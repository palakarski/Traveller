package com.example.travellerproject.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {
/*
    nov nachin -> ne raboti vzimaneto ot aplication properties
    ostanaloto bachkashe , da se pogledne na novo

    @Value("${mail.auth}")
    private String mailAuth;
    @Value("${mail.host}")
    private String host;
    @Value("${mail.ttl}")
    private String ttlEnabled;
    @Value("${mail.senderEmail}")
    private String senderEmail;
    @Value("${mail.senderPassword}")
    private String senderPassword;


    public void sendEmail(String recipient, String subject , String msg) {

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
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable","smtp.gmail.com");
        properties.put("mail.smtp.host","true");
        properties.put("mail.smtp.port", "587");
        String myAccount = "stefeanpvivan1998@gmail.com";
        String password = "CODMW@codmw2";
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(myAccount,password);
            }
        });

        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccount));
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

 */
// star nachin ot youTube raboti
    public void sendEmail(String recepient,String subject , String msg){
        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        String myAccount = "stefeanpvivan1998@gmail.com";
        String password = "CODMW@codmw2";
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccount,password);
            }
        });
        Message message = prepareMessage(session,myAccount,recepient,subject,msg);
        try {
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("greshka pri prashtane na email");
        }
    }
    private static Message prepareMessage(Session session,String myAccount,String recepient,String messege, String subject){

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccount));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
            message.setSubject(subject);
            message.setText(messege);
            return message;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
