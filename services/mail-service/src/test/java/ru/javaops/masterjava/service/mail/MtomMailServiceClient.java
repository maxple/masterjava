package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableSet;
import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.developer.StreamingDataHandler;
import ru.javaops.web.WebStateException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class MtomMailServiceClient {

    public static void main(String[] args) throws MalformedURLException, WebStateException {
        /*Service service = Service.create(
                new URL("http://localhost:8085/mail/mailService?wsdl"),
                new QName("http://mail.javaops.ru/", "MailServiceImplService"));

        MailService mailService = service.getPort(MailService.class);

        String state = mailService.sendToGroup(ImmutableSet.of(new Addressee("masterjava@javaops.ru", null)), null,
                "Group mail subject", "Group mail body");
        System.out.println("Group mail state: " + state);

        GroupResult groupResult = mailService.sendBulk(ImmutableSet.of(
                new Addressee("Мастер Java <maxple1@gmail.com>"),
                new Addressee("Bad Email <bad_email.ru>")), "Bulk mail subject", "Bulk mail body");
        System.out.println("\nBulk mail groupResult:\n" + groupResult);*/

        Service service = Service.create(
                new URL("http://localhost:8085/mail/mailService?wsdl"),
                new QName("http://mail.javaops.ru/", "MailServiceImplService"));

        MTOMFeature feature = new MTOMFeature();
        MtomStreamingPortType port = service.getMtomStreamingPortTypePort(
                feature);
        Map<String, Object> ctxt=((BindingProvider)port).getRequestContext();
        ctxt.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 8192);
        DataHandler dh = new DataHandler(new
                FileDataSource("/tmp/example.jar"));
        port.fileUpload("/tmp/tmp.jar",dh);

        DataHandler dhn = port.fileDownload("/tmp/tmp.jar");
        StreamingDataHandler sdh = (StreamingDataHandler)dh;
        try{
            File file = new File("/tmp/tmp.jar");
            sdh.moveTo(file);
            sdh.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
