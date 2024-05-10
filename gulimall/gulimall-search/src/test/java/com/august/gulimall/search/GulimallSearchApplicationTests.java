package com.august.gulimall.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.json.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
@Slf4j
class GulimallSearchApplicationTests {

    @Autowired
    public ElasticsearchClient client;

    @Test
    void contextLoads() throws IOException {
        System.out.println("------------------" + client);
        CreateIndexResponse createResponse = client.indices().create(
                new CreateIndexRequest.Builder()
                        .index("my-index")
                        .aliases("foo",
                                new Alias.Builder().isWriteIndex(true).build()
                        )
                        .build()
        );
        CreateIndexResponse createResponse1 = client.indices().create(
                createIndexBuilder -> createIndexBuilder
                        .index("my-index")
                        .aliases("foo", aliasBuilder -> aliasBuilder
                                .isWriteIndex(true)
                        )
                );
    }

    @Test
    public void getIndex() throws IOException {
        SearchResponse<JsonData> search = client.search(SearchRequest.of(d -> d.index("emp")),JsonData.class);
        System.out.println(search);

    }

}
