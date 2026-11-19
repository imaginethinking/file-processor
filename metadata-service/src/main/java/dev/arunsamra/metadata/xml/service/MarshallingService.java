package dev.arunsamra.metadata.xml.service;

import dev.arunsamra.metadata.xml.exception.InvalidXmlException;
import dev.arunsamra.metadata.xml.exception.MarshallingException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
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

    public <T> T unmarshal(InputStream inputStream, Class<T> type) {
        try {
            XMLInputFactory factory = XMLInputFactory.newFactory();
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

            XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
            Unmarshaller unmarshaller = JAXBContext.newInstance(type).createUnmarshaller();

            return unmarshaller.unmarshal(reader, type).getValue();
        } catch (JAXBException | XMLStreamException exception) {
            throw new InvalidXmlException("Failed to unmarshal XML", exception);
        }
    }
}
