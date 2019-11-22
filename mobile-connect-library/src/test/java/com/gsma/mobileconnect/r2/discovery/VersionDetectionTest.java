package com.gsma.mobileconnect.r2.discovery;

import com.gsma.mobileconnect.r2.constants.Scope;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.exceptions.InvalidScopeException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class VersionDetectionTest {
    private static final String UNSUPPORTED = "mc_si_r2_v1.0";
    private static final String INCORRECT = "incorrect_version";
    private static final String EMPTY = "";
    private static final List<String> LIST_V1_1_V3_0 = Arrays.asList(Version.MC_V1_1, Version.MC_DI_V3_0);
    private static final List<String> LIST_V1_1 = Arrays.asList(Version.MC_V1_1);
    private static final List<String> LIST_V3_0 = Arrays.asList(Version.MC_DI_V3_0);

    @DataProvider(name = "getCurrentVersionTest")
    public Object[][] getCurrentVersionTest() {
        return new Object[][] {
                // version from config is supported
                {Version.MC_V1_1, null, LIST_V1_1, Version.MC_V1_1},
                {Version.MC_DI_V3_0, null, LIST_V3_0, Version.MC_DI_V3_0},
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
                {EMPTY, null, LIST_V1_1_V3_0}
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
