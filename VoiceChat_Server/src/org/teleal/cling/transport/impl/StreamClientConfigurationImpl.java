/*
 * Copyright (C) 2011 Teleal GmbH, Switzerland
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

package org.teleal.cling.transport.impl;

import org.teleal.cling.transport.spi.StreamClientConfiguration;
import org.teleal.cling.model.ServerClientTokens;

/**
 * Settings for the default implementation.
 *
 * @author Christian Bauer
 */
public class StreamClientConfigurationImpl implements StreamClientConfiguration {

    private boolean usePersistentConnections = false;
    private int connectionTimeoutSeconds = 5;
    private int dataReadTimeoutSeconds = 5;

    /**
     * Defaults to <code>false</code>, avoiding obscure bugs in the JDK.
     */
    public boolean isUsePersistentConnections() {
        return usePersistentConnections;
    }

    public void setUsePersistentConnections(boolean usePersistentConnections) {
        this.usePersistentConnections = usePersistentConnections;
    }

    /**
     * Defaults to 5 seconds;
     */
    public int getConnectionTimeoutSeconds() {
        return connectionTimeoutSeconds;
    }

    public void setConnectionTimeoutSeconds(int connectionTimeoutSeconds) {
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
    }

    /**
     * Defaults to 5 seconds.
     */
    public int getDataReadTimeoutSeconds() {
        return dataReadTimeoutSeconds;
    }

    public void setDataReadTimeoutSeconds(int dataReadTimeoutSeconds) {
        this.dataReadTimeoutSeconds = dataReadTimeoutSeconds;
    }

    /**
     * Defaults to the values defined in {@link org.teleal.cling.model.Constants}.
     */
    public String getUserAgentValue(int majorVersion, int minorVersion) {
        return new ServerClientTokens(majorVersion, minorVersion).toString();
    }

}
