/*
 * Copyright 2021 Metro Bank. All rights reserved.
 */
package com.metrobank.payments.sepa.inbound.bl.connector.utils;

import com.metrobank.payments.sepa.inbound.bl.connector.config.InboundBlConnectorTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = InboundBlConnectorTestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@ExtendWith(MockitoExtension.class)
class ApplicationPropertiesTest {

    @Autowired
    private ApplicationProperties applicationProperties;


    @Test
    @DisplayName("Test Application Properties For Inbound Mode")
    void testApplicationPropertiesForInboundMode() {

        Given:{

            ApplicationProperties.Headers headers = new ApplicationProperties.Headers();
            headers.setMonitoring("SEPA-INBOUND-PAYMENT-RECEIVED");
            headers.setStatus("SEPA-INBOUND-PAYMENT-RECEIVED");

            ApplicationProperties.ApplicationModeProperties applicationModeProps= new ApplicationProperties.ApplicationModeProperties();

            applicationModeProps.setQueue("EAI.SCT_BL_RECPMT.REQ");
            applicationModeProps.setRawTopic("private.payments.sepa.inbound.raw.dev");
            applicationModeProps.setHeaders(headers);

            Map<String, ApplicationProperties.ApplicationModeProperties> applicationModes = new HashMap<>();


            applicationModes.put("inbound", applicationModeProps);
            applicationProperties.setModes(applicationModes);


        }
        When:{
            applicationProperties.getQueueDestination();
            applicationProperties.getRawTopic();
            applicationProperties.getMonitoringHeader();
            applicationProperties.getPaymentStatusHeader();
        }
        Then:{
            assertNotNull(applicationProperties);
            assertNotNull(applicationProperties.getQueueDestination());
            assertNotNull(applicationProperties.getRawTopic());
            assertNotNull(applicationProperties.getMonitoringHeader());
            assertNotNull(applicationProperties.getPaymentStatusHeader());
            assertEquals("EAI.SCT_BL_RECPMT.REQ", applicationProperties.getQueueDestination());
            assertEquals("private.payments.sepa.inbound.raw.dev", applicationProperties.getRawTopic());
            assertEquals("SEPA-INBOUND-PAYMENT-RECEIVED", applicationProperties.getMonitoringHeader());
            assertEquals("SEPA-INBOUND-PAYMENT-RECEIVED", applicationProperties.getPaymentStatusHeader());
            assertEquals("inbound", applicationProperties.getMode().toString());
        }

    }

}