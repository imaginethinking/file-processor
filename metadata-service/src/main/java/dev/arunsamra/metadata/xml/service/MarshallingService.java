package dev.arunsamra.metadata.xml.service;

import dev.arunsamra.metadata.xml.exception.MarshallingException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.OutputStream;

@Service
public class MarshallingService {

    public <T> void marshal(T object, OutputStream outputStream) {
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(object, outputStream);
        } catch (JAXBException exception) {
            throw new MarshallingException("Failed to marshal object to XML", exception);
        }
    }
}
