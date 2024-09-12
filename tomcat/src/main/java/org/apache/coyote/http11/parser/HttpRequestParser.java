package org.apache.coyote.http11.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.data.ContentType;
import org.apache.coyote.http11.data.HttpCookie;
import org.apache.coyote.http11.data.HttpMethod;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpRequestParameter;
import org.apache.coyote.http11.data.HttpVersion;
import org.apache.coyote.http11.data.MediaType;

public class HttpRequestParser {

    public static HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        String startLine = bufferedReader.readLine();
        if (startLine == null) {
            return null;
        }
        String[] requestParts = startLine.split(" ");
        String httpMethodName = requestParts[0].trim();
        HttpMethod httpMethod = HttpMethod.from(httpMethodName)
                .orElseThrow(() -> new IllegalArgumentException("이름이 " + httpMethodName + "인 HTTP 메소드가 없습니다."));
        String path = requestParts[1].trim();
        HttpVersion httpVersion = HttpVersion.from(requestParts[2].trim())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 HTTP 버전입니다."));

        Map<String, String> rawHttpRequestHeader = parseRawHttpRequestHeader(bufferedReader);
        ContentType contentType = null;
        Integer contentLength = null;
        String requestBody = null;
        HttpRequestParameter httpRequestParameter = null;
        if (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.PATCH) {
            String rawContentType = rawHttpRequestHeader.get("Content-Type");
            contentType = new ContentType(rawContentType);
            String rawContentLength = rawHttpRequestHeader.get("Content-Length");
            contentLength = Integer.valueOf(rawContentLength);
            requestBody = parseHttpRequestBody(contentLength, bufferedReader);
            if (contentType.getMediaType() == MediaType.URLENC) {
                httpRequestParameter = parseHttpRequestParameter(requestBody);
            }
        }
        List<HttpCookie> httpCookies = HttpCookieParser.parseCookiesFromRequest(rawHttpRequestHeader.get("Cookie"));
        return new HttpRequest(httpMethod, path, httpVersion, requestBody, contentType, contentLength,
                httpRequestParameter, httpCookies);
    }

    private static Map<String, String> parseRawHttpRequestHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> rawHttpRequestHeader = new HashMap<>();
        String requestLine = null;
        while ((requestLine = bufferedReader.readLine()) != null && !requestLine.isEmpty()) {
            String[] requestParts = requestLine.split(":");
            if (requestParts.length >= 2) {
                rawHttpRequestHeader.put(requestParts[0].trim(), requestParts[1].trim());
            }
        }
        return rawHttpRequestHeader;
    }

    private static String parseHttpRequestBody(int contentLength, BufferedReader bufferedReader)
            throws IOException {
        StringBuilder requestBodyBuilder = new StringBuilder();
        char[] bodyChars = new char[contentLength];
        bufferedReader.read(bodyChars, 0, contentLength);
        requestBodyBuilder.append(bodyChars);
        return requestBodyBuilder.toString();
    }

    private static HttpRequestParameter parseHttpRequestParameter(String rawRequestBody) {
        Map<String, String> paramMap = Arrays.stream(rawRequestBody.split("&"))
                .map(param -> param.split("=", 2))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]
                ));
        return new HttpRequestParameter(paramMap);
    }
}
