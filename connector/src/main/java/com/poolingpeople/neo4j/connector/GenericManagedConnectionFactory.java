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
import com.poolingpeople.neo4j.Neo4jClientFactory;

import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.util.Set;
import java.util.logging.Logger;

@ConnectionDefinition(connectionFactory = Neo4jClientFactory.class,
   connectionFactoryImpl = Neo4jRestClientFactory.class,
   connection = Neo4jClient.class,
   connectionImpl = Neo4jRestClient.class)
public class GenericManagedConnectionFactory implements ManagedConnectionFactory, ResourceAdapterAssociation {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private String endPoint = "http://localhost:7474";
    private ResourceAdapter ra;
    PrintWriter out;

    public GenericManagedConnectionFactory() {
        logger.info("#GenericManagedConnectionFactory.constructor");
    }

    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
        logger.info("#GenericManagedConnectionFactory.createConnectionFactory,1");
        return new Neo4jRestClientFactory(this, cxManager);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        logger.info("#GenericManagedConnectionFactory.createManagedFactory,2");
        return new Neo4jRestClientFactory(this, null);
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo info) {
        logger.info("#GenericManagedConnectionFactory.createManagedConnection");
        return new GenericManagedConnection(endPoint, this, info);
    }

    @Override
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo info)
            throws ResourceException {
        logger.info("#GenericManagedConnectionFactory.matchManagedConnections Subject " + subject + " Info: " +  info);
        for (Object con : connectionSet) {
            GenericManagedConnection gmc = (GenericManagedConnection) con;
            ConnectionRequestInfo connectionRequestInfo = gmc.getConnectionRequestInfo();
            if((info == null) || connectionRequestInfo.equals(info))
                return gmc;
        }
        throw new ResourceException("Cannot find connection for info!");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.out = out;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return out;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GenericManagedConnectionFactory other = (GenericManagedConnectionFactory) obj;
        return true;
    }

    @Override
    public int hashCode() {
       return super.hashCode();
    }

    @Override
    public ResourceAdapter getResourceAdapter() {
        return this.ra;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter ra) throws ResourceException {
        this.ra = ra;
    }
}
