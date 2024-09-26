package ru.d4nik.carparts.components.kiselev.adapters.arlanavto;

import com.opencsv.CSVWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.d4nik.carparts.components.kiselev.domain.PriceList;
import ru.d4nik.carparts.components.kiselev.domain.Product;
import ru.d4nik.carparts.components.kiselev.infra.MyMailProperies;

import java.io.*;

@Component
@AllArgsConstructor
public class ArlanAvtoAdapter {
//    private static final String SAVE_DIRECTORY = "/home/danil/projects/tmp";
    private static final String DELIVERY_DAYS = "3";

    private JavaMailSender mailSender;

    public void sendPriceList(PriceList priceList) {
        var fileToAttach = writeToCsv(priceList);
        sendMailWithAttachment("danil.ivanov.v@ya.ru", "Прайс лист от Киселева Евгения", fileToAttach);
    }

//    private void saveToFile(ByteArrayResource byteArrayResource) {
//        try {
//            try (OutputStream fileOutputStreamoutputStream = new FileOutputStream(SAVE_DIRECTORY + "/Прайс лист на отправку.csv")) {
//                fileOutputStreamoutputStream.write(byteArrayResource.getByteArray());
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void sendMailWithAttachment(String to, String subject, InputStreamSource fileToAttach) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setFrom(MyMailProperies.USERNAME, "evgeniy.kiselev@ya.ru");
            helper.setSubject(subject);
            helper.setText("Прайс лист от Киселева Евгения");
            helper.addAttachment("Прайс лист от Киселева Евгения.csv", fileToAttach);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // send email
        try {
            mailSender.send(mimeMessage);
        } catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }

    private ByteArrayResource writeToCsv(PriceList priceList) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(os), ';', '"', '"', "\n");
            writeHeaders(writer);
            priceList.products().forEach(product -> writer.writeNext(productToCsv(product)));
            writer.close();
            return new ByteArrayResource(os.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeHeaders(CSVWriter writer) {
        writer.writeNext(new String[]{"Наименование", "Номер артикула", "Бренд", "Кол-во", "Цена", "Срок в днях", "Кратность"});
    }

    private String[] productToCsv(Product product) {
        return new String[]{
                product.name(),
                product.article(),
                product.brand(),
                String.valueOf(product.stocks()),
                String.valueOf(product.price()),
                DELIVERY_DAYS,
                String.valueOf(product.quantityInSet())};
    }
}
