package com.betaTest.utils;


import cn.hutool.core.lang.Pair;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * api网关请求工具
 */
public class ApiSixHttpUtils {
    private static final Logger log = LoggerFactory.getLogger(ApiSixHttpUtils.class);

    private static final String HTTP_JSON = "application/json; charset=utf-8";
    private static final String HTTP_FORM = "application/x-www-form-urlencoded; charset=utf-8";

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();


    /**
     * get请求
     * 对于小文档，响应体上的string()方法非常方便和高效。
     * 但是，如果响应主体很大(大于1 MB)，则应避免string()，
     * 因为它会将整个文档加载到内存中。在这种情况下，将主体处理为流。
     *
     * @param url
     * @return
     */
    public static String httpGet(String url) {
        if (url == null || "".equals(url)) {
            log.error("url为null!");
            return "";
        }

        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                String responseStr = response.body().string();
                log.debug("http GET 请求成功; [url={}, res={}]", url, responseStr);
                return responseStr;
            } else {
                log.warn("Http GET 请求失败; [errorCode = {} , url={}]", response.code(), url);
            }
        } catch (IOException e) {
            throw new RuntimeException("同步http GET 请求失败,url:" + url, e);
        }
        return null;
    }

    /**
     * get请求
     *
     * @param url
     * @param headers
     * @return
     */
    public static String httpGet(String url, Map<String, String> headers) {
        if (headers == null || headers.size() == 0) {
            return httpGet(url);
        }

        Request.Builder builder = new Request.Builder();
        headers.forEach((String key, String value) -> builder.header(key, value));
        Request request = builder.get().url(url).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                String responseStr = response.body().string();
                log.debug("http GET 请求成功; [url={}, res={}]", url, responseStr);
                return responseStr;
            } else {
                log.warn("Http GET 请求失败; [errorCode = {} , url={}]", response.code(), url);
            }
        } catch (IOException e) {
            throw new RuntimeException("同步http GET 请求失败,url:" + url, e);
        }
        return null;
    }

    /**
     * 同步 POST调用 无Header
     *
     * @param url
     * @param json
     * @return
     */
    public String httpPostJson(String url, String json) {
        if (url == null || "".equals(url)) {
            log.error("url为null!");
            return "";
        }

        MediaType JSON = MediaType.parse(HTTP_JSON);
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder requestBuilder = new Request.Builder().url(url);
        Request request = requestBuilder.post(body).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                log.debug("http Post 请求成功; [url={}, requestContent={}]", url, json);
                return response.body().string();
            } else {
                log.warn("Http POST 请求失败; [ errorCode = {}, url={}, param={}]", response.code(), url, json);
            }
        } catch (IOException e) {
            throw new RuntimeException("同步http请求失败,url:" + url, e);
        }
        return null;
    }

    /**
     * 同步 POST调用 有apisix验签Header
     *
     * @param url
     * @param json
     * @param active
     * @return
     * @throws Exception
     */
    public String httpPostJson(String url, String json, String active) throws Exception {
        return this.httpPostJson(url, null, json, active);
    }


    public static String calculateSignature(byte[] paramArrayOfByte, String keys) throws Exception {
        String Algor = "HmacSHA256";
        Mac localMac = Mac.getInstance(Algor);
        byte[] key = keys.getBytes();
        localMac.init(new SecretKeySpec(key, Algor));
        paramArrayOfByte = localMac.doFinal(paramArrayOfByte);
        return DatatypeConverter.printBase64Binary(paramArrayOfByte);
    }

    /**
     * 构造apisix网关请求 header
     *
     * @param urlStr        请求url，全路径格式，比如：https://xx.xx.xx.cn/api/p/v1/user.get
     * @param requestMethod 请求方法,大写格式，如：GET, POST
     * @param appKey        企业分配的appKey
     * @param secret        企业分配的secret
     * @param bodyJson      请求数据
     * @return
     */
    public static Map<String, String> generateHeader(String urlStr, String requestMethod, String appKey, String secret, String bodyJson) {
        log.info("params,urlStr={},requestMethod={},appKey={},secret={}", urlStr, requestMethod, appKey, secret);
        Map<String, String> header = new HashMap<>();
        try {
            DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = dateFormat.format(new Date());
            URL url = new URL(urlStr);
            URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
            String canonicalQueryString = getCanonicalQueryString(uri.getQuery());
            String message = requestMethod.toUpperCase() + "\n" + uri.getPath() + "\n" + canonicalQueryString + "\n" + appKey + "\n" + date + "\n";
            Mac hasher = Mac.getInstance("HmacSHA256");
            hasher.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            byte[] hash = hasher.doFinal(message.getBytes());
            // to lowercase hexits
            DatatypeConverter.printHexBinary(hash);
            // to base64
            String sign = DatatypeConverter.printBase64Binary(hash);
            header.put("X-HMAC-SIGNATURE", sign);
            header.put("X-HMAC-ALGORITHM", "hmac-sha256");
            header.put("X-HMAC-ACCESS-KEY", appKey);
            header.put("Date", date);
            if (StringUtils.isNotEmpty(bodyJson)) {
                String data = calculateSignature(bodyJson.getBytes("UTF-8"), secret);
                header.put("X-HMAC-DIGEST", data);
            }
        } catch (Exception e) {
            log.error("generate error", e);
            throw new RuntimeException(e);
        }
        return header;
    }

    private static String getCanonicalQueryString(String query) {
        if (query == null || query.trim().length() == 0) {
            return "";
        }
        List<Pair<String, String>> queryParamList = new ArrayList<>();
        String[] params = query.split("&");
        for (String param : params) {
            int eqIndex = param.indexOf("=");
            String key = param.substring(0, eqIndex);
            String value = param.substring(eqIndex + 1);
            Pair<String, String> pair = new Pair<String, String>(key, value);
            queryParamList.add(pair);
        }

        List<Pair<String, String>> sortedParamList = queryParamList.stream().sorted(Comparator.comparing(param -> param.getKey() + "=" + Optional.ofNullable(param.getValue()).orElse(""))).collect(Collectors.toList());
        List<Pair<String, String>> encodeParamList = new ArrayList<>();
        sortedParamList.stream().forEach(param -> {
            try {
                String key = URLEncoder.encode(param.getKey(), "utf-8");
                String value = URLEncoder.encode(Optional.ofNullable(param.getValue()).orElse(""), "utf-8")
                        .replaceAll("\\%2B", "%20")
                        .replaceAll("\\+", "%20")
                        .replaceAll("\\%21", "!")
                        .replaceAll("\\%27", "'")
                        .replaceAll("\\%28", "(")
                        .replaceAll("\\%29", ")")
                        .replaceAll("\\%7E", "~")
                        .replaceAll("\\%25", "%");
                encodeParamList.add(new Pair<>(key, value));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("encoding error");
            }
        });
        StringBuilder queryParamString = new StringBuilder(64);
        for (Pair<String, String> encodeParam : encodeParamList) {
            queryParamString.append(encodeParam.getKey()).append("=").append(Optional.ofNullable(encodeParam.getValue()).orElse(""));
            queryParamString.append("&");
        }

        return queryParamString.substring(0, queryParamString.length() - 1);
    }


    /**
     * 同步 POST调用 有apisix验签Header
     *
     * @param url
     * @param json
     * @return
     */
    public static String httpPostJson(String url, String appKey, String secret, String json) throws Exception {
        Map<String, String> headers = generateHeader(url, "POST", appKey, secret, json);
        return httpPostJson(url, appKey, secret, json, headers);
    }

    /**
     * 同步 POST调用 有apisix验签Header
     *
     * @param url
     * @param headers
     * @param json
     * @return
     */
    public static String httpPostJson(String url, String appKey, String secret, String json, Map<String, String> headers) throws Exception {
        log.debug("============================");
        log.debug("请求json:{}", json);
        log.debug("路径url:{}", url);

        MediaType JSON = MediaType.parse(HTTP_JSON);
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder requestBuilder = new Request.Builder().url(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            log.debug("header::{}", entry);
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = requestBuilder.post(body).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println(response);
            if (response.code() == 200) {
                String res = response.body().string();
                log.debug("http Post 请求成功; [url={}, request={}, response={}]", url, json, res);
                return res;
            } else {
                log.warn("Http POST 请求失败; [ errorCode = {}, url={}, param={}]", response.code(), url, json);
            }
        } catch (IOException e) {
            throw new RuntimeException("同步http请求失败,url:" + url, e);
        }
        return null;
    }

    public static String httpSend(String url, String appKey, String secret, String json, String method) throws Exception {
        log.debug("============================");
        log.debug("请求json:{}", json);
        log.debug("请求方法:{}", method);
        log.debug("路径url:{}", url);

        Map<String, String> headers = generateHeader(url, method, appKey, secret, json);

        MediaType JSON = MediaType.parse(HTTP_JSON);
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder requestBuilder = new Request.Builder().url(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            log.debug("header::{}", entry);
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = requestBuilder.method(method, body).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                String res = response.body().string();
                log.debug("HTTP请求成功; [req={}, resp={}]", json, res);
                return res;
            } else {
                log.warn("HTTP请求失败; [ errorCode = {}, param={}]", response.code(), json);
            }
        } catch (IOException e) {
            throw new RuntimeException("同步http请求失败,url:" + url, e);
        }
        return null;
    }
}