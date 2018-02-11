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

package org.teleal.cling.model.message.gena;

import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.header.CallbackHeader;
import org.teleal.cling.model.message.header.NTEventHeader;
import org.teleal.cling.model.message.header.TimeoutHeader;
import org.teleal.cling.model.message.header.UpnpHeader;

import java.net.URL;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class OutgoingSubscribeRequestMessage extends StreamRequestMessage {

    public OutgoingSubscribeRequestMessage(RemoteGENASubscription subscription, List<URL> callbackURLs) {

        super(UpnpRequest.Method.SUBSCRIBE, subscription.getEventSubscriptionURL());

        getHeaders().add(
                UpnpHeader.Type.CALLBACK,
                new CallbackHeader(callbackURLs)
        );

        getHeaders().add(
                UpnpHeader.Type.NT,
                new NTEventHeader()
        );

        getHeaders().add(
                UpnpHeader.Type.TIMEOUT,
                new TimeoutHeader(subscription.getRequestedDurationSeconds())
        );
    }

    public boolean hasCallbackURLs() {
        CallbackHeader callbackHeader =
                getHeaders().getFirstHeader(UpnpHeader.Type.CALLBACK, CallbackHeader.class);
        return callbackHeader.getValue().size() > 0;
    }

}
