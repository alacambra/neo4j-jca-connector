
package com.poolingpeople.neo4j.connector;

import java.io.Closeable;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.resource.ResourceException;
import static javax.resource.spi.ConnectionEvent.*;
import javax.resource.spi.*;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

public class GenericManagedConnection
        implements ManagedConnection, LocalTransaction,Closeable {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private ManagedConnectionFactory mcf;
    private Neo4jRestClient neo4jConnection;
    private ConnectionRequestInfo connectionRequestInfo;
    private List<ConnectionEventListener> listeners;
    private final String endPoint;
    private PrintWriter out;

    GenericManagedConnection(String endPoint,ManagedConnectionFactory mcf,
                             ConnectionRequestInfo connectionRequestInfo) {
        this.endPoint = endPoint;
        logger.info("#GenericManagedConnection");
        this.mcf = mcf;
        this.connectionRequestInfo = connectionRequestInfo;
        this.listeners = new LinkedList<>();
        this.neo4jConnection = new Neo4jRestClient(this.endPoint,this);
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo)
            throws ResourceException {
        logger.info("#GenericManagedConnection.getConnection");
        return neo4jConnection;
    }

    @Override
    public void destroy() {
        logger.info("#GenericManagedConnection.destroy");
        this.neo4jConnection.destroy();
    }

    @Override
    public void cleanup() {
        logger.info("#GenericManagedConnection.cleanup");
//        this.neo4jConnection.clear();
    }

    @Override
    public void associateConnection(Object connection) {
        logger.info("#GenericManagedConnection.associateConnection " + connection);
        this.neo4jConnection = (Neo4jRestClient) connection;

    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        logger.info("#GenericManagedConnection.addConnectionEventListener");
        this.listeners.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        logger.info("#GenericManagedConnection.removeConnectionEventListener");
        this.listeners.remove(listener);
    }

    @Override
    public XAResource getXAResource()
            throws ResourceException {
        logger.info("#GenericManagedConnection.getXAResource");
        throw new ResourceException("XA protocol is not supported by the file-jca adapter");
    }

    @Override
    public LocalTransaction getLocalTransaction() {
        logger.info("#GenericManagedConnection.getLocalTransaction");
        return this;
    }

    @Override
    public ManagedConnectionMetaData getMetaData()
            throws ResourceException {
        logger.info("#GenericManagedConnection.getMetaData");
        return new ManagedConnectionMetaData() {

            public String getEISProductName()
                    throws ResourceException {
                logger.info("#GenericManagedConnection.getEISProductName");
                return "File JCA";
            }

            public String getEISProductVersion()
                    throws ResourceException {
                logger.info("#GenericManagedConnection.getEISProductVersion");
                return "1.0";
            }

            public int getMaxConnections()
                    throws ResourceException {
                logger.info("#GenericManagedConnection.getMaxConnections");
                return 5;
            }

            public String getUserName()
                    throws ResourceException {
                return null;
            }
        };
    }

    @Override
    public void setLogWriter(PrintWriter out)
            throws ResourceException {
        this.logger.info("#GenericManagedConnection.setLogWriter");
        this.out = out;
    }

    @Override
    public PrintWriter getLogWriter()
            throws ResourceException {
        this.logger.info("#GenericManagedConnection.getLogWriter");
        return out;
    }


    ConnectionRequestInfo getConnectionRequestInfo() {
        return connectionRequestInfo;
    }

    @Override
    public void begin() throws ResourceException {
        this.neo4jConnection.begin();
        this.fireConnectionEvent(LOCAL_TRANSACTION_STARTED);
    }

    @Override
    public void commit() throws ResourceException {
        this.neo4jConnection.commit();
        this.fireConnectionEvent(LOCAL_TRANSACTION_COMMITTED);
    }

    @Override
    public void rollback() throws ResourceException {
        this.neo4jConnection.rollback();
        this.fireConnectionEvent(LOCAL_TRANSACTION_ROLLEDBACK);
    }

    public void fireConnectionEvent(int event) {
        ConnectionEvent connnectionEvent = new ConnectionEvent(this, event);
        connnectionEvent.setConnectionHandle(this.neo4jConnection);
        for (ConnectionEventListener listener : this.listeners) {
            switch (event) {
                case LOCAL_TRANSACTION_STARTED:
                    listener.localTransactionStarted(connnectionEvent);
                    break;
                case LOCAL_TRANSACTION_COMMITTED:
                    listener.localTransactionCommitted(connnectionEvent);
                    break;
                case LOCAL_TRANSACTION_ROLLEDBACK:
                    listener.localTransactionRolledback(connnectionEvent);
                    break;
                case CONNECTION_CLOSED:
                    listener.connectionClosed(connnectionEvent);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown event: " + event);
            }
        }
    }

    @Override
   public void close() {
        this.fireConnectionEvent(CONNECTION_CLOSED);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GenericManagedConnection other = (GenericManagedConnection) obj;
        if (this.connectionRequestInfo != other.connectionRequestInfo
                && (this.connectionRequestInfo == null
                || !this.connectionRequestInfo.equals(other.connectionRequestInfo))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.connectionRequestInfo != null ? this.connectionRequestInfo.hashCode() : 0);
        return hash;
    }
    
}
