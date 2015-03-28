package com.alliander.osgp.adapter.domain.publiclighting.infra.jms.core.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.publiclighting.application.services.DeviceMonitoringService;
import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.core.OsgpCoreResponseMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.PowerUsageHistoryResponseMessageDataContainer;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

/**
 * Class for processing public lighting power usage history response messages
 * 
 * @author CGI
 * 
 */
@Component("domainPublicLightingPowerUsageHistoryResponseMessageProcessor")
public class PublicLightingPowerUsageHistoryResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublicLightingPowerUsageHistoryResponseMessageProcessor.class);

    @Autowired
    @Qualifier("domainPublicLightingDeviceMonitoringService")
    private DeviceMonitoringService deviceMonitoringService;

    protected PublicLightingPowerUsageHistoryResponseMessageProcessor() {
        super(DeviceFunction.GET_POWER_USAGE_HISTORY);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing public lighting power usage history response message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        ResponseMessage responseMessage = null;
        ResponseMessageResultType responseMessageResultType = null;
        String description = null;
        Object dataObject = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

            responseMessage = (ResponseMessage) message.getObject();
            responseMessageResultType = responseMessage.getResult();
            description = responseMessage.getDescription();
            dataObject = responseMessage.getDataObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("responseMessageResultType: {}", responseMessageResultType);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("description: {}", description);
            return;
        }

        try {
            LOGGER.info("Calling application service function to handle response: {}", messageType);

            final PowerUsageHistoryResponseMessageDataContainer powerUsageHistoryResponseMessageDataContainer = (PowerUsageHistoryResponseMessageDataContainer) dataObject;

            this.deviceMonitoringService.handleGetPowerUsageHistoryResponse(
                    powerUsageHistoryResponseMessageDataContainer, organisationIdentification, deviceIdentification,
                    correlationUid, messageType, responseMessageResultType, description);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }
    }
}