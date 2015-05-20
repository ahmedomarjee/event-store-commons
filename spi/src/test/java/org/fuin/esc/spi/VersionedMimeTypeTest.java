package org.fuin.esc.spi;

import static org.fest.assertions.Assertions.assertThat;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.activation.MimeTypeParseException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link  VersionedMimeType} class.
 */
// CHECKSTYLE:OFF Test
public class VersionedMimeTypeTest {

    private VersionedMimeType testee;

    @Before
    public void setup() throws MimeTypeParseException {
        testee = new VersionedMimeType("application/xml;version=1.0.2;encoding=utf-8");
    }

    @After
    public void teardown() {
        testee = null;
    }

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(VersionedMimeType.class).suppress(Warning.NULL_FIELDS, Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    public void testConstrcutionPrimarySub() throws MimeTypeParseException {
        
        // PREPARE & TEST
        final VersionedMimeType testee = new VersionedMimeType("application", "json");
        
        //VERIFY
        assertThat(testee.getPrimaryType()).isEqualTo("application");
        assertThat(testee.getSubType()).isEqualTo("json");
        assertThat(testee.getVersion()).isNull();
        assertThat(testee.getEncoding()).isNull();
    }

    @Test
    public void testConstrcutionPrimarySubEncoding() throws MimeTypeParseException {
        
        // PREPARE & TEST
        final VersionedMimeType testee = new VersionedMimeType("application", "json", Charset.forName("utf-8"));
        
        //VERIFY
        assertThat(testee.getPrimaryType()).isEqualTo("application");
        assertThat(testee.getSubType()).isEqualTo("json");
        assertThat(testee.getVersion()).isNull();
        assertThat(testee.getEncoding()).isEqualTo(Charset.forName("UTF-8"));
    }
    
    @Test
    public void testConstrcutionAllArgs() throws MimeTypeParseException {
        
        // PREPARE & TEST
        final Map<String, String> params = new HashMap<String, String>();
        params.put("a", "1");
        final VersionedMimeType testee = new VersionedMimeType("application", "json", Charset.forName("utf-8"), "1.0.2", params);
        
        //VERIFY
        assertThat(testee.getPrimaryType()).isEqualTo("application");
        assertThat(testee.getSubType()).isEqualTo("json");
        assertThat(testee.getEncoding()).isEqualTo(Charset.forName("utf-8"));
        assertThat(testee.getParameter(VersionedMimeType.ENCODING)).isEqualTo("UTF-8");
        assertThat(testee.getVersion()).isEqualTo("1.0.2");
        assertThat(testee.getParameter(VersionedMimeType.VERSION)).isEqualTo("1.0.2");
        assertThat(testee.getParameter("a")).isEqualTo("1");
        assertThat(testee.getParameters().size()).isEqualTo(3);
    }
    
    
    @Test
    public void testGetter() {
        assertThat(testee.getPrimaryType()).isEqualTo("application");
        assertThat(testee.getSubType()).isEqualTo("xml");
        assertThat(testee.getVersion()).isEqualTo("1.0.2");
        assertThat(testee.getEncoding()).isEqualTo(Charset.forName("utf-8"));
    }

}
