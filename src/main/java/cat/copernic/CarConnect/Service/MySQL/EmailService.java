package cat.copernic.CarConnect.Service.MySQL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Servicio que gestiona el envío de correos electrónicos. Proporciona métodos
 * para enviar correos tanto en texto plano como en formato HTML.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía un correo electrónico en texto plano.
     *
     * @param to La dirección de correo electrónico del destinatario.
     * @param subject El asunto del correo electrónico.
     * @param text El cuerpo del correo en texto plano.
     * @throws RuntimeException Si ocurre un error al enviar el correo.
     */
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false); // 'false' indica que es texto plano
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo de texto plano", e);
        }
    }

    /**
     * Envía un correo electrónico con contenido HTML.
     *
     * @param to La dirección de correo electrónico del destinatario.
     * @param subject El asunto del correo electrónico.
     * @param htmlContent El cuerpo del correo en formato HTML.
     * @throws RuntimeException Si ocurre un error al enviar el correo.
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // 'true' indica que es HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo HTML", e);
        }
    }
}
