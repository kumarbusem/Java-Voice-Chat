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

package org.teleal.cling;

import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.registry.RegistryListener;
import org.teleal.cling.registry.Registry;

/**
 * Runs a simple UPnP discovery procedure.
 * <p>
 * Call this class from the command-line to quickly evaluate Cling, it will
 * search for all UPnP devices on your LAN and print out any discovered, added,
 * and removed devices while it is running.
 * </p>
 *
 * @author Christian Bauer
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cling...");

        UpnpService upnpService = new UpnpServiceImpl(
                new RegistryListener() {

                    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
                        System.out.println("Discovery started: " + device.getDisplayString());
                    }

                    public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
                        System.out.println("Discovery failed: " + device.getDisplayString() + " => " + ex);
                    }

                    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
                        System.out.println("Remote device added: " + device.getDisplayString());
                    }

                    public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
                        System.out.println("Remote device updated: " + device.getDisplayString());
                    }

                    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
                        System.out.println("Remote device removed: " + device.getDisplayString());
                    }

                    public void localDeviceAdded(Registry registry, LocalDevice device) {
                        System.out.println("Local device added: " + device.getDisplayString());
                    }

                    public void localDeviceRemoved(Registry registry, LocalDevice device) {
                        System.out.println("Local device removed: " + device.getDisplayString());
                    }

                    public void beforeShutdown(Registry registry) {
                        System.out.println("Before shutdown, the registry has devices: " + registry.getDevices().size());
                    }

                    public void afterShutdown() {
                        System.out.println("Shutdown of registry complete!");

                    }
                }
        );

        upnpService.getControlPoint().search(new STAllHeader()); // Search for all devices and services

        System.out.println("Waiting 10 seconds before shutting down...");

        Thread.sleep(10000);

        System.out.println("Stopping Cling...");
        upnpService.shutdown();
    }
}
