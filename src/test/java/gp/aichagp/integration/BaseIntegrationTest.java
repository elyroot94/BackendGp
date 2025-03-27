package gp.aichagp.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        // Nettoyer la base de données avant chaque test
        mongoTemplate.getDb().drop();
    }

    @AfterEach
    void tearDown() {
        // Nettoyer la base de données après chaque test
        mongoTemplate.getDb().drop();
    }
} 