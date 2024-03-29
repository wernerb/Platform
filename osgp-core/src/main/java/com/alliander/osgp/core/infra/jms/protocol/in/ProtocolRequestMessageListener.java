/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.infra.jms.protocol.in;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.core.domain.model.domain.DomainRequestService;
import com.alliander.osgp.domain.core.entities.DomainInfo;
import com.alliander.osgp.shared.infra.jms.MessageProcessor;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

// This class should fetch request messages from protocol incoming requests queue.
public class ProtocolRequestMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolRequestMessageListener.class);

    private DomainRequestService domainRequestService;
    private List<DomainInfo> domainInfos;
    private ProtocolRequestMessageProcessorMap protocolRequestMessageProcessorMap;

    public ProtocolRequestMessageListener(final DomainRequestService domainRequestService,
            final List<DomainInfo> domainInfos,
            final ProtocolRequestMessageProcessorMap protocolRequestMessageProcessorMap) {
        this.domainRequestService = domainRequestService;
        this.domainInfos = domainInfos;
        this.protocolRequestMessageProcessorMap = protocolRequestMessageProcessorMap;
    }

    @Override
    public void onMessage(final Message message) {
        try {
            LOGGER.info("Received message of type: {}", message.getJMSType());
            final ObjectMessage objectMessage = (ObjectMessage) message;

            // Check if message can be processed by generic OSGP-CORE
            // message processor.
            try {
                final MessageProcessor processor = this.protocolRequestMessageProcessorMap
                        .getMessageProcessor(objectMessage);

                processor.processMessage(objectMessage);

            } catch (final JMSException ex) {
                // The message needs to be sent to a domain adapter.
                this.sendMessageToDomainAdapter((RequestMessage) objectMessage.getObject(), message.getJMSType());

            }

        } catch (final JMSException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
        }
    }

    /**
     * Send the RequestMessage to a domain adapter.
     * 
     * @param requestMessage
     *            The RequestMessage to process.
     * @param messageType
     *            The MessageType of the RequestMessage to process.
     */
    private void sendMessageToDomainAdapter(final RequestMessage requestMessage, final String messageType) {

        //
        // TODO: CHOOSE THE RIGHT DOMAIN INFO, PERHAPS BY USING LOOKUP
        // TABLE?? MAP MESSAGETYPE/FUNCTION TO DOMAIN??
        //
        DomainInfo domainInfo = null;

        for (final DomainInfo di : this.domainInfos) {
            if (di.getDomain().equals("CORE") && di.getDomainVersion().equals("1.0")) {
                domainInfo = di;
            }
        }

        if (domainInfo == null) {
            LOGGER.error(
                    "No DomainInfo found, unable to send message of message type: {} to domain adapater. RequestMessage dropped.",
                    messageType);
        } else {
            // Send message to domain adapter.
            this.domainRequestService.send(requestMessage, messageType, domainInfo);
        }
    }
}
