/*
 * The MIT License
 *
 * Copyright 2018 GeoSolutions.
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
package it.geosolutions.geoserver.rest.decoder;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tw
 */
public class RESTStyleTest {

    public RESTStyleTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private String TEST_XML_WITH_LEGEND = "<style>"
            + "<name>test_sld_style</name>"
            + "<format>sld</format>"
            + "<languageVersion>"
            + "<version>1.0.0</version>"
            + "</languageVersion>"
            + "<filename>test_sld_style.sld</filename>"
            + "<legend>"
            + "<width>500</width>"
            + "<height>600</height>"
            + "<format>image/jpeg;charset=UTF-8</format>"
            + "<onlineResource>legende_test_sld_style.PNG</onlineResource>"
            + "</legend>"
            + "</style>";

    
    @Test
    public void testReadLegendData() {
        RESTStyle style = RESTStyle.build(TEST_XML_WITH_LEGEND);
        
        assertEquals("test_sld_style", style.getName());
        
        assertEquals("500", style.getLegendWidth());
        assertEquals("600", style.getLegendHeight());
        assertEquals("image/jpeg;charset=UTF-8", style.getLegendFormat());
        assertEquals("legende_test_sld_style.PNG", style.getLegendOnlineResource());
    }
    
    @Test
    public void testReadLegendData2() {
        RESTStyle style = RESTStyle.build(TEST_XML_WITH_LEGEND);
        
        assertTrue(style.hasLegend());
        
        style.removeLegend();
        
        System.out.println(style);
        
        assertFalse(style.hasLegend());
        
    }
    
    @Test
    public void testBuildLegendData() {
        RESTStyle style = RESTStyle.build(TEST_XML_WITH_LEGEND);
        style.removeLegend();
        
        style.addLegend(200, 300, "image/png;charset=UTF-8", "test.png");
        
        assertEquals("200", style.getLegendWidth());
        assertEquals("300", style.getLegendHeight());
        assertEquals("image/png;charset=UTF-8", style.getLegendFormat());
        assertEquals("test.png", style.getLegendOnlineResource());
    }
}
