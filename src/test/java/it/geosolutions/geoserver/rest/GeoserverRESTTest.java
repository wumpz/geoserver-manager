/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *
 *  Copyright (C) 2007 - 2016 GeoSolutions S.A.S.
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
package it.geosolutions.geoserver.rest;

import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStore;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.decoder.RESTFeatureType;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroup;
import it.geosolutions.geoserver.rest.decoder.about.GSVersionDecoder;
import it.geosolutions.geoserver.rest.decoder.about.GSVersionDecoder.VERSION;
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * Initializes REST params.
 * <P>
 * <B>These tests are destructive, so you have to explicitly enable them</B> by setting the env var
 * <TT>resttest</TT> to <TT>true</TT>.
 * <P>
 * The target geoserver instance can be customized by defining the following env vars:
 * <ul>
 * <LI><TT>resturl</TT> (default <TT>http://localhost:8080/geoserver</TT>)</LI>
 * <LI><TT>restuser</TT> (default: <TT>admin</TT>)</LI>
 * <LI><TT>restpw</TT> (default: <TT>geoserver</TT>)</LI>
 * </ul>
 *
 * @author etj
 * @author carlo cancellieri - GeoSolutions
 */
public abstract class GeoserverRESTTest {

  private static final Logger LOG = Logger.getLogger(GeoserverRESTTest.class.getName());

  @Rule
  public TestName _testName = new TestName();

  public static final String DEFAULT_WS = "geosolutions";

  public static final String RESTURL;

  public static final String RESTUSER;

  public static final String RESTPW;

  // geoserver target version
  public static final String GS_VERSION;

  public static URL URL;

  public static GeoServerRESTManager manager;

  public static GeoServerRESTReader reader;

  public static GeoServerRESTPublisher publisher;

  private static boolean enabled = false;

  private static Boolean existgs = null;

  static {
    RESTURL = getenv("gsmgr_resturl", "http://localhost:8080/geoserver");
    RESTUSER = getenv("gsmgr_restuser", "admin");
    RESTPW = getenv("gsmgr_restpw", "geoserver");
    GS_VERSION = getenv("gsmgr_version", "2.8");

    // These tests will destroy data, so let's make sure we do want to run them
    enabled = getenv("gsmgr_resttest", "false").equalsIgnoreCase("true");
    if (!enabled) {
      LOG.warning("Tests are disabled. Please read the documentation to enable them.");
    }

    try {
      URL = new URL(RESTURL);
      manager = new GeoServerRESTManager(URL, RESTUSER, RESTPW);
      reader = manager.getReader();
      publisher = manager.getPublisher();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  private static String getenv(String envName, String envDefault) {
    String env = System.getenv(envName);
    String prop = System.getProperty(envName, env);
    LOG.fine("varname " + envName + " --> env:" + env + " prop:" + prop);
    return prop != null ? prop : envDefault;
  }

  @BeforeClass
  public static void setUp() throws Exception {
    if (enabled) {
      if (existgs == null) {
        existgs = reader.existGeoserver();
        if (!existgs) {
          LOG.severe("TESTS WILL FAIL BECAUSE NO GEOSERVER WAS FOUND AT " + RESTURL
                  + " (" + RESTUSER + ":" + RESTPW + ")");
        } else {
          LOG.info("Using geoserver instance " + RESTUSER + ":" + RESTPW + " @ "
                  + RESTURL);
        }
      } else if (existgs == false) {
        LOG.fine("Failing tests  : geoserver not found");
        fail("GeoServer not found");
      }

      GSVersionDecoder v = reader.getGeoserverVersion();
      if (v.compareTo(VERSION.getVersion(GS_VERSION)) <= 0) {
        LOG.fine("Failing tests  : geoserver version does not match.\nAccepted versions: " + VERSION.print());
        fail("GeoServer version (" + v.getVersion().name() + ") does not match the desired one (" + GS_VERSION + ")");
      }
    } else {
      LOG.fine("Skipping tests ");
      LOG.warning("Tests are disabled. Please read the documentation to enable them.");
    }
  }

  @Before
  public void before() {
    String testName = _testName.getMethodName();
    LOG.warning("");
    LOG.warning("============================================================");
    LOG.warning("=== RUNNING TEST " + testName);
    LOG.warning("");
  }

  protected boolean enabled() {
    return enabled;
  }

  protected void deleteAll() {
    LOG.info("Starting DELETEALL procedure");
    deleteAllLayerGroups();
    assertTrue("Some layergroups were not removed", reader.getLayerGroups().isEmpty());

    deleteAllLayers();
    assertTrue("Some layers were not removed", reader.getLayers().isEmpty());

    deleteAllCoverageStores();
    deleteAllDataStores();

    deleteAllWorkspaces();
    // assertTrue("Some workspaces were not removed", reader.getWorkspaces().isEmpty());

    deleteAllStyles();
    //Standard styles are since geoserver 2.12 not deletable anymore. (point, line, polygon, generic, raster)
    assertTrue("Some styles were not removed", reader.getStyles().size() <= 5);

    LOG.info("ENDING DELETEALL procedure");
  }

  private void deleteAllLayerGroups() {
    List<String> groups = reader.getLayerGroups().getNames();
    LOG.info("Found " + groups.size() + " layerGroups");
    for (String groupName : groups) {
      RESTLayerGroup group = reader.getLayerGroup(groupName);
      if (groups != null) {
        StringBuilder sb = new StringBuilder("Group: ").append(groupName).append(":");
        for (NameLinkElem layer : group.getPublishedList()) {
          sb.append(" ").append(layer);
        }

        boolean removed = publisher.removeLayerGroup(groupName);
        LOG.info(sb.toString() + ": removed: " + removed);
        assertTrue("LayerGroup not removed: " + groupName, removed);
      }
    }
  }

  private void deleteAllLayers() {
    List<String> layers = reader.getLayers().getNames();
    if (layers != null) {
      for (String layerName : layers) {
        RESTLayer layer = reader.getLayer(layerName);
        if (layer != null) {
          if (layer.getType() == RESTLayer.Type.VECTOR) {
            deleteFeatureType(layer);
          } else if (layer.getType() == RESTLayer.Type.RASTER) {
            deleteCoverage(layer);
          } else {
            LOG.severe("Unknown layer type " + layer.getType());
          }
        } else {
          LOG.warning("layer not found");
        }
      }
    }
  }

  private void deleteAllCoverageStores() {
    List<String> workspaces = reader.getWorkspaceNames();
    for (String workspace : workspaces) {
      List<String> stores = reader.getCoverageStores(workspace).getNames();

      for (String storename : stores) {
        // RESTCoverageStore store = reader.getCoverageStore(workspace, storename);

        LOG.warning("Deleting CoverageStore " + workspace + " : " + storename);
        boolean removed = publisher.removeCoverageStore(workspace, storename, false, GeoServerRESTPublisher.Purge.METADATA);
        assertTrue("CoverageStore not removed " + workspace + " : " + storename, removed);
      }
    }
  }

  private void deleteAllDataStores() {
    List<String> workspaces = reader.getWorkspaceNames();
    for (String workspace : workspaces) {
      List<String> stores = reader.getDatastores(workspace).getNames();

      for (String storename : stores) {
        // RESTDataStore store = reader.getDatastore(workspace, storename);

        // if(store.getType() == RESTDataStore.DBType.POSTGIS) {
        // LOG.info("Skipping PG datastore " + store.getWorkspaceName()+":"+store.getName());
        // continue;
        // }
        LOG.warning("Deleting DataStore " + workspace + " : " + storename);
        boolean removed = publisher.removeDatastore(workspace, storename, false, GeoServerRESTPublisher.Purge.METADATA);
        assertTrue("DataStore not removed " + workspace + " : " + storename, removed);
      }
    }
  }

  protected void deleteAllWorkspacesRecursively() {
    List<String> workspaces = reader.getWorkspaceNames();
    for (String workspace : workspaces) {
      LOG.warning("Deleting Workspace " + workspace);
      boolean removed = publisher.removeWorkspace(workspace, true);
      assertTrue("Workspace not removed " + workspace, removed);

    }
  }

  protected void deleteAllWorkspaces() {
    List<String> workspaces = reader.getWorkspaceNames();
    for (String workspace : workspaces) {
      LOG.warning("Deleting Workspace " + workspace);
      boolean removed = publisher.removeWorkspace(workspace, true);
      assertTrue("Workspace not removed " + workspace, removed);

    }
  }

  protected static final List<String> UNDELETABLE_STYLES = Arrays.asList("generic", "line", "point", "polygon", "raster");

  protected void deleteAllStyles() {
    List<String> styles = reader.getStyles().getNames();
    if (styles != null) {
      for (String style : styles) {
        LOG.warning("Deleting Style " + style);
        boolean removed = publisher.removeStyle(style, true);
        if (!UNDELETABLE_STYLES.contains(style)) {
          assertTrue("Style not removed " + style, removed);
        }
      }
    }
  }

  private void deleteFeatureType(RESTLayer layer) {
    RESTFeatureType featureType = reader.getFeatureType(layer);
    RESTDataStore datastore = reader.getDatastore(featureType);

    LOG.warning("Deleting FeatureType " + datastore.getWorkspaceName() + " : "
            + datastore.getName() + " / " + featureType.getName());

    boolean removed = publisher.unpublishFeatureType(datastore.getWorkspaceName(),
            datastore.getName(), layer.getName());
    assertTrue(
            "FeatureType not removed:" + datastore.getWorkspaceName() + " : "
            + datastore.getName() + " / " + featureType.getName(), removed);

  }

  private void deleteCoverage(RESTLayer layer) {
    RESTCoverage coverage = reader.getCoverage(layer);
    RESTCoverageStore coverageStore = reader.getCoverageStore(coverage);

    LOG.warning("Deleting Coverage " + coverageStore.getWorkspaceName() + " : "
            + coverageStore.getName() + " / " + coverage.getName());

    boolean removed = publisher.unpublishCoverage(coverageStore.getWorkspaceName(),
            coverageStore.getName(), coverage.getName());
    assertTrue("Coverage not deleted " + coverageStore.getWorkspaceName() + " : "
            + coverageStore.getName() + " / " + coverage.getName(), removed);
  }

  protected boolean existsLayer(String layername) {
    return reader.getLayer(layername) != null;
  }

}
