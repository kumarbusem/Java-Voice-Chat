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

package org.teleal.cling.support.renderingcontrol;

import org.teleal.cling.binding.annotations.UpnpAction;
import org.teleal.cling.binding.annotations.UpnpInputArgument;
import org.teleal.cling.binding.annotations.UpnpOutputArgument;
import org.teleal.cling.binding.annotations.UpnpService;
import org.teleal.cling.binding.annotations.UpnpServiceId;
import org.teleal.cling.binding.annotations.UpnpServiceType;
import org.teleal.cling.binding.annotations.UpnpStateVariable;
import org.teleal.cling.binding.annotations.UpnpStateVariables;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;
import org.teleal.cling.support.lastchange.LastChange;
import org.teleal.cling.support.model.Channel;
import org.teleal.cling.support.model.PresetName;
import org.teleal.cling.support.model.VolumeDBRange;
import org.teleal.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;

import java.beans.PropertyChangeSupport;

/**
 *
 */
@UpnpService(
        serviceId = @UpnpServiceId("RenderingControl"),
        serviceType = @UpnpServiceType(value = "RenderingControl", version = 1),
        stringConvertibleTypes = LastChange.class
)
@UpnpStateVariables({
        @UpnpStateVariable(
                name = "PresetNameList",
                sendEvents = false,
                datatype = "string"),
        @UpnpStateVariable(
                name = "Mute",
                sendEvents = false,
                datatype = "boolean"),
        @UpnpStateVariable(
                name = "Volume",
                sendEvents = false,
                datatype = "ui2",
                allowedValueMinimum = 0,
                allowedValueMaximum = 100),
        @UpnpStateVariable(
                name = "VolumeDB",
                sendEvents = false,
                datatype = "i2"),
        @UpnpStateVariable(
                name = "Loudness",
                sendEvents = false,
                datatype = "boolean"),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_Channel",
                sendEvents = false,
                allowedValuesEnum = Channel.class),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_PresetName",
                sendEvents = false,
                allowedValuesEnum = PresetName.class),
        @UpnpStateVariable(
                name = "A_ARG_TYPE_InstanceID",
                sendEvents = false,
                datatype = "ui4")

})
public abstract class AbstractAudioRenderingControl {

    @UpnpStateVariable(eventMaximumRateMilliseconds = 200)
    final private LastChange lastChange;

    final protected PropertyChangeSupport propertyChangeSupport;

    protected AbstractAudioRenderingControl() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastChange = new LastChange(new RenderingControlLastChangeParser());
    }

    protected AbstractAudioRenderingControl(LastChange lastChange) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastChange = lastChange;
    }

    protected AbstractAudioRenderingControl(PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
        this.lastChange = new LastChange(new RenderingControlLastChangeParser());
    }

    protected AbstractAudioRenderingControl(PropertyChangeSupport propertyChangeSupport, LastChange lastChange) {
        this.propertyChangeSupport = propertyChangeSupport;
        this.lastChange = lastChange;
    }

    public LastChange getLastChange() {
        return lastChange;
    }

    public void fireLastChange() {
        getLastChange().fire(getPropertyChangeSupport());
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public static UnsignedIntegerFourBytes getDefaultInstanceID() {
        return new UnsignedIntegerFourBytes(0);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "CurrentPresetNameList", stateVariable = "PresetNameList"))
    public String listPresets(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId) throws RenderingControlException {
        return PresetName.FactoryDefault.toString();
    }

    @UpnpAction
    public void selectPreset(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                             @UpnpInputArgument(name = "PresetName") String presetName) throws RenderingControlException {
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "CurrentMute", stateVariable = "Mute"))
    public abstract boolean getMute(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                                    @UpnpInputArgument(name = "Channel") String channelName) throws RenderingControlException;

    @UpnpAction
    public abstract void setMute(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                                 @UpnpInputArgument(name = "Channel") String channelName,
                                 @UpnpInputArgument(name = "DesiredMute", stateVariable = "Mute") boolean desiredMute) throws RenderingControlException;

    @UpnpAction(out = @UpnpOutputArgument(name = "CurrentVolume", stateVariable = "Volume"))
    public abstract UnsignedIntegerTwoBytes getVolume(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                                                      @UpnpInputArgument(name = "Channel") String channelName) throws RenderingControlException;

    @UpnpAction
    public abstract void setVolume(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                                   @UpnpInputArgument(name = "Channel") String channelName,
                                   @UpnpInputArgument(name = "DesiredVolume", stateVariable = "Volume") UnsignedIntegerTwoBytes desiredVolume) throws RenderingControlException;

    @UpnpAction(out = @UpnpOutputArgument(name = "CurrentVolume", stateVariable = "VolumeDB"))
    public Integer getVolumeDB(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                             @UpnpInputArgument(name = "Channel") String channelName) throws RenderingControlException {
        return 0;
    }

    @UpnpAction
    public void setVolumeDB(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                            @UpnpInputArgument(name = "Channel") String channelName,
                            @UpnpInputArgument(name = "DesiredVolume", stateVariable = "VolumeDB") Integer desiredVolumeDB) throws RenderingControlException {
        /*
        VolumeDB volumeDB = new VolumeDB();
        volumeDB.setChannel(channelName);
        volumeDB.setVal(new BigInteger(desiredVolumeDB.toString()));
        */
    }

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "MinValue", stateVariable = "VolumeDB", getterName = "getMinValue"),
            @UpnpOutputArgument(name = "MaxValue", stateVariable = "VolumeDB", getterName = "getMaxValue")
    })
    public VolumeDBRange getVolumeDBRange(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                                          @UpnpInputArgument(name = "Channel") String channelName) throws RenderingControlException {
        return new VolumeDBRange(0, 0);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "CurrentLoudness", stateVariable = "Loudness"))
    public boolean getLoudness(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                               @UpnpInputArgument(name = "Channel") String channelName) throws RenderingControlException {
        return false;
    }

    @UpnpAction
    public void setLoudness(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                            @UpnpInputArgument(name = "Channel") String channelName,
                            @UpnpInputArgument(name = "DesiredLoudness", stateVariable = "Loudness") boolean desiredLoudness) throws RenderingControlException {
/*
        Loudness loudness = new Loudness();
        loudness.setChannel(channelName);
        loudness.setVal(desiredLoudness);
*/
    }

    protected Channel getChannel(String channelName) throws RenderingControlException {
        try {
            return Channel.valueOf(channelName);
        } catch (IllegalArgumentException ex) {
            throw new RenderingControlException(ErrorCode.ARGUMENT_VALUE_INVALID, "Unsupported audio channel: " + channelName);
        }
    }

}
