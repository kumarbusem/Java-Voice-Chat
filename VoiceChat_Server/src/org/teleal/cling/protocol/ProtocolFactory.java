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

package org.teleal.cling.protocol;

import org.teleal.cling.UpnpService;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.gena.LocalGENASubscription;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.protocol.async.SendingNotificationAlive;
import org.teleal.cling.protocol.async.SendingNotificationByebye;
import org.teleal.cling.protocol.async.SendingSearch;
import org.teleal.cling.protocol.sync.SendingAction;
import org.teleal.cling.protocol.sync.SendingEvent;
import org.teleal.cling.protocol.sync.SendingRenewal;
import org.teleal.cling.protocol.sync.SendingSubscribe;
import org.teleal.cling.protocol.sync.SendingUnsubscribe;

import java.net.URL;

/**
 * Factory for UPnP protocols, the core implementation of the UPnP specification.
 * <p>
 * This factory creates an executable protocol either based on the received UPnP messsage, or
 * on local device/search/service metadata). A protocol is an aspect of the UPnP specification,
 * you can override individual protocols to customize the behavior of the UPnP stack.
 * </p>
 * <p>
 * An implementation has to be thread-safe.
 * </p>
 * 
 * @author Christian Bauer
 */
public interface ProtocolFactory {

    public UpnpService getUpnpService();

    /**
     * Creates a {@link org.teleal.cling.protocol.async.ReceivingNotification},
     * {@link org.teleal.cling.protocol.async.ReceivingSearch},
     * or {@link org.teleal.cling.protocol.async.ReceivingSearchResponse} protocol.
     *
     * @param message The incoming message, either {@link org.teleal.cling.model.message.UpnpRequest} or
     *                {@link org.teleal.cling.model.message.UpnpResponse}.
     * @return        The appropriate protocol that handles the messages or <code>null</code> if the message should be dropped.
     * @throws ProtocolCreationException If no protocol could be found for the message.
     */
    public ReceivingAsync createReceivingAsync(IncomingDatagramMessage message) throws ProtocolCreationException;

    /**
     * Creates a {@link org.teleal.cling.protocol.sync.ReceivingRetrieval},
     * {@link org.teleal.cling.protocol.sync.ReceivingAction},
     * {@link org.teleal.cling.protocol.sync.ReceivingSubscribe},
     * {@link org.teleal.cling.protocol.sync.ReceivingUnsubscribe}, or
     * {@link org.teleal.cling.protocol.sync.ReceivingEvent} protocol.
     *
     * @param requestMessage The incoming message, examime {@link org.teleal.cling.model.message.UpnpRequest.Method}
     *                       to determine the protocol.
     * @return        The appropriate protocol that handles the messages.
     * @throws ProtocolCreationException If no protocol could be found for the message.
     */
    public ReceivingSync createReceivingSync(StreamRequestMessage requestMessage) throws ProtocolCreationException;

    /**
     * Called by the {@link org.teleal.cling.registry.Registry}, creates a protocol for announcing local devices.
     */
    public SendingNotificationAlive createSendingNotificationAlive(LocalDevice localDevice);

    /**
     * Called by the {@link org.teleal.cling.registry.Registry}, creates a protocol for announcing local devices.
     */
    public SendingNotificationByebye createSendingNotificationByebye(LocalDevice localDevice);

    /**
     * Called by the {@link org.teleal.cling.controlpoint.ControlPoint}, creates a protocol for a multicast search.
     */
    public SendingSearch createSendingSearch(UpnpHeader searchTarget, int mxSeconds);

    /**
     * Called by the {@link org.teleal.cling.controlpoint.ControlPoint}, creates a protocol for executing an action.
     */
    public SendingAction createSendingAction(ActionInvocation actionInvocation, URL controlURL);

    /**
     * Called by the {@link org.teleal.cling.controlpoint.ControlPoint}, creates a protocol for GENA subscription.
     */
    public SendingSubscribe createSendingSubscribe(RemoteGENASubscription subscription);

    /**
     * Called by the {@link org.teleal.cling.controlpoint.ControlPoint}, creates a protocol for GENA renewal.
     */
    public SendingRenewal createSendingRenewal(RemoteGENASubscription subscription);

    /**
     * Called by the {@link org.teleal.cling.controlpoint.ControlPoint}, creates a protocol for GENA unsubscription.
     */
    public SendingUnsubscribe createSendingUnsubscribe(RemoteGENASubscription subscription);

    /**
     * Called by the {@link org.teleal.cling.model.gena.GENASubscription}, creates a protocol for sending GENA events.
     */
    public SendingEvent createSendingEvent(LocalGENASubscription subscription);
}
