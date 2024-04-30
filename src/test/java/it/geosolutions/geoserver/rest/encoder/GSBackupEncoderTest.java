/*
 *  Copyright (C) 2007 - 2011 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.geoserver.rest.encoder;

import java.util.logging.Logger;
import junit.framework.TestCase;

import org.junit.Test;

/**
 *
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GSBackupEncoderTest extends TestCase {

  private static final Logger LOG = Logger.getLogger(GSBackupEncoderTest.class.getName());

  @Test
  public void testAll() {
    final GSBackupEncoder bkpenc = new GSBackupEncoder("BK1");
    LOG.info(bkpenc.toString());

    bkpenc.setPath("test_path");
    LOG.info(bkpenc.toString());
    bkpenc.setPath("new_path");
    LOG.info(bkpenc.toString());

    bkpenc.setIncludeData(true);
    LOG.info(bkpenc.toString());

    bkpenc.setIncludeGwc(false);
    LOG.info(bkpenc.toString());

    bkpenc.setIncludeLog(false);
    LOG.info(bkpenc.toString());
  }
}
