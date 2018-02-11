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

package org.teleal.cling.model.types.csv;

import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerOneByte;

/**
 * @author Christian Bauer
 */
public class CSVUnsignedIntegerOneByte extends CSV<UnsignedIntegerOneByte> {

    public CSVUnsignedIntegerOneByte() {
    }

    public CSVUnsignedIntegerOneByte(String s) throws InvalidValueException {
        super(s);
    }
}