/*
 * Copyright 2021 Metro Bank. All rights reserved.
 */
package com.metrobank.payments.sepa.inbound.bl.connector;

import com.metrobank.payments.sepa.inbound.bl.connector.utils.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class PaymentsSepaBlConnectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentsSepaBlConnectorApplication.class, args);
	}

}
