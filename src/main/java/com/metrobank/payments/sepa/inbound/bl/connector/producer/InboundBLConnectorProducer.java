/*
 * Copyright 2021 Metro Bank. All rights reserved.
 */
package com.metrobank.payments.sepa.inbound.bl.connector.producer;

import com.metrobank.commons.utils.UniqueIdGenerator;
import com.metrobank.header.util.HeaderEnums;
import com.metrobank.header.util.HeaderProvider;
import com.metrobank.payments.sepa.inbound.bl.connector.utils.ApplicationProperties;
import com.metrobank.payments.sepa.internal.RawData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A component that processes the incoming message, 1. Generates a Base 64 of the XML message 2.
 * Generate the rawData Avro Message 3. generate the UUID 4. Send the rawData avro to the Kafka raw
 * topic, from where the topology shall pick up for further processing
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class InboundBLConnectorProducer {

  String rawPayloadTopic;

  private final KafkaTemplate<String, RawData> rawKafkaTemplate;
  private final ApplicationProperties applicationProperties;

  /**
   * A method that shall process the incoming message 1. Generate UUId and set MDC 2. Generate a
   * Base 64 of the XML message 3. Generate the rawData Avro Message 4. set the Message Header
   * values 5. Send the rawData avro to the Kafka raw topic, from where the topology shall pick up
   * for further processing
   *
   * @param pacs008XmlString the XML received from BottomLine
   */
  public boolean service(@NonNull String pacs008XmlString) {

    // Step 1 : Generate a UUID and set MDC
    String recordKey = String.valueOf(UniqueIdGenerator.generateId());
    MDC.put("uuid", recordKey);

    // Step 2 : Generate Base 64 of the incoming xml
    String base64Value = Base64.getEncoder().encodeToString(pacs008XmlString.getBytes());

    // Step 3: Generate the Raw Data Avro
    RawData rawDataPayload = RawData.newBuilder().setRawXMLAvro(base64Value).build();

    // Step 4: set the Message Header values
    Headers rawMessageHeaders =
        HeaderProvider.getInstance(recordKey, applicationProperties.getMonitoringHeader())
            .flowDirection(HeaderEnums.FlowDirection.INBOUND)
            .paymentStatus(applicationProperties.getPaymentStatusHeader())
            .messageType(HeaderEnums.MessageType.PACS008_V001_02)
            .originator(HeaderEnums.Originator.BOTTOMLINE)
            .scheme(HeaderEnums.Scheme.SEPA)
            .provide();

    // Step 5: Send the RawData avro to the raw Kafka topic
    return sendRawPayLoad(rawDataPayload, recordKey, Arrays.asList(rawMessageHeaders.toArray()));
  }

  /**
   * A utility method to send the data to the raw kafka topic
   *
   * @param rawData - raw data avro object
   * @param recordKey - the kafka message key
   * @param headers - the kafka headers for the message
   */
  private boolean sendRawPayLoad(RawData rawData, String recordKey, List<Header> headers) {

    boolean sentRawRecord = false;
    rawPayloadTopic = applicationProperties.getRawTopic();
    ProducerRecord<String, RawData> rawOutputRecord =
        new ProducerRecord<>(
            applicationProperties.getRawTopic(), null, recordKey, rawData, headers);
    log.info(
        "sending the raw message='{}' to topic='{}' with headers= '{}' and recordkey='{}'",
        rawData,
        applicationProperties.getRawTopic(),
        headers,
        recordKey);
    CompletableFuture<SendResult<String, RawData>> result =
        rawKafkaTemplate.send(rawOutputRecord).completable();
    if (result.isCancelled() || result.isCompletedExceptionally()) {
      log.info(
          "Could not sent raw record : topic name ='{}', payload ='{}', recordkey='{}', headers='{}'",
          rawPayloadTopic,
          rawData,
          recordKey,
          headers);
    } else {
      log.info(
          "Successfully sent raw record : topic name ='{}', payload ='{}', recordkey='{}', headers='{}'",
          rawPayloadTopic,
          rawData,
          recordKey,
          headers);
      sentRawRecord = true;
    }
    return sentRawRecord;
  }
}
