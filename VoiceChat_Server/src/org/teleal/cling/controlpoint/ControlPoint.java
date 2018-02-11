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

package org.teleal.cling.controlpoint;

import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.registry.Registry;

/**
 * Unified API for the asynchronous execution of network searches, actions, event subscriptions.
 *
 * @author Christian Bauer
 */
public interface ControlPoint {

    public UpnpServiceConfiguration getConfiguration();
    public ProtocolFactory getProtocolFactory();
    public Registry getRegistry();

    public void search();
    public void search(UpnpHeader searchType);
    public void search(int mxSeconds);
    public void search(UpnpHeader searchType, int mxSeconds);
    public void execute(ActionCallback callback);
    public void execute(SubscriptionCallback callback);

}
