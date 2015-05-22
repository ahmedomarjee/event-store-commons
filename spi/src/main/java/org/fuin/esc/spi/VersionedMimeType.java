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
package org.fuin.esc.spi;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import javax.activation.MimeTypeParseException;
import javax.validation.constraints.NotNull;

import org.fuin.objects4j.common.NeverNull;
import org.fuin.objects4j.common.Nullable;

/**
 * Enhances the {@link javax.activation.MimeType} class with equals and hash
 * code based on the base type, sub type, encoding and version.
 */
public final class VersionedMimeType extends javax.activation.MimeType {

    /** Version parameter name. */
    public static final String VERSION = "version";

    /** Encoding parameter name. */
    public static final String ENCODING = "encoding";

    /**
     * Default constructor for de-serialization.
     */
    public VersionedMimeType() {
        super();
    }

    /**
     * Constructor with all data.
     * 
     * @param str
     *            Contains base type, sub type and parameters.
     * 
     * @throws MimeTypeParseException
     *             If the string is not valid.
     */
    public VersionedMimeType(@NotNull final String str)
            throws MimeTypeParseException {
        super(str);
    }

    /**
     * Constructor with primary and sub type (no parameters).
     * 
     * @param primary
     *            Primary type.
     * @param sub
     *            Sub type.
     * 
     * @throws MimeTypeParseException
     *             If the primary type or sub type is not a valid token
     */
    public VersionedMimeType(@NotNull final String primary,
            @NotNull final String sub) throws MimeTypeParseException {
        super(primary, sub);
    }

    /**
     * Constructor with primary, sub type and encoding.
     * 
     * @param primary
     *            Primary type.
     * @param sub
     *            Sub type.
     * @param encoding
     *            Encoding.
     * 
     * @throws MimeTypeParseException
     *             If the primary type or sub type is not a valid token
     */
    public VersionedMimeType(@NotNull final String primary,
            @NotNull final String sub, @Nullable final Charset encoding)
            throws MimeTypeParseException {
        super(primary, sub);
        if (encoding != null) {
            super.setParameter(ENCODING, encoding.name());
        }
    }

    /**
     * Constructor with all data.
     * 
     * @param primary
     *            Primary type.
     * @param sub
     *            Sub type.
     * @param encoding
     *            Encoding.
     * @param version
     *            Version.
     * @param params
     *            Other parameters than version and encoding.
     * 
     * @throws MimeTypeParseException
     *             If the primary type or sub type is not a valid token
     */
    public VersionedMimeType(@NotNull final String primary,
            @NotNull final String sub, @Nullable final Charset encoding,
            @Nullable final String version, @Nullable final Map<String, String> params) throws MimeTypeParseException {
        super(primary, sub);
        if (encoding != null) {
            super.setParameter(ENCODING, encoding.name());
        }
        if (version != null) {
            super.setParameter(VERSION, version);
        }
        if (params != null) {
            final Iterator<String> it = params.keySet().iterator();
            while( it.hasNext()) {
                final String key = it.next();
                final String value = params.get(key);
                if (key.equals(ENCODING)) {
                    throw new IllegalArgumentException("Setting encoding with the parameters is not allowed. Use the 'encoding' argument instead.");
                }
                if (key.equals(VERSION)) {
                    throw new IllegalArgumentException("Setting version with the parameters is not allowed. Use the 'version' argument instead.");
                }
                super.setParameter(key, value);
            }
        }
    }

    /**
     * Returns the version from the parameters.
     * 
     * @return Version or <code>null</code> if not available.
     */
    @Nullable
    public final String getVersion() {
        return getParameter(VERSION);
    }
    
    /**
     * Unsupported operation.
     */
    @Override    
    public final void setPrimaryType(final String primary) {
        throw new UnsupportedOperationException("Changing the primary type after construction is not allowed!");
    }
    
    /**
     * Unsupported operation.
     */
    @Override
    public final void setSubType(final String sub) {
        throw new UnsupportedOperationException("Changing the sub type after construction is not allowed!");
    }

    /**
     * Unsupported operation.
     */
    @Override
    public void setParameter(final String name, final String value) {
        throw new UnsupportedOperationException("Changing parameters after construction is not allowed!");
    }
    
    /**
     * Returns the encoding from the parameters.
     * 
     * @return Encoding or <code>null</code> as default if not available.
     */
    @Nullable
    public final Charset getEncoding() {
        final String parameter = getParameter(ENCODING);
        if (parameter == null) {
            return null;
        }
        return Charset.forName(parameter);
    }

    // CHECKSTYLE:OFF Generated code

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((getPrimaryType() == null) ? 0 : getPrimaryType().hashCode());
        result = prime * result
                + ((getSubType() == null) ? 0 : getSubType().hashCode());
        result = prime * result
                + ((getEncoding() == null) ? 0 : getEncoding().hashCode());
        result = prime * result
                + ((getVersion() == null) ? 0 : getVersion().hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof VersionedMimeType))
            return false;
        VersionedMimeType other = (VersionedMimeType) obj;
        if (getPrimaryType() == null) {
            if (other.getPrimaryType() != null)
                return false;
        } else if (!getPrimaryType().equals(other.getPrimaryType()))
            return false;
        if (getSubType() == null) {
            if (other.getSubType() != null)
                return false;
        } else if (!getSubType().equals(other.getSubType()))
            return false;
        if (getEncoding() == null) {
            if (other.getEncoding() != null)
                return false;
        } else if (!getEncoding().equals(other.getEncoding()))
            return false;
        if (getVersion() == null) {
            if (other.getVersion() != null)
                return false;
        } else if (!getVersion().equals(other.getVersion()))
            return false;
        return true;
    }

    // CHECKSTYLE:ON

    /**
     * Creates an instance with all data and exceptions wrapped to runtime
     * exceptions.
     * 
     * @param str
     *            Contains base type, sub type and parameters.
     */
    @NeverNull
    public static VersionedMimeType create(@NotNull final String str) {
        try {
            return new VersionedMimeType(str);
        } catch (final MimeTypeParseException ex) {
            throw new RuntimeException("Failed to create versioned mime type: "
                    + str, ex);
        }
    }

    /**
     * Creates an instance with primary and sub type (no parameters) and
     * exceptions wrapped to runtime exceptions.
     * 
     * @param primary
     *            Primary type.
     * @param sub
     *            Sub type.
     */
    @NeverNull
    public static VersionedMimeType create(@NotNull final String primary,
            @NotNull final String sub) {
        return create(primary, sub, null, null, null);
    }

    /**
     * Creates an instance with primary, sub type and encoding and exceptions
     * wrapped to runtime exceptions.
     * 
     * @param primary
     *            Primary type.
     * @param sub
     *            Sub type.
     * @param encoding
     *            Encoding.
     */
    @NeverNull
    public static VersionedMimeType create(@NotNull final String primary,
            @NotNull final String sub, final Charset encoding) {
        return create(primary, sub, encoding, null, null);
    }

    /**
     * Creates an instance with all data and exceptions wrapped to runtime
     * exceptions.
     * 
     * @param primary
     *            Primary type.
     * @param sub
     *            Sub type.
     * @param encoding
     *            Encoding.
     * @param version
     *            Version.
     * @param parameters
     *            Additional parameters.
     */
    @NeverNull
    public static VersionedMimeType create(@NotNull final String primary,
            @NotNull final String sub, final Charset encoding,
            final String version, final Map<String, String> parameters) {
        try {
            return new VersionedMimeType(primary, sub, encoding, version, parameters);
        } catch (final MimeTypeParseException ex) {
            throw new RuntimeException("Failed to create versioned mime type: "
                    + primary + "/" + sub, ex);
        }
    }

}
