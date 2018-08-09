package com.gsma.mobileconnect.r2.demo.utils;

import java.io.File;

public class Constants {
        public static String ConfigFilePath = System.getProperty("user.dir").replace("target", "") + File.separator + "mobile-connect-demo" + File.separator + "src" + File.separator + "main" + File.separator +
                "resources" + File.separator + "config" + File.separator + "OperatorData.json";
        public static String Version1 = "mc_v1.1";
        public static String Version2 = "mc_v1.2";
        public static String ContextBindingMsg = "demo";
        public static int ResponseOk = 200;
        public static String InvalidArgument = "invalid_argument";
    }