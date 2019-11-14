package com.gsma.mobileconnect.r2.discovery;

import com.gsma.mobileconnect.r2.model.constants.Scope;
import com.gsma.mobileconnect.r2.model.constants.Scopes;
import com.gsma.mobileconnect.r2.model.exceptions.InvalidScopeException;
import com.gsma.mobileconnect.r2.service.discovery.ProviderMetadata;
import com.gsma.mobileconnect.r2.service.discovery.Version;
import com.gsma.mobileconnect.r2.service.discovery.VersionDetection;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class VersionDetectionTest {
    private static final String UNSUPPORTED = "mc_si_r2_v1.0";
    private static final String INCORRECT = "incorrect_version";
    private static final String EMPTY = "";
    private static final List<String> LIST_V1_1_V2_0_V2_3 = Arrays.asList(Version.MC_V1_1, Version.MC_V2_0,
            Version.MC_DI_R2_V2_3);
    private static final List<String> LIST_V2_0_V2_3 = Arrays.asList(Version.MC_V2_0,
            Version.MC_DI_R2_V2_3);
    private static final List<String> LIST_V1_1_V1_0_V2_3 = Arrays.asList(Version.MC_V1_1, UNSUPPORTED,
            Version.MC_DI_R2_V2_3);
    private static final List<String> LIST_V1_1_V2_0 = Arrays.asList(Version.MC_V1_1, Version.MC_V2_0);
    private static final List<String> LIST_V1_1 = Arrays.asList(Version.MC_V1_1);
    private static final List<String> LIST_V2_0 = Arrays.asList(Version.MC_V2_0);
    private static final List<String> LIST_V2_3 = Arrays.asList(Version.MC_DI_R2_V2_3);

    @DataProvider(name = "getCurrentVersionTest")
    public Object[][] getCurrentVersionTest() {
        return new Object[][] {
                // version from config is supported
                {Version.MC_V1_1, null, LIST_V1_1, Version.MC_V1_1},
                {Version.MC_V2_0, null, LIST_V2_0, Version.MC_V2_0},
                {Version.MC_DI_R2_V2_3, null, LIST_V2_3, Version.MC_DI_R2_V2_3},

                // version is supported but doesn't match with mc_version
                {Version.MC_V1_1, null, LIST_V2_0, Version.MC_V1_1},
                {Version.MC_V2_0, null, LIST_V1_1, Version.MC_V2_0},
                {Version.MC_DI_R2_V2_3, null, LIST_V2_0, Version.MC_DI_R2_V2_3},

                // version isn't supported (or incorrect)
                {UNSUPPORTED, Scope.OPENID, LIST_V1_1_V2_0, Version.MC_V1_1},
                {INCORRECT, Scope.OPENID, LIST_V1_1_V2_0, Version.MC_V1_1},
                {EMPTY, Scope.OPENID, LIST_V1_1_V2_0, Version.MC_V1_1},
                {null, Scope.OPENID, LIST_V1_1_V2_0, Version.MC_V1_1},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_AUTHENTICATION, LIST_V1_1_V2_0, Version.MC_V2_0},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_AUTHORIZATION, LIST_V1_1_V2_0, Version.MC_V2_0},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_SIGNUP_PLUS, LIST_V1_1_V2_0, Version.MC_V2_0},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_SIGNUP, LIST_V1_1_V2_0, Version.MC_V2_0},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_PHONE, LIST_V1_1_V2_0, Version.MC_V2_0},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_NATIONALID, LIST_V1_1_V2_0, Version.MC_V2_0},

                {UNSUPPORTED, Scope.OPENID, LIST_V1_1_V2_0_V2_3, Version.MC_V1_1},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_AUTHENTICATION, LIST_V1_1_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_AUTHORIZATION, LIST_V1_1_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_SIGNUP_PLUS, LIST_V1_1_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_SIGNUP, LIST_V1_1_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_PHONE, LIST_V1_1_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_NATIONALID, LIST_V1_1_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_KYC_HASHED, LIST_V1_1_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_KYC_PLAIN, LIST_V1_1_V2_0_V2_3, Version.MC_DI_R2_V2_3},

                {UNSUPPORTED, Scopes.MOBILE_CONNECT_AUTHENTICATION, LIST_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_AUTHORIZATION, LIST_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_SIGNUP_PLUS, LIST_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_SIGNUP, LIST_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_PHONE, LIST_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_NATIONALID, LIST_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_KYC_HASHED, LIST_V2_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_KYC_PLAIN, LIST_V2_0_V2_3, Version.MC_DI_R2_V2_3},

                {UNSUPPORTED, Scope.OPENID, LIST_V1_1_V1_0_V2_3, Version.MC_V1_1},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_AUTHENTICATION, LIST_V1_1_V1_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_AUTHORIZATION, LIST_V1_1_V1_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_SIGNUP_PLUS, LIST_V1_1_V1_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_SIGNUP, LIST_V1_1_V1_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_PHONE, LIST_V1_1_V1_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_IDENTITY_NATIONALID, LIST_V1_1_V1_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_KYC_HASHED, LIST_V1_1_V1_0_V2_3, Version.MC_DI_R2_V2_3},
                {UNSUPPORTED, Scopes.MOBILE_CONNECT_KYC_PLAIN, LIST_V1_1_V1_0_V2_3, Version.MC_DI_R2_V2_3},
        };
    }

    @Test(dataProvider = "getCurrentVersionTest")
    public void getCurrentVersionTest(String version, String scope, List<String> mcVersion, String expectedVersion) throws InvalidScopeException {
        if (mcVersion != null) {
            new ProviderMetadata.Builder().withMCVersion(mcVersion).build();
        }
        String actualVersion = VersionDetection.getCurrentVersion(version, scope,
                new ProviderMetadata.Builder().withMCVersion(mcVersion).build());
        assertEquals(actualVersion, expectedVersion,
                String.format("Expected version: %s,\n" +
                        "actual version: %s,\n" +
                        "version from config: %s,\n" +
                        "scope from config: %s,\n" +
                        "mc_version: %s", expectedVersion, actualVersion, version, scope, mcVersion));
    }

    @DataProvider(name = "getCurrentVersionExcTest")
    public Object[][] getCurrentVersionExc() {
        return new Object[][]{
                {EMPTY, Scopes.MOBILE_CONNECT_KYC_PLAIN, LIST_V1_1_V2_0},
                {EMPTY, Scopes.MOBILE_CONNECT_KYC_HASHED, LIST_V1_1_V2_0},
                {EMPTY, Scope.OPENID, LIST_V2_0_V2_3},
                {EMPTY, null, LIST_V1_1_V1_0_V2_3}
        };
    }

    @Test(dataProvider = "getCurrentVersionExcTest", expectedExceptions = InvalidScopeException.class)
    public void getCurrentVersionExcTest(String version, String scope, List<String> mcVersion) throws InvalidScopeException {
        if (mcVersion != null) {
            new ProviderMetadata.Builder().withMCVersion(mcVersion).build();
        }
        VersionDetection.getCurrentVersion(version, scope, new ProviderMetadata.Builder().withMCVersion(mcVersion).build());
    }
}
