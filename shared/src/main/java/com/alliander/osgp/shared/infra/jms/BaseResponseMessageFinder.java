package com.alliander.osgp.shared.infra.jms;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

/**
 * Base class for retrieving response messages from a queue by correlation UID.
 * 
 * @author CGI
 * 
 */
public abstract class BaseResponseMessageFinder {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseResponseMessageFinder.class);

    private static final String JMS_CORRELATION_ID_START = "JMSCorrelationID='";
    private static final String JMS_CORRELATION_ID_END = "'";

    /**
     * Method for retrieving messages by correlation UID.
     * 
     * @param correlationUid
     *            The correlation UID of the message to find.
     * @return A response message.
     */
    public ResponseMessage findMessage(final String correlationUid) throws OsgpException {
        LOGGER.info("Trying to find message with correlationUID: {}", correlationUid);
        final ObjectMessage om = this.receiveObjectMessage(correlationUid);
        ResponseMessage responseJmsMessage = null;

        if (om != null) {
            LOGGER.info("Message with correlationUID: {} has been found, trying to read message...", correlationUid);
            try {
                responseJmsMessage = (ResponseMessage) om.getObject();
                this.checkResponseMessage(responseJmsMessage);
                LOGGER.info("Returning response for message with correlationUID: {}", correlationUid);
            } catch (final JMSException e) {
                LOGGER.error("Error while finding message", e);
                responseJmsMessage = this.createEmptyMessage(correlationUid);
            }
        } else {
            LOGGER.info("No message with correlationUID: {} has been found, NOT_FOUND will be returned.", correlationUid);
            responseJmsMessage = this.createEmptyMessage(correlationUid);
        }

        return responseJmsMessage;
    }

    /**
     * Receive an object message from the JMS Template. This method has to be implemented for specific queues by classes
     * that extend this abstract class.
     * 
     * @param correlationUid
     *            The correlation UID of the message to receive.
     * @return An object message or null.
     */
    protected abstract ObjectMessage receiveObjectMessage(final String correlationUid);

    /**
     * Get a JMSCorrelationID for a correlation UID.
     * 
     * @param correlationUid
     *            The correlation UID of the message.
     * @return A JMSCorrelationID.
     */
    protected String getJmsCorrelationId(final String correlationUid) {
        return JMS_CORRELATION_ID_START + correlationUid + JMS_CORRELATION_ID_END;
    }

    /**
     * Check if the response message contains an error message.
     * 
     * @param responseMessage
     *            The response message to check.
     * @throws ResponseMessageException
     *             In case an error message is present in the response message.
     */
    protected void checkResponseMessage(final ResponseMessage responseMessage) throws OsgpException {
        if (responseMessage.getResult().equals(ResponseMessageResultType.NOT_OK)) {
            if (responseMessage.getDataObject() instanceof OsgpException) {
                throw (OsgpException) responseMessage.getDataObject();
            } else {
                // Should not get here, since all exceptions in the response messages should be osgp exceptions
                Exception ex = null;
                if (responseMessage.getDataObject() instanceof Exception) {
                    ex = (Exception) responseMessage.getDataObject();
                    LOGGER.error("Unexpected exception: ", ex);
                }
                throw new TechnicalException(ComponentType.UNKNOWN, "Unexpected exception while retrieving response message", ex);
            }
        }
    }

    /**
     * Method for creating an empty not found response message.
     * 
     * @param correlationUid
     *            The correlation UID of the message.
     * @return An empty not found message.
     */
    protected ResponseMessage createEmptyMessage(final String correlationUid) {
        return new ResponseMessage(correlationUid, null, null, ResponseMessageResultType.NOT_FOUND, "", null);
    }

}