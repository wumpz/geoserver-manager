package it.geosolutions.geoserver.rest.manager;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Self contained test for working with Structured readers
 *
 *
 * @author Simone Giannecchini, simone.giannecchini@geo-solutions.it
 * @author Daniele Romagnoli, GeoSolutions SAS
 *
 */
public class GeoServerRESTFileSystemManagerTest extends GeoserverRESTTest {

  @Test
  public void testXmlConfigExists() {
    if (!enabled()) {
      return;
    }

    assertTrue(manager.getFileSystemResourceManager().exists("global.xml"));
    assertFalse(manager.getFileSystemResourceManager().exists("global2.xml"));
  }

  @Test
  public void testXmlDownload() {
    if (!enabled()) {
      return;
    }

    assertTrue(manager.getFileSystemResourceManager().download("global.xml").contains("claudius.ptolomaeus@mercury.olympus.gov"));
    assertNull(manager.getFileSystemResourceManager().download("global2.xml"));
  }

  @Test
  public void testXmlDownload2() {
    if (!enabled()) {
      return;
    }

    assertNotNull(manager.getFileSystemResourceManager().download("data/nyc/poi.shx"));
  }

  @Test
  public void testUploadAndDeleteFile() {
    if (!enabled()) {
      return;
    }

    manager.getFileSystemResourceManager().delete("testfile.txt");

    assertFalse(manager.getFileSystemResourceManager().exists("testfile.txt"));
    assertNotNull(manager.getFileSystemResourceManager().upload("testfile.txt", "abc"));

    assertTrue(manager.getFileSystemResourceManager().exists("testfile.txt"));
    assertEquals("abc", manager.getFileSystemResourceManager().download("testfile.txt"));

    assertTrue(manager.getFileSystemResourceManager().delete("testfile.txt"));
  }
}
