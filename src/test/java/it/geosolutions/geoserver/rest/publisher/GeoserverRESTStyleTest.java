/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007-2016 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package it.geosolutions.geoserver.rest.publisher;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTStyle;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.jdom.Element;
import org.jdom.Namespace;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;

/**
 * Testcase for publishing layers on geoserver. We need a running GeoServer to
 * properly run the tests. If such geoserver instance cannot be contacted, tests
 * will be skipped.
 *
 * @author etj
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GeoserverRESTStyleTest extends GeoserverRESTTest {

  private static final Logger LOG = Logger.getLogger(GeoserverRESTStyleTest.class.getName());

    

    @Before
    public void setup() throws Exception {
        if (enabled()) {
            deleteAll();
        }
    }

    @Test
    public void testStyles() throws IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());

        final String STYLENAME = "restteststyle";
        File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();

        // insert style
        assertTrue(publisher.publishStyle(sldFile));
        assertTrue(reader.existsStyle(STYLENAME));

        assertFalse(publisher.publishStyle(sldFile));
        assertTrue(reader.existsStyle(STYLENAME));

        // insert style v110
        final String STYLENAMEV110 = "restteststyleV110";
        File sldFileV110 = new ClassPathResource("testdata/" + STYLENAMEV110 + ".sld").getFile();

        assertTrue(publisher.publishStyle(sldFileV110, STYLENAMEV110, true));
        assertTrue(reader.existsStyle(STYLENAMEV110));

        assertFalse(publisher.publishStyle(sldFileV110, STYLENAMEV110, true));

        RESTStyle style = reader.getStyle(STYLENAME);
        assertEquals(STYLENAME, style.getName());
        assertNull(style.getWorkspace());

        String sld = reader.getSLD(STYLENAME);
        assertNotNull(sld);

        Element styleEl = JDOMBuilder.buildElement(sld);
        assertNotNull(styleEl);

        Namespace SLDNS = Namespace.getNamespace("sld", "http://www.opengis.net/sld");

        try {
            assertEquals(STYLENAME, styleEl.getChild("NamedLayer", SLDNS)
                    .getChild("Name", SLDNS).getText());
            assertEquals("STYLE FOR TESTING PURPOSES",
                    styleEl.getChild("NamedLayer", SLDNS)
                            .getChild("UserStyle", SLDNS)
                            .getChild("Title", SLDNS).getText());
        } catch (NullPointerException npe) {
            fail("Error in SLD");
        }

        // assertEquals(1475, sld.length());
        assertEquals(2 + UNDELETABLE_STYLES.size(), reader.getStyles().size());
    }

    protected void cleanupTestStyle(final String styleName) {
        // dry run delete to work in a known state
        if (reader.existsStyle(styleName)) {
            LOG.info("Clearing stale test style " + styleName);
            boolean ok = publisher.removeStyle(styleName);
            if (!ok) {
                fail("Could not unpublish style " + styleName);
            }
        }
        assertFalse("Cleanup failed", reader.existsStyle(styleName));
    }

    @Test
    public void testPublishDeleteStyleFile() throws FileNotFoundException,
            IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();
        // Assume.assumeTrue(enabled);
        final String styleName = "restteststyle";

        File sldFile = new ClassPathResource("testdata/restteststyle.sld")
                .getFile();

        final String STYLENAMEV110 = "restteststyleV110";
        File sldFileV110 = new ClassPathResource("testdata/" + STYLENAMEV110 + ".sld")
                .getFile();

        // known state?
        cleanupTestStyle(styleName);
        cleanupTestStyle(STYLENAMEV110);

        // test insert
        boolean published = publisher.publishStyle(sldFile); // Will take the
        // name from sld
        // contents
        assertTrue("publish() failed", published);
        assertTrue(reader.existsStyle(styleName));

        sldFile = new ClassPathResource("testdata/restteststyle2.sld").getFile();
        published = publisher.updateStyle(sldFile, styleName); // update
        assertTrue("update() failed", published);

        // test delete
        boolean ok = publisher.removeStyle(styleName);
        assertTrue("Unpublish() failed", ok);
        assertFalse(reader.existsStyle(styleName));

        published = publisher.publishStyle(sldFileV110, STYLENAMEV110, true);

        assertTrue("publish() failed", published);
        assertTrue(reader.existsStyle(STYLENAMEV110));

        boolean updated = publisher.updateStyle(sldFileV110, STYLENAMEV110, true);
        assertTrue("update() failed", updated);

        // test delete
        ok = publisher.removeStyle(STYLENAMEV110);
        assertTrue("Unpublish() failed", ok);
        assertFalse(reader.existsStyle(STYLENAMEV110));
    }

    @Test
    public void testPublishDeleteStyleString()
            throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
        // Assume.assumeTrue(enabled);
        String styleName = "restteststyle";

        File sldFile = new ClassPathResource("testdata/restteststyle.sld")
                .getFile();

        // known state?
        cleanupTestStyle(styleName);

        // test insert
        String sldContent = IOUtils.toString(new FileInputStream(sldFile));

        boolean published = publisher.publishStyle(sldContent); // Will take the
        // name from sld
        // contents
        assertTrue("publish() failed", published);
        assertTrue(reader.existsStyle(styleName));
        // test delete
        boolean ok = publisher.removeStyle(styleName);
        assertTrue("Unpublish() failed", ok);
        assertFalse(reader.existsStyle(styleName));

        styleName = "restteststyle_with_name";
        // test insert with name
        published = publisher.publishStyle(sldContent, styleName); // Will set
        // the name
        assertTrue("publish() failed", published);
        assertTrue(reader.existsStyle(styleName));
        String newSldContent = sldContent.replace(
                "<sld:Title>STYLE FOR TESTING PURPOSES</sld:Title>",
                "<sld:Title>MODIFIED STYLE FOR TESTING</sld:Title>");
        published = publisher.updateStyle(newSldContent, styleName); // update
        assertTrue("publish() failed", published);

        // test delete
        ok = publisher.removeStyle(styleName);
        assertTrue("Unpublish() failed", ok);
        assertFalse(reader.existsStyle(styleName));

    }

    @Test
    public void testUpdateDefaultStyle()
            throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        String storeName = "resttestshp";
        String layerName = "cities";

        final String styleName = "restteststyle";
        {
            File sldFile = new ClassPathResource("testdata/restteststyle.sld")
                    .getFile();
            cleanupTestStyle(styleName);
            boolean sldpublished = publisher.publishStyle(sldFile); // Will take
            // the name
            // from sld
            // contents
            assertTrue("style publish() failed", sldpublished);
            assertTrue(reader.existsStyle(styleName));
        }

        final String styleName2 = "restteststyle2";
        {
            File sldFile = new ClassPathResource("testdata/restteststyle2.sld")
                    .getFile();
            cleanupTestStyle(styleName2);
            boolean sldpublished = publisher.publishStyle(sldFile, styleName2);
            assertTrue("style publish() failed", sldpublished);
            assertTrue(reader.existsStyle(styleName2));
        }

        File zipFile = new ClassPathResource("testdata/resttestshp.zip")
                .getFile();

        assertTrue(publisher.createWorkspace(DEFAULT_WS));

        // test insert
        boolean published = publisher.publishShp(DEFAULT_WS, storeName,
                layerName, zipFile, "EPSG:4326", styleName);
        assertTrue("publish() failed", published);
        assertTrue(existsLayer(layerName));

        {
            RESTLayer layer = reader.getLayer(layerName);
            LOG.info("Layer style is " + layer.getDefaultStyle());
            assertEquals(styleName, layer.getDefaultStyle());
        }

        GSLayerEncoder le = new GSLayerEncoder();
        le.setDefaultStyle(styleName2);
        publisher.configureLayer(DEFAULT_WS, layerName, le);

        {
            RESTLayer layer = reader.getLayer(layerName);
            LOG.info("Layer style is " + layer.getDefaultStyle());
            assertEquals(styleName2, layer.getDefaultStyle());
        }

        // remove layer and datastore
        boolean dsRemoved = publisher.removeDatastore(DEFAULT_WS, storeName,
                true);
        assertTrue("removeDatastore() failed", dsRemoved);
    }

    @Test
    public void testStylesInWorkspace() throws IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        final String WORKSPACE = "testWorkspace";
        final String STYLENAME = "restteststyle";
        File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();

        publisher.createWorkspace(WORKSPACE);

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(0, reader.getStyles(WORKSPACE).size());

        // insert style
        assertTrue("Error inserting style", publisher.publishStyleInWorkspace(WORKSPACE, sldFile));
        assertTrue("Style does not exist in workspace", reader.existsStyle(WORKSPACE, STYLENAME));

        // this assertion is not enforced by geoserver, which is quite lenient in searching names
        //assertFalse("Style should not be global", reader.existsStyle(STYLENAME));
        // insert style again
        assertFalse("Dup style not trapped", publisher.publishStyleInWorkspace(WORKSPACE, sldFile));
        assertTrue("Style does not exist in workspace (2)", reader.existsStyle(WORKSPACE, STYLENAME));
        // this assertion is not enforced by geoserver, which is quite lenient in searching names
        //assertFalse("Style should not be global (2)", reader.existsStyle(STYLENAME));

        String sld = reader.getSLD(WORKSPACE, STYLENAME);
        assertNotNull(sld);

        RESTStyle style = reader.getStyle(WORKSPACE, STYLENAME);
        assertEquals(STYLENAME, style.getName());
        assertEquals(WORKSPACE, style.getWorkspace());

        Element styleEl = JDOMBuilder.buildElement(sld);
        assertNotNull(styleEl);

        Namespace SLDNS = Namespace.getNamespace("sld", "http://www.opengis.net/sld");

        try {

            assertEquals(STYLENAME, styleEl.getChild("NamedLayer", SLDNS)
                    .getChild("Name", SLDNS).getText());
            assertEquals(
                    "STYLE FOR TESTING PURPOSES",
                    styleEl.getChild("NamedLayer", SLDNS)
                            .getChild("UserStyle", SLDNS)
                            .getChild("Title", SLDNS).getText());
        } catch (NullPointerException npe) {
            fail("Error in SLD");
        }

        // assertEquals(1475, sld.length());
        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(1, reader.getStyles(WORKSPACE).size());
    }

    @Test
    public void testRemoveStylesInWorkspace() throws IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        final String WORKSPACE = "testWorkspace";
        final String STYLENAME = "restteststyle";
        final File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();

        publisher.createWorkspace(WORKSPACE);

        assertEquals(0, reader.getStyles(WORKSPACE).size());

        // insert style
        assertTrue(publisher.publishStyleInWorkspace(WORKSPACE, sldFile));
        assertEquals(1, reader.getStyles(WORKSPACE).size());
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));

        // remove style
        assertTrue(publisher.removeStyleInWorkspace(WORKSPACE, STYLENAME, true));
        assertEquals(0, reader.getStyles(WORKSPACE).size());
        assertFalse(reader.existsStyle(WORKSPACE, STYLENAME));
    }

    @Test
    public void testStylesInWorkspaceRaw() throws IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        final String WORKSPACE = "testWorkspace";
        final String STYLENAME = "restteststyle";
        File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();

        publisher.createWorkspace(WORKSPACE);

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(0, reader.getStyles(WORKSPACE).size());

        // insert style
        assertTrue(publisher.publishStyleInWorkspace(WORKSPACE, sldFile, STYLENAME, true));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));
        assertFalse(reader.existsStyle(STYLENAME));

        // insert style again
        assertFalse(publisher.publishStyleInWorkspace(WORKSPACE, sldFile, STYLENAME, true));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));
        assertFalse(reader.existsStyle(STYLENAME));

        String sld = reader.getSLD(WORKSPACE, STYLENAME);
        assertNotNull(sld);

        RESTStyle style = reader.getStyle(WORKSPACE, STYLENAME);
        assertEquals(STYLENAME, style.getName());
        assertEquals(WORKSPACE, style.getWorkspace());

        Element styleEl = JDOMBuilder.buildElement(sld);
        assertNotNull(styleEl);

        Namespace SLDNS = Namespace.getNamespace("sld",
                "http://www.opengis.net/sld");

        try {

            assertEquals("country", styleEl.getChild("NamedLayer", SLDNS)
                    .getChild("Name", SLDNS).getText());
            assertEquals(
                    "STYLE FOR TESTING PURPOSES",
                    styleEl.getChild("NamedLayer", SLDNS)
                            .getChild("UserStyle", SLDNS)
                            .getChild("Title", SLDNS).getText());
        } catch (NullPointerException npe) {
            fail("Error in SLD");
        }

        // assertEquals(1475, sld.length());
        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(1, reader.getStyles(WORKSPACE).size());
    }

    @Test
    public void testCssStylesInWorkspaceRaw() throws IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        final String WORKSPACE = "testWorkspace";
        final String STYLENAME = "restteststyle";
        File sldFile = new ClassPathResource("testdata/restteststyle.css").getFile();

        publisher.createWorkspace(WORKSPACE);

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(0, reader.getStyles(WORKSPACE).size());

        // insert style
        assertTrue(publisher.publishStyleInWorkspace(WORKSPACE, sldFile, STYLENAME, true));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));
        assertFalse(reader.existsStyle(STYLENAME));

        // insert style again
        assertFalse(publisher.publishStyleInWorkspace(WORKSPACE, sldFile, STYLENAME, true));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));
        assertFalse(reader.existsStyle(STYLENAME));

        String sld = reader.getSLD(WORKSPACE, STYLENAME);
        assertNotNull(sld);

        RESTStyle style = reader.getStyle(WORKSPACE, STYLENAME);
        assertEquals(STYLENAME, style.getName());
        assertEquals(WORKSPACE, style.getWorkspace());

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(1, reader.getStyles(WORKSPACE).size());
    }

    @Test
    public void testSldInZipStylesInWorkspaceRaw() throws IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        final String WORKSPACE = "testWorkspace";
        final String STYLENAME = "restteststyle";
        buildZipFile("target/restteststyle.zip", "target/test-classes/testdata", 
                "restteststyle.sld", 
                "style_image.png");
        
        File zipFile = new File("target/restteststyle.zip");

        publisher.createWorkspace(WORKSPACE);

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(0, reader.getStyles(WORKSPACE).size());

        // insert style
        assertTrue(publisher.publishStyleInWorkspace(WORKSPACE, zipFile, STYLENAME, true));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));
        assertFalse(reader.existsStyle(STYLENAME));

        // insert style again
        assertFalse(publisher.publishStyleInWorkspace(WORKSPACE, zipFile, STYLENAME, true));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));
        assertFalse(reader.existsStyle(STYLENAME));

        String sld = reader.getSLD(WORKSPACE, STYLENAME);
        assertNotNull(sld);

        RESTStyle style = reader.getStyle(WORKSPACE, STYLENAME);
        assertEquals(STYLENAME, style.getName());
        assertEquals(WORKSPACE, style.getWorkspace());

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(1, reader.getStyles(WORKSPACE).size());
    }
    
    /**
     * Unfortunately GeoServer allows ZIP upload only for SLD Styles.
     * @throws IOException 
     */
    @Test
    @Ignore
    public void testCssInZipStylesInWorkspaceRaw() throws IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        final String WORKSPACE = "testWorkspace";
        final String STYLENAME = "restteststyle";
        buildZipFile("target/restteststyle.zip", "target/test-classes/testdata", 
                "restteststyle.css", 
                "style_image.png");
        
        File zipFile = new File("target/restteststyle.zip");

        publisher.createWorkspace(WORKSPACE);

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(0, reader.getStyles(WORKSPACE).size());

        // insert style
        assertTrue(publisher.publishStyleInWorkspace(WORKSPACE, zipFile, STYLENAME, true));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));
        assertFalse(reader.existsStyle(STYLENAME));

        // insert style again
        assertFalse(publisher.publishStyleInWorkspace(WORKSPACE, zipFile, STYLENAME, true));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));
        assertFalse(reader.existsStyle(STYLENAME));

        String sld = reader.getSLD(WORKSPACE, STYLENAME);
        assertNotNull(sld);

        RESTStyle style = reader.getStyle(WORKSPACE, STYLENAME);
        assertEquals(STYLENAME, style.getName());
        assertEquals(WORKSPACE, style.getWorkspace());

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(1, reader.getStyles(WORKSPACE).size());
    }

    private static void buildZipFile(String zipFileName, String baseDir, String ... filesToAdd) {
        try (FileOutputStream fos = new FileOutputStream(zipFileName);
             ZipOutputStream zos = new ZipOutputStream(fos);) {			

            for (String file : filesToAdd) {
                addToZipFile(baseDir, file, zos);
            }
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private static void addToZipFile(String baseDir, String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {
        File file = new File(baseDir, fileName);
        try (FileInputStream fis = new FileInputStream(file);) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[4096];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }

            zos.closeEntry();
        }

    }
    
    @Test
    public void testStylesWithLegend() throws IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        final String WORKSPACE = "testWorkspace";
        final String STYLENAME = "restteststyle";
        File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();

        publisher.createWorkspace(WORKSPACE);

        assertTrue("Error inserting style", publisher.publishStyleInWorkspace(WORKSPACE, sldFile));
        assertTrue("Style does not exist in workspace", reader.existsStyle(WORKSPACE, STYLENAME));
        
        String sld = reader.getSLD(WORKSPACE, STYLENAME);
        assertNotNull(sld);

        RESTStyle style = reader.getStyle(WORKSPACE, STYLENAME);
        assertEquals(STYLENAME, style.getName());
        assertEquals(WORKSPACE, style.getWorkspace());
        assertFalse(style.hasLegend());
        
        style.removeFileName();

        style.addLegend(20, 20, "image/png", "http://legendserver.com/legend1");
        
        publisher.updateStyle(style, WORKSPACE, STYLENAME);
        
        RESTStyle result = reader.getStyle(WORKSPACE, STYLENAME);
        assertEquals("20", result.getLegendWidth());
        assertEquals("20", result.getLegendHeight());
        assertEquals("image/png", result.getLegendFormat());
        assertEquals("http://legendserver.com/legend1", result.getLegendOnlineResource());
        
        sld = reader.getSLD(WORKSPACE, STYLENAME);
        assertNotNull(sld);
    }
    
    @Test
    public void testStylesWithLegend2() throws IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        final String WORKSPACE = "testWorkspace";
        final String STYLENAME = "restteststyle";
        File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();

        publisher.createWorkspace(WORKSPACE);

        assertTrue("Error inserting style", publisher.publishStyleInWorkspace(WORKSPACE, sldFile));
        assertTrue("Style does not exist in workspace", reader.existsStyle(WORKSPACE, STYLENAME));

        RESTStyle style = reader.getStyle(WORKSPACE, STYLENAME);
        assertEquals(STYLENAME, style.getName());
        assertEquals(WORKSPACE, style.getWorkspace());
        assertFalse(style.hasLegend());

        style.addLegend("image/png", "http://legendserver.com/legend1");
        
        publisher.updateStyle(style, WORKSPACE, STYLENAME);
        
        RESTStyle result = reader.getStyle(WORKSPACE, STYLENAME);
        assertEquals("image/png", result.getLegendFormat());
        assertEquals("http://legendserver.com/legend1", result.getLegendOnlineResource());
        assertEquals("0", result.getLegendWidth());
        assertEquals("0", result.getLegendHeight());
    }
    
    @Test
    public void testYsldStylesInWorkspaceRaw() throws IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        final String WORKSPACE = "testWorkspace";
        final String STYLENAME = "restteststyle";
        File sldFile = new ClassPathResource("testdata/restteststyle.ysld").getFile();

        publisher.createWorkspace(WORKSPACE);

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(0, reader.getStyles(WORKSPACE).size());

        // insert style
        assertTrue(publisher.publishStyleInWorkspace(WORKSPACE, sldFile, STYLENAME, true));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));
        assertFalse(reader.existsStyle(STYLENAME));

        // insert style again
        assertFalse(publisher.publishStyleInWorkspace(WORKSPACE, sldFile, STYLENAME, true));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));
        assertFalse(reader.existsStyle(STYLENAME));

        String sld = reader.getSLD(WORKSPACE, STYLENAME);
        assertNotNull(sld);

        RESTStyle style = reader.getStyle(WORKSPACE, STYLENAME);
        assertEquals(STYLENAME, style.getName());
        assertEquals(WORKSPACE, style.getWorkspace());

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(1, reader.getStyles(WORKSPACE).size());
    }
    
    @Test
    public void testYsldStylesInWorkspaceRawWithYamlExtension() throws IOException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        final String WORKSPACE = "testWorkspace";
        final String STYLENAME = "restteststyle";
        File sldFile = new ClassPathResource("testdata/restteststyle.yaml").getFile();

        publisher.createWorkspace(WORKSPACE);

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(0, reader.getStyles(WORKSPACE).size());

        // insert style
        assertTrue(publisher.publishStyleInWorkspace(WORKSPACE, sldFile, STYLENAME, true));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));
        assertFalse(reader.existsStyle(STYLENAME));

        // insert style again
        assertFalse(publisher.publishStyleInWorkspace(WORKSPACE, sldFile, STYLENAME, true));
        assertTrue(reader.existsStyle(WORKSPACE, STYLENAME));
        assertFalse(reader.existsStyle(STYLENAME));

        String sld = reader.getSLD(WORKSPACE, STYLENAME);
        assertNotNull(sld);

        RESTStyle style = reader.getStyle(WORKSPACE, STYLENAME);
        assertEquals(STYLENAME, style.getName());
        assertEquals(WORKSPACE, style.getWorkspace());

        assertEquals(UNDELETABLE_STYLES.size(), reader.getStyles().size());
        assertEquals(1, reader.getStyles(WORKSPACE).size());
    }
}
