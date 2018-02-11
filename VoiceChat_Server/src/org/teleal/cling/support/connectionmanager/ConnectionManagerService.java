/*
 * Copyright (C) 2010 Teleal GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.teleal.cling.support.connectionmanager;

import org.teleal.cling.binding.annotations.UpnpAction;
import org.teleal.cling.binding.annotations.UpnpInputArgument;
import org.teleal.cling.binding.annotations.UpnpOutputArgument;
import org.teleal.cling.binding.annotations.UpnpService;
import org.teleal.cling.binding.annotations.UpnpServiceId;
import org.teleal.cling.binding.annotations.UpnpServiceType;
import org.teleal.cling.binding.annotations.UpnpStateVariable;
import org.teleal.cling.binding.annotations.UpnpStateVariables;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.model.types.csv.CSV;
import org.teleal.cling.model.types.csv.CSVUnsignedIntegerFourBytes;
import org.teleal.cling.support.model.ConnectionInfo;
import org.teleal.cling.support.model.ProtocolInfo;
import org.teleal.cling.support.model.ProtocolInfos;

import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Base for connection management, implements the connection ID "0" behavior.
 *
 * @author Christian Bauer
 * @author Alessio Gaeta
 */
@UpnpService(
        serviceId = @UpnpServiceId("ConnectionManager"),
        serviceType = @UpnpServiceType(value = "ConnectionManager", version = 1),
        stringConvertibleTypes = {ProtocolInfo.class, ProtocolInfos.class, ServiceReference.class}
)
@UpnpStateVariables({
        @UpnpStateVariable(name = "SourceProtocolInfo", datatype = "string"),
        @UpnpStateVariable(name = "SinkProtocolInfo", datatype = "string"),
        @UpnpStateVariable(name = "CurrentConnectionIDs", datatype = "string"),
        @UpnpStateVariable(name = "A_ARG_TYPE_ConnectionStatus", allowedValuesEnum = ConnectionInfo.Status.class, sendEvents = false),
        @UpnpStateVariable(name = "A_ARG_TYPE_ConnectionManager", datatype = "string", sendEvents = false),
        @UpnpStateVariable(name = "A_ARG_TYPE_Direction", allowedValuesEnum = ConnectionInfo.Direction.class, sendEvents = false),
        @UpnpStateVariable(name = "A_ARG_TYPE_ProtocolInfo", datatype = "string", sendEvents = false),
        @UpnpStateVariable(name = "A_ARG_TYPE_ConnectionID", datatype = "i4", sendEvents = false),
        @UpnpStateVariable(name = "A_ARG_TYPE_AVTransportID", datatype = "i4", sendEvents = false),
        @UpnpStateVariable(name = "A_ARG_TYPE_RcsID", datatype = "i4", sendEvents = false)
})
public class ConnectionManagerService {

    final private static Logger log = Logger.getLogger(ConnectionManagerService.class.getName());

    final protected PropertyChangeSupport propertyChangeSupport;
    final protected Map<Integer, ConnectionInfo> activeConnections = new ConcurrentHashMap();
    final protected ProtocolInfos sourceProtocolInfo;
    final protected ProtocolInfos sinkProtocolInfo;

    /**
     * Creates a default "active" connection with identifier "0".
     */
    public ConnectionManagerService() {
        this(new ConnectionInfo());
    }

    /**
     * Creates a default "active" connection with identifier "0".
     */
    public ConnectionManagerService(ProtocolInfos sourceProtocolInfo, ProtocolInfos sinkProtocolInfo) {
        this(sourceProtocolInfo, sinkProtocolInfo, new ConnectionInfo());
    }

    public ConnectionManagerService(ConnectionInfo... activeConnections) {
        this(null, new ProtocolInfos(), new ProtocolInfos(), activeConnections);
    }

    public ConnectionManagerService(ProtocolInfos sourceProtocolInfo, ProtocolInfos sinkProtocolInfo, ConnectionInfo... activeConnections) {
        this(null, sourceProtocolInfo, sinkProtocolInfo, activeConnections);
    }

    public ConnectionManagerService(PropertyChangeSupport propertyChangeSupport,
                                            ProtocolInfos sourceProtocolInfo, ProtocolInfos sinkProtocolInfo,
                                            ConnectionInfo... activeConnections) {
        this.propertyChangeSupport =
                propertyChangeSupport == null
                        ? new PropertyChangeSupport(this) : propertyChangeSupport;

        this.sourceProtocolInfo = sourceProtocolInfo;
        this.sinkProtocolInfo = sinkProtocolInfo;

        for (ConnectionInfo activeConnection : activeConnections) {
            this.activeConnections.put(activeConnection.getConnectionID(), activeConnection);
        }
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "RcsID", getterName = "getRcsID"),
            @UpnpOutputArgument(name = "AVTransportID", getterName = "getAvTransportID"),
            @UpnpOutputArgument(name = "ProtocolInfo", getterName = "getProtocolInfo"),
            @UpnpOutputArgument(name = "PeerConnectionManager", stateVariable = "A_ARG_TYPE_ConnectionManager", getterName = "getPeerConnectionManager"),
            @UpnpOutputArgument(name = "PeerConnectionID", stateVariable = "A_ARG_TYPE_ConnectionID", getterName = "getPeerConnectionID"),
            @UpnpOutputArgument(name = "Direction", getterName = "getDirection"),
            @UpnpOutputArgument(name = "Status", stateVariable = "A_ARG_TYPE_ConnectionStatus", getterName = "getConnectionStatus")
    })
    synchronized public ConnectionInfo getCurrentConnectionInfo(@UpnpInputArgument(name = "ConnectionID") int connectionId)
            throws ActionException {
        log.fine("Getting connection information of connection ID: " + connectionId);
        ConnectionInfo info;
        if ((info = activeConnections.get(connectionId)) == null) {
            throw new ConnectionManagerException(
                    ConnectionManagerErrorCode.INVALID_CONNECTION_REFERENCE,
                    "Non-active connection ID: " + connectionId
            );
        }
        return info;
    }

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "ConnectionIDs")
    })
    synchronized public CSV<UnsignedIntegerFourBytes> getCurrentConnectionIDs() {
        CSV<UnsignedIntegerFourBytes> csv = new CSVUnsignedIntegerFourBytes();
        for (Integer connectionID : activeConnections.keySet()) {
            csv.add(new UnsignedIntegerFourBytes(connectionID));
        }
        log.fine("Returning current connection IDs: " + csv.size());
        return csv;
    }

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "Source", stateVariable = "SourceProtocolInfo", getterName = "getSourceProtocolInfo"),
            @UpnpOutputArgument(name = "Sink", stateVariable = "SinkProtocolInfo", getterName = "getSinkProtocolInfo")
    })
    synchronized public void getProtocolInfo() throws ActionException {
        // NOOP
    }

    synchronized public ProtocolInfos getSourceProtocolInfo() {
        return sourceProtocolInfo;
    }

    synchronized public ProtocolInfos getSinkProtocolInfo() {
        return sinkProtocolInfo;
    }
}
