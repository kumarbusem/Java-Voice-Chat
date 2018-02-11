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

import org.teleal.cling.model.state.StateVariableValue;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.header.ContentTypeHeader;
import org.teleal.cling.model.message.header.EventSequenceHeader;
import org.teleal.cling.model.message.header.NTEventHeader;
import org.teleal.cling.model.message.header.NTSHeader;
import org.teleal.cling.model.message.header.SubscriptionIdHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.types.NotificationSubtype;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.model.gena.GENASubscription;

import java.net.URL;
import java.util.Collection;

/**
 * @author Christian Bauer
 */
public class OutgoingEventRequestMessage extends StreamRequestMessage {

    final private Collection<StateVariableValue> stateVariableValues;

    public OutgoingEventRequestMessage(GENASubscription subscription,
                                       URL callbackURL,
                                       UnsignedIntegerFourBytes sequence,
                                       Collection<StateVariableValue> values) {

        super(new UpnpRequest(UpnpRequest.Method.NOTIFY, callbackURL));

        getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, new ContentTypeHeader());
        getHeaders().add(UpnpHeader.Type.NT, new NTEventHeader());
        getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.PROPCHANGE));
        getHeaders().add(UpnpHeader.Type.SID, new SubscriptionIdHeader(subscription.getSubscriptionId()));

        // Important! Pass by value so that we can safely increment it afterwards and before this is send!
        getHeaders().add(UpnpHeader.Type.SEQ, new EventSequenceHeader(sequence.getValue()));

        this.stateVariableValues = values;
    }

    public OutgoingEventRequestMessage(GENASubscription subscription, URL callbackURL) {
        this(subscription, callbackURL, subscription.getCurrentSequence(), subscription.getCurrentValues().values());
    }

    public Collection<StateVariableValue> getStateVariableValues() {
        return stateVariableValues;
    }
}
