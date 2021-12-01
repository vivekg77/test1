/*
 * Copyright 2021 Metro Bank. All rights reserved.
 */
package com.metrobank.payments.sepa.inbound.bl.connector.listener;

import com.metrobank.payments.sepa.inbound.bl.connector.producer.InboundBLConnectorProducer;
import com.metrobank.payments.sepa.inbound.bl.connector.utils.ApplicationProperties;
import com.metrobank.payments.sepa.inbound.bl.connector.utils.InboundBlConnectorConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InboundConnectorListenerTest {

    @Mock
    private ObjectMessage JmsPaymentDTO ;

    @Mock
    private InboundBLConnectorProducer inboundBLConnectorProducer;

    @Mock
    private ApplicationProperties applicationProperties;

    @InjectMocks
    private InboundConnectorListener inboundConnectorListener;

    /**
     * A test method to test the success case of the JMS receiveMessage method in Listener class
     * @throws JMSException in case unable to listen on the Mock JMS Queue
     */
    @Test
   void testSuccessCase() throws JMSException {

       Given:{
            Mockito.when(JmsPaymentDTO.getBody(String.class)).thenReturn("SOME-PACS008-XML");
            Mockito.when(inboundBLConnectorProducer.service(Mockito.any(String.class))).thenReturn(true);
       }
       When:{
            inboundConnectorListener.receiveMessage(JmsPaymentDTO);
       }
       Then:{
            assertTrue(true);
       }
   }

    /**
     * A test method to test the negative case of the JMS receiveMessage method in Listener class
     * @throws JMSException in case unable to listen on the Mock JMS Queue
     */
   @Test
   void testFailureCase() throws JMSException {

       JMSException jmsException;

       Given:{
           Mockito.when(JmsPaymentDTO.getBody(String.class)).thenReturn("SOME-PACS008-XML");
           Mockito.when(inboundBLConnectorProducer.service(Mockito.any(String.class))).thenReturn(false);
       }
       When:{
           jmsException = assertThrows(JMSException.class,
                   () -> inboundConnectorListener.receiveMessage(JmsPaymentDTO),
                   InboundBlConnectorConstants.RAW_TOPIC_SEND_ERROR);
       }
       Then:{
           assertTrue(jmsException.getMessage().contains(InboundBlConnectorConstants.RAW_TOPIC_SEND_ERROR));
       }

   }

}