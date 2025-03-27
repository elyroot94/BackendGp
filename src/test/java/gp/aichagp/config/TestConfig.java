package gp.aichagp.config;

import com.mongodb.client.MongoClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@TestConfiguration
public class TestConfig {

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(
            new SimpleMongoClientDatabaseFactory(mongoClient, "testdb")
        );
    }
} 