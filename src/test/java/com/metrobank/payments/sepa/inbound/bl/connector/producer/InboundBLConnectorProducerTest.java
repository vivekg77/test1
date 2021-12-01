/*
 * Copyright 2021 Metro Bank. All rights reserved.
 */
package com.metrobank.payments.sepa.inbound.bl.connector.producer;

import com.metrobank.payments.sepa.inbound.bl.connector.config.InboundBlConnectorTestConfig;
import com.metrobank.payments.sepa.inbound.bl.connector.data.TestDataHelper;
import com.metrobank.payments.sepa.inbound.bl.connector.utils.ApplicationProperties;
import com.metrobank.payments.sepa.internal.RawData;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@SpringBootTest(classes = InboundBlConnectorTestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class InboundBLConnectorProducerTest {



    @Mock
    private KafkaTemplate<String, RawData> rawKafkaTemplate;

    @Mock
    private ListenableFuture<SendResult<String, RawData>> responseFuture;

    @Mock
    private CompletableFuture<SendResult<String, RawData>> responseCompleted;

    @Autowired
    private ApplicationProperties applicationProperties;

    private InboundBLConnectorProducer inboundBLConnectorProducer;

    @Test
    @DisplayName("Test Producer Success")
    void testProducerSuccessCase() throws IOException {

        String pacs008Xml;
        boolean result;

        Given:{
            inboundBLConnectorProducer = new InboundBLConnectorProducer(rawKafkaTemplate, applicationProperties);
            pacs008Xml = TestDataHelper.getDummyPacs008();

            Mockito.when(rawKafkaTemplate.send((ProducerRecord<String, RawData>) Mockito.any())).thenReturn(responseFuture);
            Mockito.when(responseFuture.completable()).thenReturn(responseCompleted);
            Mockito.when(responseCompleted.isCancelled()).thenReturn(false);
            Mockito.when(responseCompleted.isCompletedExceptionally()).thenReturn(false);

            ReflectionTestUtils.setField(inboundBLConnectorProducer, "rawPayloadTopic", "testRawTopic");

        }
        When:{

            result = inboundBLConnectorProducer.service(pacs008Xml);
        }
        Then:{
            Assertions.assertTrue(result);

        }


    }

    @Test
    @DisplayName("Test Producer Failure")
    void testProducerFailureCase() throws IOException {

        String pacs008Xml;
        boolean result;

        Given:{
            inboundBLConnectorProducer = new InboundBLConnectorProducer(rawKafkaTemplate, applicationProperties);
            pacs008Xml = TestDataHelper.getDummyPacs008();

            Mockito.when(rawKafkaTemplate.send((ProducerRecord<String, RawData>) Mockito.any())).thenReturn(responseFuture);
            Mockito.when(responseFuture.completable()).thenReturn(responseCompleted);
            Mockito.when(responseCompleted.isCancelled()).thenReturn(true);
            Mockito.when(responseCompleted.isCompletedExceptionally()).thenReturn(false);


            ReflectionTestUtils.setField(inboundBLConnectorProducer, "rawPayloadTopic", "testRawTopic");

        }
        When:{

            result = inboundBLConnectorProducer.service(pacs008Xml);
        }
        Then:{
            Assertions.assertFalse(result);

        }


    }

}