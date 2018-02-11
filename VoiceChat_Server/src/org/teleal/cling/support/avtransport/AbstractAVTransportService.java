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

package org.teleal.cling.support.avtransport;

import org.teleal.cling.binding.annotations.UpnpAction;
import org.teleal.cling.binding.annotations.UpnpInputArgument;
import org.teleal.cling.binding.annotations.UpnpOutputArgument;
import org.teleal.cling.binding.annotations.UpnpService;
import org.teleal.cling.binding.annotations.UpnpServiceId;
import org.teleal.cling.binding.annotations.UpnpServiceType;
import org.teleal.cling.binding.annotations.UpnpStateVariable;
import org.teleal.cling.binding.annotations.UpnpStateVariables;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.model.TransportState;
import org.teleal.cling.support.model.DeviceCapabilities;
import org.teleal.cling.support.model.MediaInfo;
import org.teleal.cling.support.model.PlayMode;
import org.teleal.cling.support.model.PositionInfo;
import org.teleal.cling.support.model.RecordMediumWriteStatus;
import org.teleal.cling.support.model.RecordQualityMode;
import org.teleal.cling.support.model.SeekMode;
import org.teleal.cling.support.model.TransportInfo;
import org.teleal.cling.support.model.TransportSettings;
import org.teleal.cling.support.model.TransportStatus;
import org.teleal.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.teleal.cling.support.model.StorageMedium;
import org.teleal.cling.support.lastchange.LastChange;

import java.beans.PropertyChangeSupport;

/**
 * Skeleton of service with "LastChange" eventing support.
 *
 * @author Christian Bauer
 */
@UpnpService(
        serviceId = @UpnpServiceId("AVTransport"),
        serviceType = @UpnpServiceType(value = "AVTransport", version = 1),
        stringConvertibleTypes = LastChange.class
)
@UpnpStateVariables({
        @UpnpStateVariable(
                name = "TransportState",
                sendEvents = false,
                allowedValuesEnum = TransportState.class),
        @UpnpStateVariable(
                name = "TransportStatus",
                sendEvents = false,
                allowedValuesEnum = TransportStatus.class),
        @UpnpStateVariable(
                name = "PlaybackStorageMedium",
                sendEvents = false,
                defaultValue = "NONE",
                allowedValuesEnum = StorageMedium.class),
        @UpnpStateVariable(
                name = "RecordStorageMedium",
                sendEvents = false,
                defaultValue = "NOT_IMPLEMENTED",
                allowedValuesEnum = StorageMedium.class),
        @UpnpStateVariable(
                name = "PossiblePlaybackStorageMedia",
                sendEvents = false,
                datatype = "string",
                defaultValue = "NETWORK"),
        @UpnpStateVariable(
                name = "PossibleRecordStorageMedia",
                sendEvents = false,
                datatype = "string",
                defaultValue = "NOT_IMPLEMENTED"),
        @UpnpStateVariable( // TODO
                name = "CurrentPlayMode",
                sendEvents = false,
                defaultValue = "NORMAL",
                allowedValuesEnum = PlayMode.class),
        @UpnpStateVariable( // TODO
                name = "TransportPlaySpeed",
                sendEvents = false,
                datatype = "string",
                defaultValue = "1"), // 1, 1/2, 2, -1, 1/10, etc.
        @UpnpStateVariable(
                name = "RecordMediumWriteStatus",
                sendEvents = false,
                defaultValue = "NOT_IMPLEMENTED",
                allowedValuesEnum = RecordMediumWriteStatus.class),
        @UpnpStateVariable(
                name = "CurrentRecordQualityMode",
                sendEvents = false,
                defaultValue = "NOT_IMPLEMENTED",
                allowedValuesEnum = RecordQualityMode.class),
        @UpnpStateVariable(
                name = "PossibleRecordQualityModes",
                sendEvents = false,
                datatype = "string",
                defaultValue = "NOT_IMPLEMENTED"),
        @UpnpStateVariable(
                name = "NumberOfTracks",
                sendEvents = false,
                datatype = "ui4",
                defaultValue = "0"),
        @UpnpStateVariable(
                name = "CurrentTrack",
                sendEvents = false,
                datatype = "ui4",
                defaultValue = "0"),
        @UpnpStateVariable(
                name = "CurrentTrackDuration",
                sendEvents = false,
                datatype = "string"), // H+:MM:SS[.F+] or H+:MM:SS[.F0/F1]
        @UpnpStateVariable(
                name = "CurrentMediaDuration",
                sendEvents = false,
                datatype = "string",
                defaultValue = "00:00:00"),
        @UpnpStateVariable(
                name = "CurrentTrackMetaData",
                sendEvents = false,
                datatype = "string",
                defaultValue = "NOT_IMPLEMENTED"),
        @UpnpStateVariable(
                name = "CurrentTrackURI",
                sendEvents = false,
                datatype = "string"),
        @UpnpStateVariable(
                name = "AVTransportURI",
                sendEvents = false,
                datatype = "string"),
        @UpnpStateVariable(
                name = "AVTransportURIMetaData",
                sendEvents = false,
                datatype = "string",
                defaultValue = "NOT_IMPLEMENTED"),
        @UpnpStateVariable(
                name = "NextAVTransportURI",
                sendEvents = false,
                datatype = "string",
                defaultValue = "NOT_IMPLEMENTED"),
        @UpnpStateVariable(
                name = "NextAVTransportURIMetaData",
                sendEvents = false,
                datatype = "string",
                defaultValue = "NOT_IMPLEMENTED"),
        @UpnpStateVariable(
                name = "RelativeTimePosition",
                sendEvents = false,
                datatype = "string"), // H+:MM:SS[.F+] or H+:MM:SS[.F0/F1] (in track)
        @UpnpStateVariable(
                name = "AbsoluteTimePosition",
                sendEvents = false,
                datatype = "string"), // H+:MM:SS[.F+] or H+:MM:SS[.F0/F1] (in media)
        @UpnpStateVariable(
                name = "RelativeCounterPosition",
                sendEvents = false,
                datatype = "i4",
                defaultValue = "2147483647"), // Max value means not implemented
        @UpnpStateVariable(
                name = "AbsoluteCounterPosition",
                sendEvents = false,
                datatype = "i4",
                defaultValue = "2147483647"), // Max value means not implemented
        @UpnpStateVariable(
                name = "CurrentTransportActions",
                sendEvents = false,
                datatype = "string"), // Play, Stop, Pause, Seek, Next, Previous and Record
        @UpnpStateVariable(
                name = "A_ARG_TYPE_SeekMode",
                sendEvents = false,
                allowedValuesEnum = SeekMode.class), // The 'type' of seek we can perform (or should perform)
        @UpnpStateVariable(
                name = "A_ARG_TYPE_SeekTarget",
                sendEvents = false,
                datatype = "string"), // The actual seek (offset or whatever) value
        @UpnpStateVariable(
                name = "A_ARG_TYPE_InstanceID",
                sendEvents = false,
                datatype = "ui4")
})
public abstract class AbstractAVTransportService {

    @UpnpStateVariable(eventMaximumRateMilliseconds = 200)
    final private LastChange lastChange;
    final protected PropertyChangeSupport propertyChangeSupport;

    protected AbstractAVTransportService() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastChange = new LastChange(new AVTransportLastChangeParser());
    }

    protected AbstractAVTransportService(LastChange lastChange) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastChange = lastChange;
    }

    protected AbstractAVTransportService(PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
        this.lastChange = new LastChange(new AVTransportLastChangeParser());
    }

    protected AbstractAVTransportService(PropertyChangeSupport propertyChangeSupport, LastChange lastChange) {
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

    @UpnpAction
    public abstract void setAVTransportURI(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                                           @UpnpInputArgument(name = "CurrentURI", stateVariable = "AVTransportURI") String currentURI,
                                           @UpnpInputArgument(name = "CurrentURIMetaData", stateVariable = "AVTransportURIMetaData") String currentURIMetaData)
            throws AVTransportException;

    @UpnpAction
    public abstract void setNextAVTransportURI(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                                               @UpnpInputArgument(name = "NextURI", stateVariable = "AVTransportURI") String nextURI,
                                               @UpnpInputArgument(name = "NextURIMetaData", stateVariable = "AVTransportURIMetaData") String nextURIMetaData)
            throws AVTransportException;

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "NrTracks", stateVariable = "NumberOfTracks", getterName = "getNumberOfTracks"),
            @UpnpOutputArgument(name = "MediaDuration", stateVariable = "CurrentMediaDuration", getterName = "getMediaDuration"),
            @UpnpOutputArgument(name = "CurrentURI", stateVariable = "AVTransportURI", getterName = "getCurrentURI"),
            @UpnpOutputArgument(name = "CurrentURIMetaData", stateVariable = "AVTransportURIMetaData", getterName = "getCurrentURIMetaData"),
            @UpnpOutputArgument(name = "NextURI", stateVariable = "NextAVTransportURI", getterName = "getNextURI"),
            @UpnpOutputArgument(name = "NextURIMetaData", stateVariable = "NextAVTransportURIMetaData", getterName = "getNextURIMetaData"),
            @UpnpOutputArgument(name = "PlayMedium", stateVariable = "PlaybackStorageMedium", getterName = "getPlayMedium"),
            @UpnpOutputArgument(name = "RecordMedium", stateVariable = "RecordStorageMedium", getterName = "getRecordMedium"),
            @UpnpOutputArgument(name = "WriteStatus", stateVariable = "RecordMediumWriteStatus", getterName = "getWriteStatus")
    })
    public abstract MediaInfo getMediaInfo(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "CurrentTransportState", stateVariable = "TransportState", getterName = "getCurrentTransportState"),
            @UpnpOutputArgument(name = "CurrentTransportStatus", stateVariable = "TransportStatus", getterName = "getCurrentTransportStatus"),
            @UpnpOutputArgument(name = "CurrentSpeed", stateVariable = "TransportPlaySpeed", getterName = "getCurrentSpeed")
    })
    public abstract TransportInfo getTransportInfo(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "Track", stateVariable = "CurrentTrack", getterName = "getTrack"),
            @UpnpOutputArgument(name = "TrackDuration", stateVariable = "CurrentTrackDuration", getterName = "getTrackDuration"),
            @UpnpOutputArgument(name = "TrackMetaData", stateVariable = "CurrentTrackMetaData", getterName = "getTrackMetaData"),
            @UpnpOutputArgument(name = "TrackURI", stateVariable = "CurrentTrackURI", getterName = "getTrackURI"),
            @UpnpOutputArgument(name = "RelTime", stateVariable = "RelativeTimePosition", getterName = "getRelTime"),
            @UpnpOutputArgument(name = "AbsTime", stateVariable = "AbsoluteTimePosition", getterName = "getAbsTime"),
            @UpnpOutputArgument(name = "RelCount", stateVariable = "RelativeCounterPosition", getterName = "getRelCount"),
            @UpnpOutputArgument(name = "AbsCount", stateVariable = "AbsoluteCounterPosition", getterName = "getAbsCount")
    })
    public abstract PositionInfo getPositionInfo(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "PlayMedia", stateVariable = "PossiblePlaybackStorageMedia", getterName = "getPlayMediaString"),
            @UpnpOutputArgument(name = "RecMedia", stateVariable = "PossibleRecordStorageMedia", getterName = "getRecMediaString"),
            @UpnpOutputArgument(name = "RecQualityModes", stateVariable = "PossibleRecordQualityModes", getterName = "getRecQualityModesString")
    })
    public abstract DeviceCapabilities getDeviceCapabilities(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "PlayMode", stateVariable = "CurrentPlayMode", getterName = "getPlayMode"),
            @UpnpOutputArgument(name = "RecQualityMode", stateVariable = "CurrentRecordQualityMode", getterName = "getRecQualityMode")
    })
    public abstract TransportSettings getTransportSettings(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction
    public abstract void stop(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction
    public abstract void play(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                              @UpnpInputArgument(name = "Speed", stateVariable = "TransportPlaySpeed") String speed)
            throws AVTransportException;

    @UpnpAction
    public abstract void pause(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction
    public abstract void record(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction
    public abstract void seek(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                              @UpnpInputArgument(name = "Unit", stateVariable = "A_ARG_TYPE_SeekMode") String unit,
                              @UpnpInputArgument(name = "Target", stateVariable = "A_ARG_TYPE_SeekTarget") String target)
            throws AVTransportException;

    @UpnpAction
    public abstract void next(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction
    public abstract void previous(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction
    public abstract void setPlayMode(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                                     @UpnpInputArgument(name = "NewPlayMode", stateVariable = "CurrentPlayMode") String newPlayMode)
            throws AVTransportException;

    @UpnpAction
    public abstract void setRecordQualityMode(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
                                              @UpnpInputArgument(name = "NewRecordQualityMode", stateVariable = "CurrentRecordQualityMode") String newRecordQualityMode)
            throws AVTransportException;

    @UpnpAction(out = @UpnpOutputArgument(name = "Actions"))
    public abstract String getCurrentTransportActions(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

}
