package com.gsma.mobileconnect.r2.demo.utils;
import com.google.gson.Gson;
import com.gsma.mobileconnect.r2.demo.objects.OperatorParameters;
import org.json.simple.JSONObject;

import java.io.FileReader;
import java.io.IOException;

public class ReadAndParseFiles {

    public static OperatorParameters ReadFile(String filePath)
    {
        OperatorParameters operatorParameters = null;
        try {
            operatorParameters = new Gson().fromJson(new FileReader(filePath), OperatorParameters.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return operatorParameters;
    }
}
