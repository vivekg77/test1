/*
 * Copyright 2021 Metro Bank. All rights reserved.
 */
package com.metrobank.payments.sepa.inbound.bl.connector.listener;

import com.metrobank.payments.sepa.inbound.bl.connector.producer.InboundBLConnectorProducer;
import com.metrobank.payments.sepa.inbound.bl.connector.utils.ApplicationProperties;
import com.metrobank.payments.sepa.inbound.bl.connector.utils.InboundBlConnectorConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

/**
 * A component responsible to listen from the JMS queue and read the messages. The messages are then
 * converted to RawData Avro and produced on to the raw data Kafka Topic
 */
@Log4j2
@EnableJms
@Component
public class InboundConnectorListener {

  private InboundBLConnectorProducer inboundBLConnectorProducer;

  private final ApplicationProperties applicationProperties;

  @Value("${spring.application.name}")
  private String appProp;

  /**
   * A parameterised constructor to construct an instance of InboundConnectorListener
   *
   * @param inboundBLConnectorProducer An instance of InboundBLConnectorProducer class
   * @param applicationProperties ApplicationProperties object to retrieve the raw topic and the
   *     headers based on the mode.
   */
  public InboundConnectorListener(
      InboundBLConnectorProducer inboundBLConnectorProducer,
      ApplicationProperties applicationProperties) {
    this.inboundBLConnectorProducer = inboundBLConnectorProducer;
    this.applicationProperties = applicationProperties;
  }

  /**
   * A method to listen on a JMS queue and send the messages to the RawData topic
   *
   * @param jmsPaymentDTO - the message received
   * @throws JMSException in case issues with JMS listener
   */
  @JmsListener(destination = "#{applicationProperties.getQueueDestination()}")
  public void receiveMessage(javax.jms.Message jmsPaymentDTO) throws JMSException {
    String xmlPaymentDTO = jmsPaymentDTO.getBody(String.class);
    log.info("QueueName = '{}'", applicationProperties.getQueueDestination());
    log.info("ModeName = '{}'", applicationProperties.getMode());
    log.info("Inbound Request = '{}'", xmlPaymentDTO);
    log.info("application Name = '{}'", appProp);
    // Call the Service to send the Pacs008 XML message to raw topic in form of RawData Avro
    boolean result = this.inboundBLConnectorProducer.service(xmlPaymentDTO);
    if (!result) {
      throw new JMSException(InboundBlConnectorConstants.RAW_TOPIC_SEND_ERROR);
    }
  }
}
