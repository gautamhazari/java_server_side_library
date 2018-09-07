package com.gsma.mobileconnect.r2.demo.utils;

import com.google.gson.Gson;
import com.gsma.mobileconnect.r2.demo.objects.OperatorParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadAndParseFiles {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadAndParseFiles.class);

    public static OperatorParameters ReadFile(String filePath)
    {
        OperatorParameters operatorParameters = null;
        try {
            String json = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            operatorParameters = new Gson().fromJson(json, OperatorParameters.class);
        } catch (Exception e) {
            LOGGER.warn(String.format("Failed to read file %s", filePath));
        }
        return operatorParameters;
    }
}
