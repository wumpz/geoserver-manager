/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2011 GeoSolutions S.A.S.
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

import static org.junit.Assert.*;

import java.util.List;

import it.geosolutions.geoserver.rest.decoder.RESTLayerGroup;
import java.util.logging.Logger;

import org.junit.Test;

/**
 * Simple class for testing that the DeleteAllLayerGroups() method behaves correctly.
 *
 * @author Nicola Lagomarsini
 */
public class GeoServerRESTClassTest extends GeoserverRESTTest {

  private static final Logger LOG = Logger.getLogger(GeoServerRESTClassTest.class.getName());

  @Test
  public void testGetLayerGroups() {
    if (!enabled()) {
      return;
    }
    List<String> groups = reader.getLayerGroups().getNames();
    LOG.info("Found " + groups.size() + " layerGroups");
    for (String groupName : groups) {
      RESTLayerGroup group = reader.getLayerGroup(groupName);
      if (groups != null) {
        assertNotNull(group.getPublishedList());
      }
    }
  }
}
