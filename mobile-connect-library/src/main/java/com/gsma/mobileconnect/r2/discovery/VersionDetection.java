package com.gsma.mobileconnect.r2.discovery;

import com.gsma.mobileconnect.r2.constants.Scope;
import com.gsma.mobileconnect.r2.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class VersionDetection {
//    private List<String> supportedVersions = new ArrayList<>();
//    private List<Scope> currentScopes = new ArrayList<>();

    public static boolean isVersionSupported(String version) {
        return version.equals(Version.MC_V1_1) || version.equals(Version.MC_V1_2) || version.equals(Version.MC_V1_2)
                || version.equals(Version.MC_DI_R2_V2_3);
    }

    public String getCurrentVersion(String version, String scope, ProviderMetadata providerMetadata) {
        if (version != null && isVersionSupported(version)) {
            return version;
        } else {
            List<String> supportedVersions = getSupportedVersions(providerMetadata);
            List<String> currentScopes = StringUtils.convertToListBySpase(scope);
            if (supportedVersions.contains(Version.MC_DI_R2_V2_3) & containsScopesV2_3(currentScopes)) {
                return Version.MC_DI_R2_V2_3;
            } else if (supportedVersions.contains(Version.MC_V2_0) & containsScopesV2_0(currentScopes)) {
                return Version.MC_V2_0;
            } else if (supportedVersions.contains(Version.MC_V1_1) & containsOpenidScope(currentScopes)) {
                return Version.MC_V1_1;
            } else {
//                throw new Exception();
                return null;
            }
        }

    }

    private boolean containsOpenidScope(List<String> currentScopes) {
        return currentScopes.contains(Scope.OPENID);
    }

    private boolean containsScopesV2_0(List<String> currentScopes) {
        return containsOpenidScope(currentScopes) & (currentScopes.contains(Scope.AUTHN) || currentScopes.contains(Scope.AUTHZ) ||
                currentScopes.contains(Scope.IDENTITY_PHONE) || currentScopes.contains(Scope.IDENTITY_NATIONALID) ||
                currentScopes.contains(Scope.IDENTITY_SIGNUP) || currentScopes.contains(Scope.IDENTITY_SIGNUPPLUS));
    }

    private boolean containsScopesV2_3(List<String> currentScopes) {
        return containsOpenidScope(currentScopes) & (containsScopesV2_0(currentScopes) || currentScopes.contains(Scope.KYC_HASHED)
                || currentScopes.contains(Scope.KYC_PLAIN));
    }

    public static List<String> getSupportedVersions(ProviderMetadata providerMetadata) {
        List<String> supportedVersions = new ArrayList<>();
        if (providerMetadata == null) {
            supportedVersions.add(Version.MC_V1_1);
        } else {
            supportedVersions = providerMetadata.getMCVersion();
        }
        return supportedVersions;
    }
}
