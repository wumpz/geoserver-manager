package it.geosolutions.geoserver.rest.encoder.feature;

import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.Presentation;
import it.geosolutions.geoserver.rest.encoder.metadata.GSFeatureDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;

import java.math.BigDecimal;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.filter.AbstractFilter;
import org.junit.Assert;
import org.junit.Test;

public class GSFeatureDimensionInfoEncoderTest {

  @Test
  public void dimensionTest() {
    final GSFeatureDimensionInfoEncoder elevationDimension = new GSFeatureDimensionInfoEncoder(
            "elevation_field");

    // if (LOG.isInfoEnabled())
    // LOG.info(encoder.toString());
    elevationDimension.setPresentation(
            Presentation.DISCRETE_INTERVAL, BigDecimal.valueOf(10));

    elevationDimension.setPresentation(
            Presentation.DISCRETE_INTERVAL, BigDecimal.valueOf(12));

    List<Element> elList = ElementUtils.search(
            elevationDimension.getRoot(), new AbstractFilter() {
      public Object filter(Object obj) {
        if (obj instanceof Element element) {
          final Element el = element;
          if (el.getName().equals(
                  GSDimensionInfoEncoder.DIMENSIONINFO)) {
            return el;
          }
        }
        return null;
      }
    });
    // using set we get only one element called
    // Presentation.DISCRETE_INTERVAL
    Assert.assertEquals(elList.size(), 1);

    elevationDimension.setPresentation(Presentation.LIST);

    // this kind of presentation do not support a resolution parameter
    elList = ElementUtils.search(
            elevationDimension.getRoot(), new AbstractFilter() {
      public Object filter(Object obj) {
        if (obj instanceof Element element) {
          final Element el = element;
          if (el.getName().equals(
                  GSDimensionInfoEncoder.RESOLUTION)) {
            return el;
          }
        }
        return null;
      }
    });

    Assert.assertEquals(elList.size(), 0);

  }

}
