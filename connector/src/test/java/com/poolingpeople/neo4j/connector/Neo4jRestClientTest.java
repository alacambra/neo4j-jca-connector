package com.poolingpeople.neo4j.connector;

//import com.poolingpeople.testUtils.EmbeddedTestServer;
//import com.poolingpeople.testUtils.LocalNeo4jRestServer;
import junit.framework.TestCase;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import org.junit.runners.JUnit4;

import javax.resource.ResourceException;


import java.io.Closeable;

@RunWith(JUnit4.class)
public class Neo4jRestClientTest extends TestCase {

//    static LocalNeo4jRestServer server;
    Neo4jRestClient cur;
    private Closeable closeable;


    @BeforeClass
    public static void load() throws Exception {
//        server = new EmbeddedTestServer("localhost", 7475);
//        server.start();
    }

    @Before
    public void init() throws ResourceException {
//        this.closeable = mock(Closeable.class);
//        cur = new Neo4jRestClient(new PrintWriter(System.out), "http://localhost:7474", closeable);
    }

    @AfterClass
    public static void stop() throws Exception {

//        server.shutdown();
    }

    public void testCreateIfNotExists() throws Exception {
        assertTrue(true);
    }

    @Test
    public void testBegin() throws Exception {

//        assertThat("transaction succsefully started", cur.txEndpoint,
//                is(not(RegexMatcher.matches(".+/db/data/transaction/[\\d]+"))));
//
//        cur.begin();
//
//        assertThat("transaction succsefully started", cur.txEndpoint,
//                RegexMatcher.matches(".+/db/data/transaction/[\\d]+"));
//        System.out.println(cur.txEndpoint);
//
//        cur.close();
    }

    public void testCommit() throws Exception {



    }

    public void testRollback() throws Exception {

    }

    public void testDestroy() throws Exception {

    }

    public void testRunCypherQuery() throws Exception {

    }
}