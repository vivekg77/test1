/*
 * Copyright 2021 Metro Bank. All rights reserved.
 */

package com.metrobank.payments.sepa.inbound.bl.connector.utils;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * ApplicationProperties class maintains the queue to read from, topic to write to and monitoring,
 * status and datelineage header to be set for the following modes: Inbound, Return, Recall,
 * Positive Recall, Negative Recall and Reject This class reads the 'service' tag in the
 * application.yaml and set the various properties accordingly
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "service")
public class ApplicationProperties {

  @Getter
  public enum Mode {
    INBOUND("inbound"),
    RETURN("return"),
    RECALL("recall"),
    POSTIVERECALL("positiverecall"),
    NEGATIVERECALL("negativerecall"),
    REJECT("reject");

    private final String value;

    Mode(String value) {

      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }
  }

  @NotNull private Mode mode;

  @NotNull private Map<String, ApplicationModeProperties> modes;

  @Data
  @NotNull
  public static class Headers {

    @NotNull private String status;
    @NotNull private String monitoring;
  }

  @Data
  public static class ApplicationModeProperties {

    @NotNull private String queue;
    @NotNull private String rawTopic;
    @NotNull private Headers headers;
  }

  /**
   * A method to return the Queue Name to read from based on the mode provided in the
   * application.yaml
   */
  public String getQueueDestination() {

    return modes.get(mode.getValue()).getQueue();
  }

  /**
   * A method to return the Raw Topic Name to read from based on the mode provided in the
   * application.yaml
   */
  public String getRawTopic() {

    return modes.get(mode.getValue()).getRawTopic();
  }

  /**
   * A method to return the Monitoring Header value to set based on the mode provided in the
   * application.yaml
   */
  public String getMonitoringHeader() {

    return modes.get(mode.getValue()).getHeaders().getMonitoring();
  }

  /**
   * A method to return the Payment Status Header Value to set based on the mode provided in the
   * application.yaml
   */
  public String getPaymentStatusHeader() {

    return modes.get(mode.getValue()).getHeaders().getStatus();
  }
}
