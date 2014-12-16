package com.poolingpeople.neo4j.connectortester;

import com.poolingpeople.neo4j.Neo4jClientFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * Created by alacambra on 12/13/14.
 */
@Path("neo")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Neo4jResource {

    @Resource(name = "java:/eis/neo4j/restTx")
    Neo4jClientFactory factory;

    @PostConstruct
    public void init(){
        if (factory == null)
            throw new RuntimeException("factory not injected");
    }

    @GET
    public JsonObject test(){
        factory.getClient().runCypherQuery("",null);
        return Json.createObjectBuilder().add("test", "test1").build();
    }

    public void setFactory(Neo4jClientFactory factory) {
        this.factory = factory;
    }
}
