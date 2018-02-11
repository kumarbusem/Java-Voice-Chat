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
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.NamedServiceType;
import org.teleal.cling.model.types.NotificationSubtype;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.protocol.async.ReceivingNotification;
import org.teleal.cling.protocol.async.ReceivingSearch;
import org.teleal.cling.protocol.async.ReceivingSearchResponse;
import org.teleal.cling.protocol.async.SendingNotificationAlive;
import org.teleal.cling.protocol.async.SendingNotificationByebye;
import org.teleal.cling.protocol.async.SendingSearch;
import org.teleal.cling.protocol.sync.ReceivingAction;
import org.teleal.cling.protocol.sync.ReceivingEvent;
import org.teleal.cling.protocol.sync.ReceivingRetrieval;
import org.teleal.cling.protocol.sync.ReceivingSubscribe;
import org.teleal.cling.protocol.sync.ReceivingUnsubscribe;
import org.teleal.cling.protocol.sync.SendingAction;
import org.teleal.cling.protocol.sync.SendingEvent;
import org.teleal.cling.protocol.sync.SendingRenewal;
import org.teleal.cling.protocol.sync.SendingSubscribe;
import org.teleal.cling.protocol.sync.SendingUnsubscribe;

import java.net.URL;
import java.util.logging.Logger;

/**
 * Default implementation, directly instantiates the appropriate protocols.
 *
 * @author Christian Bauer
 */
public class ProtocolFactoryImpl implements ProtocolFactory {

    final private static Logger log = Logger.getLogger(ProtocolFactory.class.getName());

    protected final UpnpService upnpService;

    public ProtocolFactoryImpl(UpnpService upnpService) {
        log.fine("Creating ProtocolFactory: " + getClass().getName());

        this.upnpService = upnpService;
    }

    public UpnpService getUpnpService() {
        return upnpService;
    }

    public ReceivingAsync createReceivingAsync(IncomingDatagramMessage message) throws ProtocolCreationException {
        log.fine("Creating protocol for incoming asynchronous: " + message);

        if (message.getOperation() instanceof UpnpRequest) {
            IncomingDatagramMessage<UpnpRequest> incomingRequest = message;

            switch (incomingRequest.getOperation().getMethod()) {
                case NOTIFY:
                    return isByeBye(incomingRequest) || isSupportedServiceAdvertisement(incomingRequest)
                            ? new ReceivingNotification(getUpnpService(), incomingRequest) : null;
                case MSEARCH:
                    return new ReceivingSearch(getUpnpService(), incomingRequest);
            }

        } else if (message.getOperation() instanceof UpnpResponse) {
            IncomingDatagramMessage<UpnpResponse> incomingResponse = message;

            return isSupportedServiceAdvertisement(incomingResponse)
                    ? new ReceivingSearchResponse(getUpnpService(), incomingResponse) : null;
        }

        throw new ProtocolCreationException("Protocol for incoming datagram message not found: " + message);
    }

    // DO NOT USE THE PARSED/TYPED MSG HEADERS! THIS WOULD DEFEAT THE PURPOSE OF THIS OPTIMIZATION!

    protected boolean isByeBye(IncomingDatagramMessage message) {
        String ntsHeader = message.getHeaders().getFirstHeader(UpnpHeader.Type.NTS.getHttpName());
        return ntsHeader != null && ntsHeader.equals(NotificationSubtype.BYEBYE.getHeaderString());
    }

    protected boolean isSupportedServiceAdvertisement(IncomingDatagramMessage message) {
        ServiceType[] exclusiveServiceTypes = getUpnpService().getConfiguration().getExclusiveServiceTypes();
        if (exclusiveServiceTypes == null) return false; // Discovery is disabled
        if (exclusiveServiceTypes.length == 0) return true; // Any advertisement is fine

        String usnHeader = message.getHeaders().getFirstHeader(UpnpHeader.Type.USN.getHttpName());
        if (usnHeader == null) return false; // Not a service advertisement, drop it

        try {
            NamedServiceType nst = NamedServiceType.valueOf(usnHeader);
            for (ServiceType exclusiveServiceType : exclusiveServiceTypes) {
                if (nst.getServiceType().implementsVersion(exclusiveServiceType))
                    return true;
            }
        } catch (InvalidValueException ex) {
            log.finest("Not a named service type header value: " + usnHeader);
        }
        log.fine("Service advertisement not supported, dropping it: " + usnHeader);
        return false;
    }

    public ReceivingSync createReceivingSync(StreamRequestMessage message) throws ProtocolCreationException {
        log.fine("Creating protocol for incoming synchronous: " + message);

        if (message.getOperation().getMethod().equals(UpnpRequest.Method.GET)) {

            return new ReceivingRetrieval(getUpnpService(), message);

        } else if (getUpnpService().getConfiguration().getNamespace().isControlPath(message.getUri())) {

            if (message.getOperation().getMethod().equals(UpnpRequest.Method.POST))
                return new ReceivingAction(getUpnpService(), message);

        } else if (getUpnpService().getConfiguration().getNamespace().isEventSubscriptionPath(message.getUri())) {

            if (message.getOperation().getMethod().equals(UpnpRequest.Method.SUBSCRIBE)) {

                return new ReceivingSubscribe(getUpnpService(), message);

            } else if (message.getOperation().getMethod().equals(UpnpRequest.Method.UNSUBSCRIBE)) {
                return new ReceivingUnsubscribe(getUpnpService(), message);
            }

        } else if (getUpnpService().getConfiguration().getNamespace().isEventCallbackPath(message.getUri())) {

            if (message.getOperation().getMethod().equals(UpnpRequest.Method.NOTIFY))
                return new ReceivingEvent(getUpnpService(), message);

        }

        throw new ProtocolCreationException("Protocol for message type not found: " + message);
    }

    public SendingNotificationAlive createSendingNotificationAlive(LocalDevice localDevice) {
        return new SendingNotificationAlive(getUpnpService(), localDevice);
    }

    public SendingNotificationByebye createSendingNotificationByebye(LocalDevice localDevice) {
        return new SendingNotificationByebye(getUpnpService(), localDevice);
    }

    public SendingSearch createSendingSearch(UpnpHeader searchTarget, int mxSeconds) {
        return new SendingSearch(getUpnpService(), searchTarget, mxSeconds);
    }

    public SendingAction createSendingAction(ActionInvocation actionInvocation, URL controlURL) {
        return new SendingAction(getUpnpService(), actionInvocation, controlURL);
    }

    public SendingSubscribe createSendingSubscribe(RemoteGENASubscription subscription) {
        return new SendingSubscribe(getUpnpService(), subscription);
    }

    public SendingRenewal createSendingRenewal(RemoteGENASubscription subscription) {
        return new SendingRenewal(getUpnpService(), subscription);
    }

    public SendingUnsubscribe createSendingUnsubscribe(RemoteGENASubscription subscription) {
        return new SendingUnsubscribe(getUpnpService(), subscription);
    }

    public SendingEvent createSendingEvent(LocalGENASubscription subscription) {
        return new SendingEvent(getUpnpService(), subscription);
    }
}
