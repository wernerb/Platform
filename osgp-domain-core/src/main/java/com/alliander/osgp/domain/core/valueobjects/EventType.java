/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects;

public enum EventType {
    DIAG_EVENTS_GENERAL(0), HARDWARE_FAILURE_RELAY(1), LIGHT_FAILURE_DALI_COMMUNICATION(2), LIGHT_FAILURE_BALLAST(3), LIGHT_EVENTS_LIGHT_ON(
            4), LIGHT_EVENTS_LIGHT_OFF(5), MONITOR_EVENTS_LONG_BUFFER_FULL(6), FIRMWARE_EVENTS_ACTIVATING(7), FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND(
            8), FIRMWARE_EVENTS_DOWNLOAD_FAILED(9), LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT(10), TARIFF_EVENTS_TARIFF_ON(11), TARIFF_EVENTS_TARIFF_OFF(
            12), MONITOR_FAILURE_P1_COMMUNICATION(13), COMM_EVENTS_ALTERNATIVE_CHANNEL(14), COMM_EVENTS_RECOVERED_CHANNEL(
            15), SECURITY_EVENTS_OUT_OF_SEQUENCE(16), MONITOR_SHORT_DETECTED(17), MONITOR_SHORT_RESOLVED(18), MONITOR_DOOR_OPENED(
            19), MONITOR_DOOR_CLOSED(20);

    private int value;

    EventType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}