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
package it.geosolutions.geoserver.rest.decoder;

import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;

import org.jdom.Element;

/**
 * Parses list of summary data about Coverages.
 *
 * <P>
 * This is the XML REST representation:
 * <PRE>{@code <coverages>
 * <coverage>
 * <name>5-25-1-120-11-DOF</name>
 * <atom:link
 * xmlns:atom="http://www.w3.org/2005/Atom"
 * rel="alternate"
 * href="http://localhost:8080/geoserver/rest/workspaces/pippo/coveragestores/pippoLayer/coverages/5-25-1-120-11-DOF.xml"
 * type="application/xml"/>
 * </coverage>
 * </coverages>
 *
 * }</PRE>
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RESTCoverageList extends RESTAbstractList<NameLinkElem> {

  public static RESTCoverageList build(String response) {
    Element elem = JDOMBuilder.buildElement(response);
    return elem == null ? null : new RESTCoverageList(elem);
  }

  protected RESTCoverageList(Element list) {
    super(list);
  }

}
