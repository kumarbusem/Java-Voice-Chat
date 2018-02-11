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

package org.teleal.cling.model.message.discovery;

import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.DeviceUSNHeader;
import org.teleal.cling.model.message.header.InterfaceMacHeader;
import org.teleal.cling.model.message.header.LocationHeader;
import org.teleal.cling.model.message.header.MaxAgeHeader;
import org.teleal.cling.model.message.header.ServiceUSNHeader;
import org.teleal.cling.model.message.header.UDNHeader;
import org.teleal.cling.model.message.header.USNRootDeviceHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.types.NamedDeviceType;
import org.teleal.cling.model.types.NamedServiceType;
import org.teleal.cling.model.types.UDN;

import java.net.URL;

/**
 * @author Christian Bauer
 */
public class IncomingSearchResponse extends IncomingDatagramMessage<UpnpResponse> {

    public IncomingSearchResponse(IncomingDatagramMessage<UpnpResponse> source) {
        super(source);
    }

    public boolean isSearchResponseMessage() {
        UpnpHeader st = getHeaders().getFirstHeader(UpnpHeader.Type.ST);
        UpnpHeader usn = getHeaders().getFirstHeader(UpnpHeader.Type.USN);
        UpnpHeader ext = getHeaders().getFirstHeader(UpnpHeader.Type.EXT); // Has no value!
        return st != null && st.getValue() != null && usn != null && usn.getValue() != null && ext != null;
    }

    public UDN getRootDeviceUDN() {
        // This processes the headers as specified in UDA 1.0, tables in section 1.1.12

        UpnpHeader<UDN> udnHeader = getHeaders().getFirstHeader(UpnpHeader.Type.USN, USNRootDeviceHeader.class);
        if (udnHeader != null) return udnHeader.getValue();

        udnHeader = getHeaders().getFirstHeader(UpnpHeader.Type.USN, UDNHeader.class);
        if (udnHeader != null) return udnHeader.getValue();

        UpnpHeader<NamedDeviceType> deviceTypeHeader = getHeaders().getFirstHeader(UpnpHeader.Type.USN, DeviceUSNHeader.class);
        if (deviceTypeHeader != null) return deviceTypeHeader.getValue().getUdn();

        UpnpHeader<NamedServiceType> serviceTypeHeader = getHeaders().getFirstHeader(UpnpHeader.Type.USN, ServiceUSNHeader.class);
        if (serviceTypeHeader != null) return serviceTypeHeader.getValue().getUdn();

        return null;
    }

    public URL getLocationURL() {
        LocationHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.LOCATION, LocationHeader.class);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    public Integer getMaxAge() {
        MaxAgeHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.MAX_AGE, MaxAgeHeader.class);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    public byte[] getInterfaceMacHeader() {
        InterfaceMacHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.EXT_IFACE_MAC, InterfaceMacHeader.class);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

}
