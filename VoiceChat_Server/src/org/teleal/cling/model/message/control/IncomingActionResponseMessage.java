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

package org.teleal.cling.model.message.control;

import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpResponse;

/**
 * @author Christian Bauer
 */
public class IncomingActionResponseMessage extends StreamResponseMessage implements ActionResponseMessage {


    public IncomingActionResponseMessage(StreamResponseMessage source) {
        super(source);
    }

    public IncomingActionResponseMessage(UpnpResponse operation) {
        super(operation);
    }

    public String getActionNamespace() {
        return null; // TODO: We _could_ read this in SOAPActionProcessor and set it when we receive a response but why?
    }

    public boolean isFailedNonRecoverable() {
        int statusCode = getOperation().getStatusCode();
        return getOperation().isFailed()
                && !(statusCode == UpnpResponse.Status.METHOD_NOT_SUPPORTED.getStatusCode() ||
                (statusCode == UpnpResponse.Status.INTERNAL_SERVER_ERROR.getStatusCode()) && hasBody());
    }

    public boolean isFailedRecoverable() {
        return hasBody() && getOperation().getStatusCode() == UpnpResponse.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }

}
