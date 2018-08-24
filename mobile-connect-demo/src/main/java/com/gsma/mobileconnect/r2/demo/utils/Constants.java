package com.gsma.mobileconnect.r2.demo.utils;

import java.io.File;

public class Constants {
        public static final String ConfigFilePath = System.getProperty("user.dir").replace("target", "")
                + File.separator + "mobile-connect-demo" + File.separator + "src" + File.separator + "main" + File.separator +
                "resources" + File.separator + "config" + File.separator + "OperatorData.json";
        public static final String Version1_1 = "mc_v1.1";
        public static final String Version2_0 = "mc_v2.0";
        public static final String ContextBindingMsg = "demo context";
        public static final String BindingMsg = "demo binding";
    }