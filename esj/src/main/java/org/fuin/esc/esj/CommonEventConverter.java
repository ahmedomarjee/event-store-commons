/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. <http://www.fuin.org/>
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
package org.fuin.esc.esj;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lt.emasina.esj.message.ReadEventCompleted;

import org.fuin.esc.api.CommonEvent;
import org.fuin.esc.spi.Deserializer;
import org.fuin.esc.spi.DeserializerRegistry;
import org.fuin.esc.spi.EnhancedMimeType;
import org.fuin.esc.spi.MetaDataAccessor;
import org.fuin.objects4j.common.Contract;

/**
 * Converts an ESJ {@link ReadEventCompleted} into an event.
 */
public final class CommonEventConverter {

    private final DeserializerRegistry deserRegistry;

    private final MetaDataAccessor metaDataAccessor;

    /**
     * Constructor with all mandatory data.
     * 
     * @param deserRegistry
     *            Deserializer registry.
     * @param metaDataAccessor
     *            Used to access an unknown type of meta data.
     */
    @SuppressWarnings("rawtypes")
    public CommonEventConverter(
            @NotNull final DeserializerRegistry deserRegistry,
            @NotNull final MetaDataAccessor metaDataAccessor) {

        Contract.requireArgNotNull("deserRegistry", deserRegistry);
        Contract.requireArgNotNull("metaDataAccessor", metaDataAccessor);

        this.deserRegistry = deserRegistry;
        this.metaDataAccessor = metaDataAccessor;
    }

    /**
     * Converts the given completed into a common event.
     * 
     * @param completed
     *            Completed result to convert.
     * 
     * @return Converted common event.
     */
    @SuppressWarnings("unchecked")
    public final CommonEvent convert(final ReadEventCompleted completed) {

        final UUID id = completed.getEventId();
        final String type = completed.getEventType();

        // The event store has no way of storing the mime type for the meta data
        // and therefore we just ask for a deserializer without content type.
        final Deserializer metaDeser = deserRegistry
                .getDeserializer(EsjEventStore.META_TYPE);
        final Object meta = metaDeser.unmarshal(completed.getResponseMeta()
                .toByteArray(), deserRegistry
                .getDefaultMimeType(EsjEventStore.META_TYPE));
        metaDataAccessor.init(meta);
        final String contentTypeStr = metaDataAccessor
                .getString("content-type");
        final EnhancedMimeType dataMimeType = EnhancedMimeType
                .create(contentTypeStr);

        // Use the data mime type from the meta data to deserialization
        final Deserializer dataDeser = deserRegistry.getDeserializer(type,
                dataMimeType);
        final Object data = dataDeser.unmarshal(completed.getResponseData()
                .toByteArray(), dataMimeType);

        return new CommonEvent(id.toString(), type, data, meta);

    }

}
