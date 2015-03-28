package com.alliander.osgp.adapter.ws.admin.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for sending common request messages to a queue
 * 
 * @author CGI
 * 
 */
public class AdminRequestMessageSender {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminRequestMessageSender.class);

    /**
     * Autowired field for admin requests jms template
     */
    @Autowired
    @Qualifier("wsAdminOutgoingRequestsJmsTemplate")
    private JmsTemplate adminRequestsJmsTemplate;

    /**
     * Method for sending a request message to the queue
     * 
     * @param requestMessage
     *            The adminRequestMessage request message to send.
     */
    public void send(final AdminRequestMessage requestMessage) {
        LOGGER.debug("Sending common request message");

        if (requestMessage.getMessageType() == null) {
            LOGGER.error("MessageType is null");
            return;
        }
        if (StringUtils.isBlank(requestMessage.getOrganisationIdentification())) {
            LOGGER.error("OrganisationIdentification is blank");
            return;
        }
        if (StringUtils.isBlank(requestMessage.getDeviceIdentification())) {
            LOGGER.error("DeviceIdentification is blank");
            return;
        }
        if (StringUtils.isBlank(requestMessage.getCorrelationUid())) {
            LOGGER.error("CorrelationUid is blank");
            return;
        }

        this.sendMessage(requestMessage);
    }

    /**
     * Method for sending a request message to the admin requests queue
     * 
     * @param requestMessage
     *            The CommonRequestMessage request message to send.
     */
    private void sendMessage(final AdminRequestMessage requestMessage) {
        LOGGER.info("Sending request message to admin requests queue");

        this.adminRequestsJmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(requestMessage.getRequest());
                objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
                objectMessage.setJMSType(requestMessage.getMessageType().toString());
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        requestMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        requestMessage.getDeviceIdentification());
                return objectMessage;
            }

        });
    }
}