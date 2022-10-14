package uuu.tf.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import uuu.tf.entity.Course;
import uuu.tf.entity.Schedule;
import uuu.tf.entity.TFException;
import uuu.tf.entity.TripDay;

/**
 *
 * @author Administrator
 */
public class MailService {

    public static void sendHelloMailWithLogo(String to, String scheduleId) throws TFException {
        if (to == null) {
            to = "rapunzel0716@hotmail.com";
        }

        if (scheduleId == null || !scheduleId.matches("\\d+")) {
            throw new TFException("傳入的scheduleId有誤");
        }

        SchedulesService service = new SchedulesService();
        Schedule s = service.getScheduleById(Integer.parseInt(scheduleId));
        if (s == null) {
            
            throw new TFException("找不到此行程");
        }

        //以下為寄件所需的SMTP伺服器與帳號設定，這裡使用gmail的SMTP Server
//        final String host = "smtp.gmail.com";
//        final int port = 587;
//        final String username = "username@gmail.com";
//        final String password = "password";//your password        
        final String host = "www.hibox.hinet.net";
        final int port = 25;
        final String username = "username@test.com.tw";
        final String password = "password";//your password
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            // 以下建立message物件作為mail的內容
            Message message = new MimeMessage(session);

            InternetAddress from;
            try {
                from = new InternetAddress(username, "TripFruit-行程規劃", "utf-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(MailService.class.getName()).log(Level.SEVERE, null, ex);
                from = new InternetAddress(username);
            }
            message.setFrom(from);//虛擬寄件人

            // Set 收件email: header field of the header.
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("TripFruit 行程寄送 行程名稱:" + s.getTripName());

            // This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");

            // first part (the html)            
            BodyPart messageBodyPart = new MimeBodyPart();

            String ipAddress = java.net.InetAddress.getLocalHost().getHostAddress();

            String htmlText = "<font color=\"blue\"><b><b>========== 行程名稱 ==========</b></b></font><br>"
                    + s.getTripName() + " " + s.getFirstDay() + " ~ " + s.getLastDay() + "<br>"
                    + "<font color=\"blue\"><b><b>========== 行程總覽 ==========</b></b></font><br>";
            for (TripDay day : s.getTripDayList()) {
                int days = s.getDayIndex(day) + 1;
                htmlText += "<b>DAY" + days + " (" + day.getTripdate() + " " + day.getTripdate().getDayOfWeek() + ")</b><br><div style='margin-left: 20px;'>";
                for (int i = 0; i < day.size(); i++) {
                    Course course = day.getCourse(i);
                    if (i != day.size() - 1) {
                        htmlText += course.getPlace().getName() + " - ";
                    } else {
                        htmlText += course.getPlace().getName();
                    }
                }
                htmlText += "</div>";
            }

            htmlText += "<font color=\"blue\"><b><b>========== 行程細節 ==========</b></b></font><br>";
            for (TripDay day : s.getTripDayList()) {
                int days = s.getDayIndex(day) + 1;
                htmlText += "<b>DAY" + days + " (" + day.getTripdate() + " " + day.getTripdate().getDayOfWeek() + ")</b><br><div style='margin-left: 20px;'>";
                for (int i = 0; i < day.size(); i++) {
                    htmlText += "<div style='border: 1px solid #e7e7e7;width:400px;'>";
                    Course course = day.getCourse(i);
                    htmlText += "<b>" + course.getPlace().getName() + "</b><br>";
                    htmlText += "<b>開始時間:</b>" + course.getStarttime() + "<br>";
                    htmlText += "<b>停留時間:</b>" + course.getStay() + "分<br>";
                    if (i != day.size() - 1) {
                        htmlText += "<b>抵達下一站交通時間預估：</b>" + course.getRouteTimeString() + "(" + course.getRouteType().getDescription() + ")" + "<br>";
                    }
                    if (course.getPlace().getOpening_hours() != null && course.getPlace().getOpening_hours().length > 0) {
                        htmlText += ("<div class='placecardOpenTime''><b>營業時間:</b><br><div style='margin-left:40px;text-align: left;'>");
                        for (String t : course.getPlace().getOpening_hours()) {
                            htmlText += (t.replace("\\n", "<br>") + "<br>");
                        }
                        htmlText += ("</div></div>");
                    }
                    htmlText += "<b>景點照片:</b><br>"
                            + "<img src='" + course.getPlace().getPhoto() + "' width=\"250\" height=\"150\"><br>";
                    htmlText += "</div>";

                }
                htmlText += "</div>";
            }

            messageBodyPart.setContent(htmlText, "text/html;charset=utf-8");
            // add it
            multipart.addBodyPart(messageBodyPart);
            /*
            // second part (the image)
            String filename = "shoppingbag.png";
            messageBodyPart = new MimeBodyPart();
            
            //取得網站上的圖檔
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL url = classLoader.getResource("/"+filename);
            System.out.println("url = " + url);
            String imagePath = filename;
            if(url!=null){
                imagePath = url.getPath();
            }
            System.out.println("imagePath = " + imagePath);            
            
            DataSource fds = new FileDataSource(imagePath);
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setFileName(filename);
            messageBodyPart.setHeader("Content-ID", "<image>");

            // add image to the multipart
            multipart.addBodyPart(messageBodyPart);
             */
            // put everything together
            message.setContent(multipart);
            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");
        } catch (Exception ex) {
            System.out.println("ex = " + ex);
            if (ex.getCause() != null) {
                System.out.println("ex.getCause():" + ex.getCause());
            }
        }
    }
}
