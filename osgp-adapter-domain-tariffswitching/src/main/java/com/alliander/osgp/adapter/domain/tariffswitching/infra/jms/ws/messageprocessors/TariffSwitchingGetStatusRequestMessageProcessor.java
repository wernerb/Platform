package com.alliander.osgp.adapter.domain.tariffswitching.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.tariffswitching.application.services.AdHocManagementService;
import com.alliander.osgp.adapter.domain.tariffswitching.infra.jms.ws.WebServiceRequestMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.DomainType;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing tariff switching get status request messages
 * 
 * @author CGI
 * 
 */
@Component("domainTariffSwitchingGetStatusRequestMessageProcessor")
public class TariffSwitchingGetStatusRequestMessageProcessor extends WebServiceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TariffSwitchingGetStatusRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainTariffSwitchingAdHocManagementService")
    private AdHocManagementService adHocManagementService;

    public TariffSwitchingGetStatusRequestMessageProcessor() {
        super(DeviceFunction.GET_TARIFF_STATUS);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.info("Processing tariff switching get status request message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            return;
        }

        try {
            LOGGER.info("Calling application service function: {}", messageType);

            this.adHocManagementService.getStatus(organisationIdentification, deviceIdentification, correlationUid,
                    DomainType.TARIFF_SWITCHING, messageType);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }
    }
}