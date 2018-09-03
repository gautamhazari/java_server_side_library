package com.gsma.mobileconnect.r2.demo.utils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.gsma.mobileconnect.r2.demo.objects.OperatorParameters;

import java.io.FileReader;
import java.io.IOException;

public class ReadAndParseFiles {

    public static OperatorParameters ReadFile(String filePath)
    {
        OperatorParameters operatorParameters = null;
        try {
            JsonReader reader = new JsonReader(new FileReader(filePath));
            operatorParameters = new Gson().fromJson(reader, OperatorParameters.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return operatorParameters;
    }
}
