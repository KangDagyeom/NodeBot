package com.javaee.test1.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.Random;

public class EmailService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String EMAIL_SENDER = "hungndth04416@fpt.edu.vn";
    private static final String PASSWORD = dotenv.get("APP_PASSWORD");

    public static String sendOtp(String recipientEmail) {
        String otp = String.format("%06d", new Random().nextInt(999999));

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_SENDER, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_SENDER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("🔐 Mã OTP Xác Minh");

            // Nội dung HTML
            // Nội dung HTML của email
            String htmlContent = "<!DOCTYPE html>"
                    + "<html><head>"
                    + "<style>"
                    + ".email-container { font-family: Arial, sans-serif; max-width: 500px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; background-color: #f9f9f9; }"
                    + ".header { text-align: center; font-size: 20px; font-weight: bold; color: #333; }"
                    + ".otp { font-size: 24px; font-weight: bold; color: #007bff; text-align: center; margin: 20px 0; }"
                    + ".footer { font-size: 12px; color: #666; text-align: center; margin-top: 20px; }"
                    + ".button { display: block; width: 200px; margin: 20px auto; padding: 10px; background-color: #007bff; color: white; text-align: center; text-decoration: none; font-weight: bold; border-radius: 5px; }"
                    + "</style>"
                    + "</head><body>"
                    + "<div class='email-container'>"
                    + "<div class='header'>🔐 Mã OTP Xác Minh</div>"
                    + "<p>Xin chào,</p>"
                    + "<p>Bạn vừa yêu cầu đặt lại mật khẩu. Dưới đây là mã OTP của bạn:</p>"
                    + "<div class='otp'>" + otp + "</div>"
                    + "<p>Vui lòng nhập mã này trong vòng 5 phút để tiếp tục.</p>"
                    + "<div class='footer'>Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.</div>"
                    + "</div></body></html>";


            // Đặt kiểu nội dung là HTML
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(message);

            return otp;
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

