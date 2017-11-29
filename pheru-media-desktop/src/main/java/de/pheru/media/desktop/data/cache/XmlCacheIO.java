package de.pheru.media.desktop.data.cache;

import de.pheru.media.desktop.EntryPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class XmlCacheIO implements CacheIO {

    private static final Logger LOGGER = LogManager.getLogger(XmlCacheIO.class);

    @Override
    public void writeCacheFile(final File file, final Cache cache) throws IOException {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(Cache.class);
            final Marshaller marshaller = jaxbContext.createMarshaller();

            final JAXBElement<Cache> jaxbElement = new JAXBElement<>(new QName("cache"), Cache.class, cache);
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(jaxbElement, file);
        } catch (final JAXBException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Cache readCacheFile(final File file) throws IOException {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(Cache.class);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setEventHandler(event -> {
//                LOGGER.error("Invalid cache-file \"" + file.getName() + "\"!", event.getLinkedException());
                return false;
            });
            final StreamSource streamSource = new StreamSource(file.getAbsolutePath());
            return unmarshaller.unmarshal(streamSource, Cache.class).getValue();
        } catch (final JAXBException e) {
            throw new IOException(e);
        }
    }

}
