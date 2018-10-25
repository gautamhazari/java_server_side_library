package com.gsma.mobileconnect.r2.demo.utils;

public class Constants {
        public static final String ConfigFilePath = Thread.currentThread().getContextClassLoader().getResource("OperatorData.json").toString();
        public static final String WDConfigFilePath = Thread.currentThread().getContextClassLoader().getResource("WithoutDiscoveryData.json").toString();
        public static final String SectorIdentifierPath = Thread.currentThread().getContextClassLoader().getResource("sector_identifier_uri.json").toString();
        public static final String Version1_1 = "mc_v1.1";
        public static final String Version2_0 = "mc_v2.0";
        public static final String Version2_3 = "mc_di_r2_v2.3";
        public static final String ContextBindingMsg = "demo context";
        public static final String BindingMsg = "demo binding";
        public static final String SERVER_SIDE_VERSION = "Java-3.2.0";
    }