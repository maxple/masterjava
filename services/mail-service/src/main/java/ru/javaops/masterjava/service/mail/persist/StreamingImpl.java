package ru.javaops.masterjava.service.mail.persist;

import com.sun.xml.ws.developer.StreamingAttachment;
import com.sun.xml.ws.developer.StreamingDataHandler;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;
import java.io.File;

@StreamingAttachment(parseEagerly = true, memoryThreshold = 40000L)
@MTOM
@WebService(name = "MtomStreaming",
        serviceName = "MtomStreamingService",
        targetNamespace = "http://mail.javaops.ru/")
public class StreamingImpl {

    // Use @XmlMimeType to map to DataHandler on the client side
    @Oneway
    @WebMethod
    public void fileUpload(String fileName, @XmlMimeType("application/octet-stream") DataHandler data) {
        try {
            StreamingDataHandler dh = (StreamingDataHandler) data;
            File file = new File(fileName);
            dh.moveTo(file);
            dh.close();
        } catch (Exception e) {
            throw new WebServiceException(e);
        }
    }

    @XmlMimeType("application/octet-stream")
    @WebMethod
    public DataHandler fileDownload(String filename) {
        return new DataHandler(new FileDataSource(filename));
    }
}
