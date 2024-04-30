/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *
 *  Copyright (C) 2007,2013 GeoSolutions S.A.S.
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

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RESTStyle {

  private final Element elem;

  public static RESTStyle build(String xml) {
    if (xml == null) {
      return null;
    }

    Element e = JDOMBuilder.buildElement(xml);
    if (e != null) {
      return new RESTStyle(e);
    } else {
      return null;
    }
  }

  protected RESTStyle(Element elem) {
    this.elem = elem;
  }

  public String getName() {
    return elem.getChildText("name");
  }

  public String getFileName() {
    return elem.getChildText("filename");
  }

  public String getWorkspace() {
    if (elem.getChild("workspace") != null) {
      return elem.getChild("workspace").getChildText("name");
    } else {
      return null;
    }
  }

  public GeoServerRESTPublisher.Format getFormat() {
    if (elem.getChild("format") != null) {
      return GeoServerRESTPublisher.Format.valueOf(elem.getChildText("format").toUpperCase());
    } else {
      return null;
    }
  }

  public boolean hasLegend() {
    return elem.getChild("legend") != null;
  }

  public String getLegendWidth() {
    if (hasLegend()) {
      return elem.getChild("legend").getChildText("width");
    }
    return null;
  }

  public String getLegendHeight() {
    if (hasLegend()) {
      return elem.getChild("legend").getChildText("height");
    }
    return null;
  }

  public String getLegendOnlineResource() {
    if (hasLegend()) {
      return elem.getChild("legend").getChildText("onlineResource");
    }
    return null;
  }

  public String getLegendFormat() {
    if (hasLegend()) {
      return elem.getChild("legend").getChildText("format");
    }
    return null;
  }

  public void removeLegend() {
    elem.removeChild("legend");
  }

  public void removeFileName() {
    elem.removeChild("filename");
  }

  public void addLegend(int width, int height, String format, String onlineResource) {
    removeLegend();

    Element legend = new Element("legend");
    elem.addContent(legend);
    legend.addContent(new Element("width").setText(String.valueOf(width)));
    legend.addContent(new Element("height").setText(String.valueOf(height)));
    legend.addContent(new Element("format").setText(format));
    legend.addContent(new Element("onlineResource").setText(onlineResource));
  }

  public void addLegend(String format, String onlineResource) {
    removeLegend();

    Element legend = new Element("legend");
    elem.addContent(legend);
    legend.addContent(new Element("format").setText(format));
    legend.addContent(new Element("onlineResource").setText(onlineResource));
  }

  private final static XMLOutputter OUTPUTTER = new XMLOutputter(Format.getCompactFormat());

  @Override
  public String toString() {
    return OUTPUTTER.outputString(elem);
  }
}
