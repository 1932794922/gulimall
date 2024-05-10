package com.august.gulimall.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;

/**
 * @author : Crazy_August
 * @description : es配置类
 * // 1.导入依赖
 * // 2.编写配置
 * // 3.注入bean
 * @Time: 2023-04-30   18:26
 */
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class ElasticSearchConfig {

    private String host;
    private int port;

    private String scheme;

    private String fingerprint;

    private String login;

    private String password;


    @Bean
    public ElasticsearchClient esRestClient() {
        ElasticsearchClient client;
        RestClient restClient;
        if (StringUtils.isNotBlank(fingerprint)) {
            SSLContext sslContext = TransportUtils
                    .sslContextFromCaFingerprint(fingerprint);
            BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
            credsProv.setCredentials(
                    AuthScope.ANY, new UsernamePasswordCredentials(login, password)
            );
            restClient = RestClient
                    .builder(new HttpHost(host, port, scheme))
                    .setHttpClientConfigCallback(hc -> hc
                            .setSSLContext(sslContext)
                            .setDefaultCredentialsProvider(credsProv)
                    )
                    .build();
        } else {
            // Create the low-level client
            restClient = RestClient.builder(
                    new HttpHost(host, port, scheme)).build();
        }
        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        // And create the API client
        client = new ElasticsearchClient(transport);
        return client;
    }

}
