/**
 * Copyright (C) 2013 Future Invent Informationsmanagement GmbH. All rights
 * reserved. <http://www.fuin.org/>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fuin.esc.spi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.activation.MimeTypeParseException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serializes and deserializes a class of a given type that only has one version
 * and is always "application/xml" with "utf-8" encoded content.
 */
public final class XmlDeSerializer implements SerializerDeserializer {

    private static final Logger LOG = LoggerFactory
            .getLogger(XmlDeSerializer.class);

    private final String type;

    private final VersionedMimeType mimeType;

    private final Marshaller marshaller;

    private final Unmarshaller unmarshaller;

    /**
     * Constructor without XML adapter.
     * 
     * @param type
     *            Type that can be serialized/deserialized.
     * @param classesToBeBound
     *            Classes to use for the JAXB context.
     */
    public XmlDeSerializer(final String type,
            final Class<?>... classesToBeBound) {
        this(type, null, classesToBeBound);
    }

    /**
     * Constructor with type and JAXB context classes.
     * 
     * @param type
     *            Type that can be serialized/deserialized.
     * @param adapters
     *            Adapters to associate with the JAXB context or
     *            <code>null</code>.
     * @param classesToBeBound
     *            Classes to use for the JAXB context.
     */
    public XmlDeSerializer(final String type,
            final XmlAdapter<?, ?>[] adapters,
            final Class<?>... classesToBeBound) {
        super();
        this.type = type;
        this.mimeType = mimeType();
        try {
            final JAXBContext ctx = JAXBContext.newInstance(classesToBeBound);
            marshaller = ctx.createMarshaller();
            unmarshaller = ctx.createUnmarshaller();
            if ((adapters == null) || (adapters.length == 0)) {
                LOG.debug("No adapters set for: " + type);
            } else {
                for (XmlAdapter<?, ?> adapter : adapters) {
                    LOG.debug("Set adapter for: " + type + " [" + adapter + "]");
                    marshaller.setAdapter(adapter);
                    unmarshaller.setAdapter(adapter);
                }
            }
            unmarshaller.setEventHandler(new ValidationEventHandler() {
                @Override
                public boolean handleEvent(final ValidationEvent event) {
                    if (event.getSeverity() > 0) {
                        if (event.getLinkedException() == null) {
                            throw new RuntimeException(
                                    "Error unmarshalling the data: "
                                            + event.getMessage());
                        }
                        throw new RuntimeException(
                                "Error unmarshalling the data", event
                                        .getLinkedException());
                    }
                    return true;
                }
            });
        } catch (final JAXBException ex) {
            throw new RuntimeException(
                    "Error initializing JAXB helper classes", ex);
        }
    }

    private VersionedMimeType mimeType() {
        try {
            return new VersionedMimeType("application", "xml",
                    Charset.forName("utf-8"));
        } catch (final MimeTypeParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public final String getType() {
        return type;
    }

    @Override
    public final VersionedMimeType getMimeType() {
        return mimeType;
    }

    @Override
    public final byte[] marshal(final Object obj) {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            final Writer writer = new OutputStreamWriter(bos, getMimeType()
                    .getEncoding());
            marshaller.marshal(obj, writer);
            return bos.toByteArray();
        } catch (final JAXBException ex) {
            throw new RuntimeException("Error serializing data", ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T unmarshal(final byte[] data) {
        try {
            final Reader reader = new InputStreamReader(
                    new ByteArrayInputStream(data), getMimeType().getEncoding());
            return (T) unmarshaller.unmarshal(reader);
        } catch (final JAXBException ex) {
            throw new RuntimeException("Error de-serializing data", ex);
        }
    }

}
