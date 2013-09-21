package com.imageservice.app.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.http.HTTPException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class HttpUtil {
    private static HttpUtil httpUtil;
    private HttpClient httpClient;

    private HttpUtil(){
        this.httpClient = new HttpClient();
        this.httpClient.getParams().setConnectionManagerTimeout(5000);
    }

    public static HttpUtil getInstance(){
        if(httpUtil==null){
            httpUtil = new HttpUtil();
        }
        return httpUtil;
    }

    public HttpResponse makePostRequest(String url, Map<String, String> paramMap) throws IOException, HTTPException {
        PostMethod post = new PostMethod(url);
        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, String> pm : paramMap.entrySet()){
            params.add(new NameValuePair(pm.getKey(), pm.getValue()));
        }
        post.setRequestBody(params.toArray(new NameValuePair[params.size()]));
        try{
            int status = this.httpClient.executeMethod(post);
            return new HttpResponse(status, post.getStatusText(), post.getResponseBodyAsString());
        }finally {
            post.releaseConnection();
        }
    }

    public HttpResponse makeGetRequest(String url, Map<String, String> paramMap) throws IOException, HTTPException {
        GetMethod get = new GetMethod(url);
        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, String> pm : paramMap.entrySet()){
            params.add(new NameValuePair(pm.getKey(), pm.getValue()));
        }
        get.setQueryString(params.toArray(new NameValuePair[params.size()]));
        try {
            int statusCode = this.httpClient.executeMethod(get);
            return new HttpResponse(statusCode, get.getStatusText(), get.getResponseBodyAsString());
        }finally {
            get.releaseConnection();
        }
    }

    public class HttpResponse {
        Integer statusCode;
        String status;
        String responseBody;

        private HttpResponse(Integer statusCode, String status, String responseBody) {
            this.statusCode = statusCode;
            this.status = status;
            this.responseBody = responseBody;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getResponseBody() {
            return responseBody;
        }

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }
    }
}
