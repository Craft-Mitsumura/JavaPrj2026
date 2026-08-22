package jp.co.sss.shop.service;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class MailService {
 
    @Autowired
    private JavaMailSender sender;
 
    public void sendMail(String name,String email,String message , String subject){
 
        SimpleMailMessage mail = new SimpleMailMessage();
 
        mail.setTo("sagarmatha603@gmail.com");
        mail.setSubject("New Review");
 
        mail.setText(
                "Name : "+name+
                "\nEmail : "+email+
                "\n\nMessage :\n"+message+
                "\n\nsubject : \n"+subject
        );
 
        sender.send(mail);
    }
}