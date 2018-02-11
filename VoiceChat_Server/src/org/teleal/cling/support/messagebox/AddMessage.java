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

package org.teleal.cling.support.messagebox;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.messagebox.model.Message;
import org.teleal.common.util.MimeType;

/**
 * @author Christian Bauer
 */
public abstract class AddMessage extends ActionCallback {

    final protected MimeType mimeType = MimeType.valueOf("text/xml;charset=\"utf-8\"");

    public AddMessage(Service service, Message message) {
        super(new ActionInvocation(service.getAction("AddMessage")));

        getActionInvocation().setInput("MessageID", Integer.toString(message.getId()));
        getActionInvocation().setInput("MessageType", mimeType.toString());
        getActionInvocation().setInput("Message", message.toString());
    }

}
