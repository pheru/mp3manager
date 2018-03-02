package de.pheru.media.core.io.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class XmlIO implements FileIO {

    private static final Logger LOGGER = LogManager.getLogger(XmlIO.class);

    @Override
    public <T> T read(final File file, final Class<T> type) throws IOException {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(type);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setEventHandler(event -> {
                LOGGER.error("Could not unmarshal file \"" + file.getName() + "\"!", event.getLinkedException());
                return false;
            });
            final StreamSource streamSource = new StreamSource(file.getAbsolutePath());
            return unmarshaller.unmarshal(streamSource, type).getValue();
        } catch (final JAXBException e) {
            if (e.getLinkedException() instanceof FileNotFoundException) {
                throw (FileNotFoundException) e.getLinkedException();
            }
            throw new IOException(e);
        }
    }

    @Override
    public <T> void write(final File file, final Class<T> type, final T t) throws IOException {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(type);
            final Marshaller marshaller = jaxbContext.createMarshaller();
            final JAXBElement<T> jaxbElement = new JAXBElement<>(new QName(type.getSimpleName()), type, t);
            marshaller.marshal(jaxbElement, file);
        } catch (final JAXBException e) {
            throw new IOException(e);
        }
    }
}
