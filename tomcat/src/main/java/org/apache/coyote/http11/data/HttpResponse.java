package org.apache.coyote.http11.data;

import java.util.ArrayList;
import java.util.List;

public class HttpResponse {
    private HttpVersion httpVersion;
    private HttpStatusCode httpStatusCode;
    private ContentType contentType;
    private int contentLength;
    private List<HttpCookie> httpCookies;
    private String responseBody;
    private String redirectUrl;

    public HttpResponse(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        this.httpCookies = new ArrayList<>();
    }

    public HttpResponse addHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
        return this;
    }

    public HttpResponse addContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpResponse addContentLength(int contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public HttpResponse addCookie(HttpCookie httpCookie) {
        this.httpCookies.add(httpCookie);
        return this;
    }

    public HttpResponse addResponseBody(String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public HttpResponse addRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public List<HttpCookie> getHttpCookies() {
        return new ArrayList<>(httpCookies);
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}

