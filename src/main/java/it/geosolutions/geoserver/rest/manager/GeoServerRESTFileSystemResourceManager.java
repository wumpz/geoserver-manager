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
package it.geosolutions.geoserver.rest.manager;

import it.geosolutions.geoserver.rest.HTTPUtils;
import java.io.File;
import java.net.URL;

/**
 *
 * @author tw
 */
public class GeoServerRESTFileSystemResourceManager extends GeoServerRESTAbstractManager {

    public GeoServerRESTFileSystemResourceManager(URL restURL, String username, String password)
            throws IllegalArgumentException {
        super(restURL, username, password);
    }

    public boolean exists(String path) {
        return HTTPUtils.exists(buildUrl(path), gsuser, gspass);
    }

    protected String buildUrl(final String path) {
        return gsBaseUrl.toString() + "/rest/resource/" + path;
    }

    public String download(String path) {
        return HTTPUtils.get(buildUrl(path), gsuser, gspass);
    }

    public String upload(String path, String content, String mimeType) {
        return HTTPUtils.put(buildUrl(path), content, mimeType, gsuser, gspass);
    }
    
    public String upload(String path, String content) {
        return upload(path, content, "text/plain");
    }
    
    public String upload(String path, File content, String mimeType) {
        return HTTPUtils.put(buildUrl(path), content, mimeType, gsuser, gspass);
    }

    public boolean delete(String path) {
        return HTTPUtils.delete(buildUrl(path), gsuser, gspass);
    }
}
