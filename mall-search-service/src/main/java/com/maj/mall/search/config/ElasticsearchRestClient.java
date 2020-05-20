package com.maj.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchRestClient {
    static Logger LOG = LoggerFactory.getLogger(ElasticsearchRestClient.class);

    private static final int ADDRESS_LENGTH = 2;

    private static final String HTTP_SCHEME = "http";

    @Value("${elasticsearch.ip}")
    String[] ipAddress;

    @Bean
    public RestClientBuilder restClientBuilder() {
        if (ipAddress == null) {
            LOG.error("elastic search配置错误！");
            return null;
        }

        HttpHost[] hosts = new HttpHost[ipAddress.length];
        for (int i = 0; i < ipAddress.length; ++i) {
            String ipAddr = ipAddress[i];
            hosts[i] = makeHttpHost(ipAddr);
        }

        return RestClient.builder(hosts);
    }

    /**
     * 高级版elsearch客户端
     * 
     * @param restClientBuilder
     * @return RestHighLevelClient
     * @date: 2019年12月2日 下午4:39:53
     */
    @Bean(name = "highLevelClient")
    public RestHighLevelClient highLevelClient(@Autowired RestClientBuilder restClientBuilder) {
        return new RestHighLevelClient(restClientBuilder);
    }

    /**
     * 根据ip:port生成HttpHost对象
     * 
     * @param s
     * @return HttpHost
     * @date: 2019年12月2日 下午4:36:53
     */
    private HttpHost makeHttpHost(String s) {
        String[] address = s.split(":");
        if (address.length == ADDRESS_LENGTH) {
            String ip = address[0];
            int port = Integer.parseInt(address[1]);
            return new HttpHost(ip, port, HTTP_SCHEME);
        } else {
            return null;
        }
    }
}
