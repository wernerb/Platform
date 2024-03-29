/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.infra.jms.domain.in;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.core.domain.model.protocol.ProtocolResponseService;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.domain.core.exceptions.OsgpCoreException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

// This class should fetch incoming response messages from a responses queue.
public class DomainResponseMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainResponseMessageListener.class);

    private ProtocolResponseService protocolResponseService;

    private List<ProtocolInfo> protocolInfos;

    public DomainResponseMessageListener(final ProtocolResponseService protocolResponseService,
            final List<ProtocolInfo> protocolInfos) {
        this.protocolResponseService = protocolResponseService;
        this.protocolInfos = protocolInfos;
    }

    @Override
    public void onMessage(final Message message) {
        try {
            final ObjectMessage objectMessage = (ObjectMessage) message;
            final String messageType = objectMessage.getJMSType();

            final Object dataObject = objectMessage.getObject();

            LOGGER.info("Received domain incoming response message off type [{}]", messageType);

            ProtocolInfo protocolInfo = null;

            //
            // TODO: MAKE SURE THE PROTOCOL INFO IS CHOSEN!!!
            //
            for (final ProtocolInfo pi : this.protocolInfos) {
                if (pi.getProtocol().equals("OSLP") && pi.getProtocolVersion().equals("1.0")) {
                    protocolInfo = pi;
                }
            }

            if (protocolInfo == null) {
                throw new OsgpCoreException("No protocol info!");
            }

            switch (messageType) {

            case "REGISTER_DEVICE":
                final ResponseMessage responseMessage = (ResponseMessage) dataObject;
                this.protocolResponseService.send(responseMessage, messageType, protocolInfo);
                break;

            default:
                throw new OsgpCoreException("Unknown JMSType: " + messageType);
            }

        } catch (final JMSException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
        } catch (final OsgpCoreException e) {
            LOGGER.error("OsgpCoreException", e);
        }
    }
}
