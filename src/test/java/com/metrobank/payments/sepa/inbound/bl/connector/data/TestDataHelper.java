/*
 * Copyright 2021 Metro Bank. All rights reserved.
 */
/*
 * Copyright 2021 Metro Bank. All rights reserved.
 */
package com.metrobank.payments.sepa.inbound.bl.connector.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A class that generated dummy data for integration test cases
 */
public class TestDataHelper {

    /**
     * A method to obtain a dummy Pacs008 XML String
     * @return Pacs008 XML String
     * @throws IOException - in case reading from example file fails
     */
    public static String getDummyPacs008() throws IOException {
        String path = "src/test/resources/xml/pacs.008.001.02.xml";
        return Files.readString(Paths.get(path));
    }
}
