package com.poolingpeople.neo4j.connector;

import com.poolingpeople.neo4j.Neo4jClient;
import com.poolingpeople.neo4j.Neo4jClientFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by alacambra on 12/13/14.
 */
public class DummyTest {
    @Test
    public void myTest(){
        assertTrue("http://localhost:7474/db/".matches(".+db.+"));
        assertTrue(Neo4jClient.class.isAssignableFrom(Neo4jRestClient.class));
        assertTrue(Neo4jClientFactory.class.isAssignableFrom(Neo4jRestClientFactory.class));
    }


}
