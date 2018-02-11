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

package org.teleal.cling.support.renderingcontrol.callback;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.model.Channel;

import java.util.logging.Logger;

/**
 *
 * @author Christian Bauer
 */
public abstract class GetMute extends ActionCallback {

    private static Logger log = Logger.getLogger(GetMute.class.getName());

    public GetMute(Service service) {
        this(new UnsignedIntegerFourBytes(0), service);
    }
    public GetMute(UnsignedIntegerFourBytes instanceId, Service service) {
        super(new ActionInvocation(service.getAction("GetMute")));
        getActionInvocation().setInput("InstanceID", instanceId);
        getActionInvocation().setInput("Channel", Channel.Master.toString());
    }

    public void success(ActionInvocation invocation) {
        boolean currentMute = (Boolean) invocation.getOutput("CurrentMute").getValue();
        received(invocation, currentMute);
    }

    public abstract void received(ActionInvocation actionInvocation, boolean currentMute);
}