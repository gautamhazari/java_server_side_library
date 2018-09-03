package com.gsma.mobileconnect.r2.demo.utils;

import com.google.gson.Gson;
import com.gsma.mobileconnect.r2.demo.objects.OperatorParameters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadAndParseFiles {

    public static OperatorParameters ReadFile(String filePath)
    {
        OperatorParameters operatorParameters = null;
        try {
            String json = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            operatorParameters = new Gson().fromJson(json, OperatorParameters.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return operatorParameters;
    }
}
