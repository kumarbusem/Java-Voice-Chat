/*
 * Copyright (C) 2010 Alessio Gaeta <alessio.gaeta@gmail.com>
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

package org.teleal.cling.support.connectionmanager.callback;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.model.ConnectionInfo;
import org.teleal.cling.support.model.ProtocolInfo;

/**
 * @author Alessio Gaeta
 * @author Christian Bauer
 */
public abstract class PrepareForConnection extends ActionCallback {

    public PrepareForConnection(Service service,
                                ProtocolInfo remoteProtocolInfo, ServiceReference peerConnectionManager,
                                int peerConnectionID, ConnectionInfo.Direction direction) {
        this(service, null, remoteProtocolInfo, peerConnectionManager, peerConnectionID, direction);
    }

    public PrepareForConnection(Service service, ControlPoint controlPoint,
                                ProtocolInfo remoteProtocolInfo, ServiceReference peerConnectionManager,
                                int peerConnectionID, ConnectionInfo.Direction direction) {
        super(new ActionInvocation(service.getAction("PrepareForConnection")), controlPoint);

        getActionInvocation().setInput("RemoteProtocolInfo", remoteProtocolInfo.toString());
        getActionInvocation().setInput("PeerConnectionManager", peerConnectionManager.toString());
        getActionInvocation().setInput("PeerConnectionID", peerConnectionID);
        getActionInvocation().setInput("Direction", direction.toString());
    }

    @Override
    public void success(ActionInvocation invocation) {
        received(
                invocation,
                (Integer)invocation.getOutput("ConnectionID").getValue(),
                (Integer)invocation.getOutput("RcsID").getValue(),
                (Integer)invocation.getOutput("AVTransportID").getValue()
        );
    }

    public abstract void received(ActionInvocation invocation, int connectionID, int rcsID, int avTransportID);

}
