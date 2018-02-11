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

package org.teleal.cling.model.types;

import org.teleal.common.util.ByteArray;
import org.teleal.common.util.HexBin;

/**
 * @author Christian Bauer
 */
public class BinHexDatatype extends AbstractDatatype<Byte[]> {

    public BinHexDatatype() {
    }

    public Class<Byte[]> getValueType() {
        return Byte[].class;
    }

    public Byte[] valueOf(String s) throws InvalidValueException {
        if (s.equals("")) return null;
        try {
            return ByteArray.toWrapper(HexBin.stringToBytes(s));
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getString(Byte[] value) throws InvalidValueException {
        if (value == null) return "";
        try {
            return HexBin.bytesToString(ByteArray.toPrimitive(value));
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }

}