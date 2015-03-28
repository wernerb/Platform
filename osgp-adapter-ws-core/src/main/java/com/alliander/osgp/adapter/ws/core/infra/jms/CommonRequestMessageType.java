package com.alliander.osgp.adapter.ws.core.infra.jms;

/**
 * Enumeration of common request message types
 * 
 * @author CGI
 * 
 */
public enum CommonRequestMessageType {
    GET_FIRMWARE_VERSION, UPDATE_FIRMWARE, START_SELF_TEST, STOP_SELF_TEST, SET_REBOOT, SET_EVENT_NOTIFICATIONS, SET_CONFIGURATION, SET_TRANSITION, GET_ACTUAL_POWER_USAGE, GET_POWER_USAGE_HISTORY, GET_CONFIGURATION, GET_STATUS, UPDATE_KEY, REVOKE_KEY
}