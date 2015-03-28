package com.alliander.osgp.adapter.domain.core.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

// This class should send response messages to the incoming responses queue of OSGP Core.
@Component(value = "domainCoreOutgoingOsgpCoreResponsesMessageSender")
public class OsgpCoreResponseMessageSender {

    @Autowired
    @Qualifier("domainCoreOutgoingOsgpCoreResponsesJmsTemplate")
    private JmsTemplate osgpCoreResponsesIncomingJmsTemplate;

    public void send(final ResponseMessage responseMessage, final String messageType) {

        this.osgpCoreResponsesIncomingJmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage();

                objectMessage.setJMSType(messageType);
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        responseMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        responseMessage.getDeviceIdentification());
                objectMessage.setObject(responseMessage);

                return objectMessage;
            }
        });
    }
}