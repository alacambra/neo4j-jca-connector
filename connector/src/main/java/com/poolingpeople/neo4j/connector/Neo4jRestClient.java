/*
 Copyright 2012 Adam Bien, adam-bien.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.poolingpeople.neo4j.connector;

import com.poolingpeople.neo4j.Neo4jClient;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.resource.ResourceException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

public class Neo4jRestClient implements Neo4jClient {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private String endPoint;
    private Closeable closeable;
    Client client;
    String txEndpoint = "/db/data/transaction/";
    String commitEndPoint;


    public Neo4jRestClient(String endPoint, Closeable closeable) {
        this.endPoint = endPoint;
        txEndpoint = this.endPoint + txEndpoint;
        logger.info("#Neo4jRestClient " + toString());
        this.closeable = closeable;
    }

    void createIfNotExists(String endpoint) {
        if(client == null)
            client = ClientBuilder.newClient();
    }

    public void begin() throws ResourceException {

        logger.info("#Neo4jRestClient.begin " + toString());
        this.createIfNotExists(this.txEndpoint);

        Response response = client.target(txEndpoint).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(""));

        if(response.getStatus() != Response.Status.CREATED.getStatusCode()){
            throw new RuntimeException("Error starting transaction");
        }

        commitEndPoint =
                getJsonObjectFromString(response.readEntity(String.class)).getJsonString("commit").getString();

        txEndpoint = response.getHeaderString("Location");

        if(txEndpoint == null || "".equals(txEndpoint))
            throw new RuntimeException("endpoint lost");
    }

    private JsonObject getJsonObjectFromString(String json){
        JsonReader jsonReader = Json.createReader(
                new InputStreamReader(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));

        JsonObject jsonObject = jsonReader.readObject();

        return jsonObject;
    }

    public void commit() throws ResourceException {
        logger.info("#Neo4jRestClient.commit " + toString());
        Response response = client.target(commitEndPoint).request(MediaType.APPLICATION_JSON_TYPE).get();

        if (response.getStatus() != Response.Status.OK.getStatusCode()){
            throw new RuntimeException("Not possible to commit");
        }
    }

    public void rollback() throws ResourceException {
        logger.info("#Neo4jRestClient.rollback  " + toString());
        Response response = client.target(txEndpoint).request(MediaType.APPLICATION_JSON_TYPE).delete();

        if (response.getStatus() != Response.Status.OK.getStatusCode()){
            throw new RuntimeException("Not possible to rollback");
        }

        /*
         @todo Should we close each time or is possible to use the connection for several tx? How to identify a user with a connection?
        */
        client.close();
    }

    public void destroy() {
        /*
        @todo what to do here if a tx is open?
         */
        logger.info("#Neo4jRestClient.close()");
        client.close();
    }

    @Override
    public void close() {
        try {
            this.closeable.close();
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot close GenericManagedConnection",ex);
        }
    }

    @Override
    public String toString() {
        return "Neo4jRestClient{" +  "txEndPoint=" + txEndpoint + ", commitEndPoint="
                + commitEndPoint + ", genericManagedConnection=" + closeable + '}';
    }

    @Override
    public Collection<Map<String, Map<String, Object>>> runCypherQuery(String query, Map<String, Object> params) {
        Response r = client.target(txEndpoint).request(MediaType.APPLICATION_JSON_TYPE)
                .header("Content-type", MediaType.APPLICATION_JSON).post(Entity.json(""));

        /*
         *@todo define mesage body reader and writer for specific classes instead to use a string and de/serialize manually
         * @todo if we give then serializer class is no more needed to use the map. just implement the message body writer
         */
        String body = r.readEntity(String.class);

        return null;
    }

    @Override
    public <T> T runCypherQuery(String query, Map<String, Object> params, Class<T> entityType) {

        Response r = client.target(txEndpoint).request(MediaType.APPLICATION_JSON_TYPE)
                .header("Content-type", MediaType.APPLICATION_JSON)
                .post(Entity.json(entityType));

        T result = r.readEntity(entityType);

        return result;
    }
}
