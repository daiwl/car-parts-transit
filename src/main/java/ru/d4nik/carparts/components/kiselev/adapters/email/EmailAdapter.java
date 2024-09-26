package ru.d4nik.carparts.components.kiselev.adapters.email;

import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.search.FlagTerm;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.imap.IMAPMessage;
import org.springframework.stereotype.Component;
import ru.d4nik.carparts.components.kiselev.domain.PriceListExcelFile;
import ru.d4nik.carparts.components.kiselev.infra.MyMailProperies;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

@Component
@Slf4j
public class EmailAdapter {
    public static final String PRICE_LISTS_FOLDER = "Прайс Листы";
    final Properties properties;

    public EmailAdapter() {
        properties = new Properties();

        // server setting
        properties.put("mail.imaps.ssl.trust", "imap.yandex.ru");
        properties.put("mail.imaps.host", "imap.yandex.ru");
        properties.put("mail.imaps.port", 993);
        properties.put("mail.store.protocol", "smtps");
        properties.put("mail.mime.charset", "UTF-8");
        properties.put("mail.mime.allowutf8", "true");
    }

    public Optional<PriceListExcelFile> loadPriceListExcelFile() {
        return downloadEmailAttachments();
    }

    private Optional<PriceListExcelFile> downloadEmailAttachments() {
        Session session = Session.getDefaultInstance(properties);

        try {
            // connects to the message store
            Store store = session.getStore("imaps");
            store.connect(MyMailProperies.USERNAME, MyMailProperies.PASSWORD);

            // opens the inbox folder
            Folder folder = store.getFolder(PRICE_LISTS_FOLDER);
            folder.open(Folder.READ_WRITE);

            FlagTerm unreadFlag = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message[] unreadMessages = folder.search(unreadFlag);

            log.info("Получено {} новых писем", unreadMessages.length);

            PriceListExcelFile priceListExcel = null;
            for (Message message : unreadMessages) {
                ((IMAPMessage) message).setPeek(true);

                Date sentDate = message.getSentDate();
                String contentType = message.getContentType();

                // store attachment file name, separated by comma

                if (contentType.contains("multipart")) {
                    // content may contain attachments
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                            priceListExcel = PriceListExcelFile.builder()
                                    .fileName(MimeUtility.decodeText(part.getFileName()))
                                    .date(sentDate.toInstant())
                                    .inputStream(part.getInputStream())
                                    .build();
                        }
                    }
                }
                markMessageAsSeen(message, folder);
            }
            folder.close(false);
            store.close();
            return Optional.ofNullable(priceListExcel);
        } catch (NoSuchProviderException ex) {
            log.error("Провайдер не найден", ex);
        } catch (MessagingException ex) {
            log.error("Ошибка подключения к провайдеру", ex);
        } catch (IOException ex) {
            log.error("Ошибка ввода вывода", ex);
        }
        return Optional.empty();
    }

    private static void markMessageAsSeen(Message message, Folder folder) throws MessagingException {
        folder.setFlags(new Message[]{message}, new Flags(Flags.Flag.SEEN), true);
    }

//    public static void downloadEmailAttachments(String host, Integer port, String userName, String password) {
//        Properties properties = new Properties();
//
//        // server setting
//        properties.put("mail.imaps.ssl.trust", "imap.yandex.ru");
//        properties.put("mail.imaps.host", host);
//        properties.put("mail.imaps.port", port);
//        properties.put("mail.store.protocol", "smtps");
//        properties.put("mail.mime.charset", "UTF-8");
//        properties.put("mail.mime.allowutf8", "true");
//
//
//        Session session = Session.getDefaultInstance(properties);
//
//        try {
//            // connects to the message store
//            Store store = session.getStore("imaps");
//            store.connect(userName, password);
//
//            // opens the inbox folder
//            Folder folder = store.getFolder("EVG");
//            folder.open(Folder.READ_WRITE);
//
//            FlagTerm unreadFlag = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
//            Message[] unreadMessages = folder.search(unreadFlag);
//
//            log.info("Получено {} новых писем", unreadMessages.length);
//
//            for (Message message : unreadMessages) {
//                ((IMAPMessage) message).setPeek(true);
//
//                String sentDate = message.getSentDate().toString();
//
//                Address[] fromAddress = message.getFrom();
//                String from = fromAddress[0].toString();
//                String subject = message.getSubject();
//
//                String contentType = message.getContentType();
//                String messageContent = "";
//
//                // store attachment file name, separated by comma
//                StringBuilder attachFiles = new StringBuilder();
//
//                if (contentType.contains("multipart")) {
//                    // content may contain attachments
//                    Multipart multiPart = (Multipart) message.getContent();
//                    int numberOfParts = multiPart.getCount();
//                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
//                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
//                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
//                            // this part is attachment
//                            String fileName = MimeUtility.decodeText(part.getFileName());
//                            attachFiles.append(fileName).append(", ");
//                            part.saveFile(SAVE_DIRECTORY + File.separator + fileName);
//                        } else {
//                            // this part may be the message content
//                            messageContent = part.getContent().toString();
//                        }
//                    }
//
//                    if (attachFiles.length() > 1) {
//                        attachFiles = new StringBuilder(attachFiles.substring(0, attachFiles.length() - 2));
//                    }
//                } else if (contentType.contains("text/plain") || contentType.contains("text/html")) {
//                    Object content = message.getContent();
//                    if (content != null) {
//                        messageContent = content.toString();
//                    }
//                }
//                markMessageAsSeen(message, folder);
//            }
//
//            // disconnect
//            folder.close(false);
//            store.close();
//        } catch (NoSuchProviderException ex) {
//            System.out.println("No provider for pop3.");
//            ex.printStackTrace();
//            log.error("No provider for pop3.", ex);
//        } catch (MessagingException ex) {
//            System.out.println("Could not connect to the message store");
//            ex.printStackTrace();
//            log.error("Could not connect to the message store", ex);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            log.error("IO error", ex);
//        }
//    }
}
