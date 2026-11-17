package dev.arunsamra.fileprocessor.xml;

import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;

@Component
public class XmlMapper {

    public <T> void marshal(T object, OutputStream outputStream) {
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(object, outputStream);

        } catch (JAXBException exception) {
            throw new FileProcessingException(
                    "Failed to marshal object to XML",
                    exception
            );
        }
    }

    public <T> T unmarshal(InputStream inputStream, Class<T> type) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);

            Unmarshaller unmarshaller = context.createUnmarshaller();

            return type.cast(unmarshaller.unmarshal(inputStream));

        } catch (JAXBException exception) {
            throw new FileProcessingException(
                    "Failed to unmarshal XML",
                    exception
            );
        }
    }
}
