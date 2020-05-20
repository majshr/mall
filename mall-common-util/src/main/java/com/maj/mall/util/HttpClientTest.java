package com.maj.mall.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

public class HttpClientTest {
    private final static String BAIDU = "https://www.baidu.com/";

    /**
     * 连接池
     */
    public static void main(String[] args) {
        // Creating the Client Connection Pool Manager by instantiating the
        // PoolingHttpClientConnectionManager class.
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

        // Set the maximum number of connections in the pool
        connManager.setMaxTotal(100);

        // Create a ClientBuilder Object by setting the connection manager
        HttpClientBuilder clientbuilder = HttpClients.custom().setConnectionManager(connManager);

        // Build the CloseableHttpClient object using the build() method.
        CloseableHttpClient httpclient = clientbuilder.build();// 原文出自【易百教程】，商业转载请联系作者获得授权，非商业请保留原文链接：https://www.yiibai.com/apache_httpclient/apache_httpclient_multiple_threads.html

    }
    
    
    /**
     * ************************************
     * cookie
     * ************************************
     * @param args
     * @throws Exception
     * void 
     * @date: 2019年12月10日 上午9:57:44
     */
    public static void main7(String args[]) throws Exception {
        // ********************创建cookie，并将其加到缓冲区
        // Creating the CookieStore object
        // cookie存储
        CookieStore cookiestore = new BasicCookieStore();

        // Creating client cookies
        BasicClientCookie clientcookie1 = new BasicClientCookie("name", "Maxsu");
        BasicClientCookie clientcookie2 = new BasicClientCookie("age", "28");
        BasicClientCookie clientcookie3 = new BasicClientCookie("place", "Hyderabad");

        // Setting domains and paths to the created cookies
        clientcookie1.setDomain(".sample.com");
        clientcookie2.setDomain(".sample.com");
        clientcookie3.setDomain(".sample.com");

        clientcookie1.setPath("/");
        clientcookie2.setPath("/");
        clientcookie3.setPath("/");

        // Adding the created cookies to cookie store
        cookiestore.addCookie(clientcookie1);
        cookiestore.addCookie(clientcookie2);
        cookiestore.addCookie(clientcookie3);

        // ***************检索cookie
        // Retrieving the cookies
        List list = cookiestore.getCookies();

        // Creating an iterator to the obtained list
        Iterator it = list.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        } // 原文出自【易百教程】，商业转载请联系作者获得授权，非商业请保留原文链接：https://www.yiibai.com/apache_httpclient/apache_httpclient_cookies_management.html

    }

    /**
     * *************************
     * 表单提交，使用RequestBuilder
     * *************************
     */
    /**
     * 
     * @param args
     * @throws Exception
     *             void
     * @date: 2019年12月10日 上午9:37:01
     */
    public static void main6(String args[]) throws Exception {

        // Creating CloseableHttpClient object
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // Creating the RequestBuilder object
        RequestBuilder reqbuilder = RequestBuilder.post();

        // Setting URI and parameters
        RequestBuilder reqbuilder1 = reqbuilder.setUri("http://httpbin.org/post");
        RequestBuilder reqbuilder2 = reqbuilder1.addParameter("Name", "username").addParameter("password", "password");

        // Building the HttpUriRequest object
        HttpUriRequest httppost = reqbuilder2.build();

        // Executing the request
        HttpResponse httpresponse = httpclient.execute(httppost);

        // Printing the status and the contents of the response
        System.out.println(EntityUtils.toString(httpresponse.getEntity()));
        System.out.println(httpresponse.getStatusLine());
    }// 原文出自【易百教程】，商业转载请联系作者获得授权，非商业请保留原文链接：https://www.yiibai.com/apache_httpclient/apache_httpclient_form_based_login.html

    /**
     * 代理验证
     * 
     * 
     */
    /**
     * 
     * @param args
     * @throws ClientProtocolException
     * @throws IOException
     *             void
     * @date: 2019年12月9日 下午8:04:44
     */
    public static void main5(String[] args) throws ClientProtocolException, IOException {
        // Creating the CredentialsProvider object
        CredentialsProvider credsProvider = new BasicCredentialsProvider();

        // Setting the credentials
        credsProvider.setCredentials(new AuthScope("example.com", 80),
                new UsernamePasswordCredentials("user", "mypass"));
        credsProvider.setCredentials(new AuthScope("localhost", 8000),
                new UsernamePasswordCredentials("abc", "passwd"));

        // Creating the HttpClientBuilder
        HttpClientBuilder clientbuilder = HttpClients.custom();

        // Setting the credentials
        clientbuilder = clientbuilder.setDefaultCredentialsProvider(credsProvider);

        // Building the CloseableHttpClient object
        CloseableHttpClient httpclient = clientbuilder.build();

        // Create the target and proxy hosts
        HttpHost targetHost = new HttpHost("example.com", 80, "http");
        HttpHost proxyHost = new HttpHost("localhost", 8000, "http");

        // Setting the proxy
        RequestConfig.Builder reqconfigconbuilder = RequestConfig.custom();
        reqconfigconbuilder = reqconfigconbuilder.setProxy(proxyHost);
        RequestConfig config = reqconfigconbuilder.build();

        // Create the HttpGet request object
        HttpGet httpget = new HttpGet("/");

        // Setting the config to the request
        httpget.setConfig(config);

        // Printing the status line
        HttpResponse response = httpclient.execute(targetHost, httpget);
        System.out.println(response.getStatusLine());// 原文出自【易百教程】，商业转载请联系作者获得授权，非商业请保留原文链接：https://www.yiibai.com/apache_httpclient/apache_httpclient_proxy_authentication.html

    }

    /*****************************
     * 使用代理
     * 
     * 代理服务器是客户端和Internet之间的中间服务器。代理服务器提供以下基本功能 -
     * 防火墙和网络数据过滤
     * 网络连接共享
     * 数据缓存
     * 
     ***********************/

    /**
     * @throws IOException
     * @throws ParseException
     * 
     */
    public static void main4(String[] args) throws ParseException, IOException {
        // 通过将表示代理主机名称的字符串参数(从中需要发送请求)传递给其构造函数来实例化org.apache.http包的HttpHost类。
        HttpHost proxyhost = new HttpHost("localhost");

        // 表示需要向其发送请求的目标主机
        HttpHost targethost = new HttpHost("google.com");

        // HttpRoutePlanner接口计算到指定主机的路由，通过实例化DefaultProxyRoutePlanner类(此接口的实现)来创建此接口的对象。
        // 作为其构造函数的参数，传递上面创建的代理主机
        HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyhost);

        // 将路线规划器设置为客户端构建器
        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder = clientBuilder.setRoutePlanner(routePlanner);

        // Building a CloseableHttpClient
        CloseableHttpClient httpclient = clientBuilder.build();

        // Creating an HttpGet object
        HttpGet httpget = new HttpGet("/");

        // Executing the Get request
        HttpResponse httpresponse = httpclient.execute(targethost, httpget);

        // Printing the status line
        System.out.println(httpresponse.getStatusLine());

        // Printing all the headers of the response
        Header[] headers = httpresponse.getAllHeaders();

        for (int i = 0; i < headers.length; i++) {
            System.out.println(headers[i]);
        }

        // Printing the body of the response
        HttpEntity entity = httpresponse.getEntity();

        if (entity != null) {
            System.out.println(EntityUtils.toString(entity));
        }
    }

    /*************************
     * httpclient认证
     * 
     * @throws IOException
     * @throws ClientProtocolException
     ***************************/
    public static void main3(String[] args) throws ClientProtocolException, IOException {
        // CredentialsProvider接口维护一个集合以保存用户登录凭据。可以通过实例化BasicCredentialsProvider类(此接口的默认实现)来创建其对象。
        CredentialsProvider credentialsPovider = new BasicCredentialsProvider();

        // 验证范围
        AuthScope scope = new AuthScope("https://www.kaops.com/", 80);
        // 认证用户名密码
        Credentials credentials = new UsernamePasswordCredentials("USERNAME", "PASSWORD");
        // 可以设置多个，内部使用put map实现的
        credentialsPovider.setCredentials(scope, credentials);

        // Creating the HttpClientBuilder
        HttpClientBuilder clientbuilder = HttpClients.custom();

        // Setting the credentials
        clientbuilder = clientbuilder.setDefaultCredentialsProvider(credentialsPovider);

        // Building the CloseableHttpClient object
        CloseableHttpClient httpclient = clientbuilder.build();

        // Creating a HttpGet object
        HttpGet httpget = new HttpGet(BAIDU);

        // Printing the method used
        System.out.println(httpget.getMethod());

        // Executing the Get request
        HttpResponse httpresponse = httpclient.execute(httpget);

        // Printing the status line
        System.out.println(httpresponse.getStatusLine());
        int statusCode = httpresponse.getStatusLine().getStatusCode();
        System.out.println(statusCode);

        Header[] headers = httpresponse.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            System.out.println(headers[i].getName());
        }

    }

    /********************* 相应处理程序 ********************/
    // 建议使用响应处理程序处理HTTP响应。
    // 如果使用响应处理程序，则将自动释放所有HTTP连接。
    static class MyResponseHandler implements ResponseHandler<String> {

        @Override
        public String handleResponse(final HttpResponse response) throws IOException {

            // Get the status of the response
            int status = response.getStatusLine().getStatusCode();
            // 如果响应状态代码在200到300之间，则表示该操作已成功接收，理解和接受。
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return "";
                } else {
                    return EntityUtils.toString(entity, Charset.forName("utf-8"));
                }

            } else {
                return "" + status;
            }
        }
    }

    public static void main2(String[] args) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        ResponseHandler<String> responseHandler = new MyResponseHandler();

        HttpGet httpget = new HttpGet(BAIDU);

        String httpresponse = httpclient.execute(httpget, responseHandler);

        System.out.println(httpresponse);
    }

    /*************************************************************/
    /** 如果手动处理HTTP响应而不是使用响应处理程序，则需要自己关闭所有http连接。本章介绍如何手动关闭连接。 */
    /**
     * get,post请求
     * 
     * @param args
     * @throws Exception
     *             void
     * @date: 2019年12月9日 下午7:33:40
     */
    public static void main1(String args[]) throws Exception {

        // 创建一个 HttpClient 对象
        try (CloseableHttpClient httpclient = HttpClients.createDefault();) {
            // 创建一个 HttpGet 对象
            // HttpGet httpget = new HttpGet("https://www.baidu.com/");

            // Printing the method used
            // System.out.println("Request Type: " + httpget.getMethod());

            // Executing the Get request
            // execute接受一个HttpUriRequest类型对象：HttpGet，HttpPost，HttpPut，HttpHead等
            // HttpResponse httpresponse = httpclient.execute(httpget);

            HttpPost httpPost = new HttpPost("https://www.baidu.com/");
            System.out.println("Request Type: " + httpPost.getMethod());

            try (CloseableHttpResponse httpresponse = httpclient.execute(httpPost);) {
                Scanner sc = new Scanner(httpresponse.getEntity().getContent());

                // Printing the status line
                System.out.println(httpresponse.getStatusLine());
                while (sc.hasNext()) {
                    System.out.println(sc.nextLine());
                }
            }
        }

    }

}
