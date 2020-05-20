package com.maj.mall.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

public class HttpclientTest2 {
    public static void main(String[] args) throws Exception {
        testPostWithParams();
    }

    public static void testPostWithParams() throws ClientProtocolException, IOException {
        // 请求参数
        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("username", "b"));
        formparams.add(new BasicNameValuePair("password", "b"));
        HttpEntity reqEntity = new UrlEncodedFormEntity(formparams, "utf-8");

        // 请求配置
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)// 一、连接超时：connectionTimeout-->指的是连接一个url的连接等待时间
                .setSocketTimeout(5000)// 二、读取数据超时：SocketTimeout-->指的是连接上一个url，获取response的返回等待时间
                .setConnectionRequestTimeout(5000) // 从连接池获取连接超时时间
                .build();

        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://cnivi.com.cn/login");
        post.setEntity(reqEntity);
        post.setConfig(requestConfig);
        HttpResponse response = client.execute(post);
    }
}

class HttpClientPool {
    private static PoolingHttpClientConnectionManager connectionManager = null;
    private static HttpClientBuilder httpBuilder = null;
    private static RequestConfig requestConfig = null;

    private static int MAXCONNECTION = 10;

    private static int DEFAULTMAXCONNECTION = 5;

    private static String IP = "cnivi.com.cn";
    private static int PORT = 80;

    static {  
        //设置http的状态参数  
        requestConfig = RequestConfig.custom()  
                .setSocketTimeout(5000)  
                .setConnectTimeout(5000)  
                .setConnectionRequestTimeout(5000)  
                .build();  
    
        HttpHost target = new HttpHost(IP, PORT);  
        connectionManager = new PoolingHttpClientConnectionManager();  
        connectionManager.setMaxTotal(MAXCONNECTION);//客户端总并行链接最大数  
        connectionManager.setDefaultMaxPerRoute(DEFAULTMAXCONNECTION);//每个主机的最大并行链接数  
        connectionManager.setMaxPerRoute(new HttpRoute(target), 20); // 设置到某个路由的最大并发连接

        httpBuilder = HttpClients.custom();  

        httpBuilder.setConnectionManager(connectionManager);

        httpBuilder.setDefaultRequestConfig(requestConfig);

        // keepalive策略
        httpBuilder.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator it = new BasicHeaderElementIterator(
                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return 60 * 1000;// 如果没有约定，则默认定义时长为60s
            }
        });
    }

    /**
     * 获取连接
     * 
     * @return CloseableHttpClient
     * @date: 2019年12月10日 上午10:26:30
     */
    public static CloseableHttpClient getConnection() {
        CloseableHttpClient httpClient = httpBuilder.build();
        return httpClient;
    }

    /**
     * 根据请求信息获取请求对象
     * 
     * @param map
     * @param url
     * @param method
     * @return HttpUriRequest
     * @date: 2019年12月10日 上午10:27:51
     */
    public static HttpUriRequest getRequestMethod(Map<String, String> map, String url, String method) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        for (Map.Entry<String, String> e : entrySet) {
            String name = e.getKey();
            String value = e.getValue();
            NameValuePair pair = new BasicNameValuePair(name, value);
            params.add(pair);
        }
        HttpUriRequest reqMethod = null;
        if ("post".equals(method)) {
            reqMethod = RequestBuilder.post().setUri(url)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()])).setConfig(requestConfig)
                    .build();
        } else if ("get".equals(method)) {
            reqMethod = RequestBuilder.get().setUri(url)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()])).setConfig(requestConfig)
                    .build();
        }
        return reqMethod;
    }

    /**
     * 手动启动一个新线程来逐出已被关闭的连接
     * 
     * @author mengaijun
     * @Description: TODO
     * @date: 2019年12月10日 上午10:42:45
     */
    public static class IdleConnectionMonitorThread extends Thread {

        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // Close expired connections
                        connMgr.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 30 sec
                        connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }

    }

    // 推荐的方式是定义一个ResponseHandler获取结果，方便你我他，不再自己catch异常和关闭流。
}
