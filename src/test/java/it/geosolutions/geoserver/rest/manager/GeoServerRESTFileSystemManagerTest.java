package it.geosolutions.geoserver.rest.manager;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Self contained test for working with Structured readers
 *
 *
 * @author Simone Giannecchini, simone.giannecchini@geo-solutions.it
 * @author Daniele Romagnoli, GeoSolutions SAS
 *
 */
public class GeoServerRESTFileSystemManagerTest extends GeoserverRESTTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoServerRESTFileSystemManagerTest.class);

    @Test
    public void testXmlConfigExists() {
        assertTrue(manager.getFileSystemResourceManager().exists("global.xml"));
        assertFalse(manager.getFileSystemResourceManager().exists("global2.xml"));
    }

    @Test
    public void testXmlDownload() {
        assertTrue(manager.getFileSystemResourceManager().download("global.xml").contains("claudius.ptolomaeus@gmail.com"));
        assertNull(manager.getFileSystemResourceManager().download("global2.xml"));
    }

    @Test
    public void testXmlDownload2() {
        assertNotNull(manager.getFileSystemResourceManager().download("data/nyc/poi.shx"));
    }

    @Test
    public void testUploadAndDeleteFile() {
        manager.getFileSystemResourceManager().delete("testfile.txt");
        
        assertFalse(manager.getFileSystemResourceManager().exists("testfile.txt"));
        assertNotNull(manager.getFileSystemResourceManager().upload("testfile.txt", "abc"));
        
        assertTrue(manager.getFileSystemResourceManager().exists("testfile.txt"));
        assertEquals("abc", manager.getFileSystemResourceManager().download("testfile.txt"));
        
        assertTrue(manager.getFileSystemResourceManager().delete("testfile.txt"));
    }
}
