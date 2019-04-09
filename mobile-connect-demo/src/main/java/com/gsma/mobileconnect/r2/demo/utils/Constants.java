package com.gsma.mobileconnect.r2.demo.utils;

public class Constants {
        public static final String CONFIG_FILE_PATH = Thread.currentThread().getContextClassLoader().getResource("OperatorData.json").toString();
        public static final String WD_CONFIG_FILE_PATH = Thread.currentThread().getContextClassLoader().getResource("WithoutDiscoveryData.json").toString();
        public static final String SECTOR_IDENTIFIER_PATH = Thread.currentThread().getContextClassLoader().getResource("sector_identifier_uri.json").toString();
        public static final String VERSION_1_1 = "mc_v1.1";
        public static final String VERSION_2_0 = "mc_v2.0";
        public static final String VERSION_2_3 = "mc_di_r2_v2.3";
        public static final String CONTEXT_BINDING_MSG = "demo context";
        public static final String BINDING_MSG = "demo binding";
        public static final String SERVER_SIDE_VERSION = "Java-3.4.0";
        public static final String SUCCESS_HTML_PAGE = "success.html";
        public static final String FAIL_HTML_PAGE = "fail.html";

        private Constants(){}
    }