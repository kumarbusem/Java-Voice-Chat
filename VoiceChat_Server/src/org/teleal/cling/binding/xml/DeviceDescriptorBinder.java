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

package org.teleal.cling.binding.xml;

import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.profile.ControlPointInfo;
import org.w3c.dom.Document;

/**
 * Reads and generates device descriptor XML metadata.
 *
 * @author Christian Bauer
 */
public interface DeviceDescriptorBinder {

    public <T extends Device> T describe(T undescribedDevice, String descriptorXml)
            throws DescriptorBindingException, ValidationException;

    public <T extends Device> T describe(T undescribedDevice, Document dom)
            throws DescriptorBindingException, ValidationException;

    public String generate(Device device, ControlPointInfo info, Namespace namespace) throws DescriptorBindingException;

    public Document buildDOM(Device device, ControlPointInfo info, Namespace namespace) throws DescriptorBindingException;

}
