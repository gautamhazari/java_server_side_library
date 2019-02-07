GSMA MobileConnect Java Server Side Library
==============================================================================================================

Mobile Connect is a mobile identity service based on the OpenID Connect & OAuth2 where end users can authenticate themselves using their mobile phone via Mobile Connect. This allows them access to websites and applications without the need to remember passwords and usernames. It’s safe, secure and no personal information is shared without their permission.

Note: if you operate in the EU then you should use EU Discovery Service domain in discovery URL: eu.discover.mobileconnect.io

## Quick Start

1. Clone reporitory

2. Open the configuration file [local path]\mobile-connect-demo\src\main\resources\config\OperatorData.json for setup the SDK using discovery and set 11 parameters:
```posh
{
  "clientID": your client Id,
  "clientSecret": your client Secret,
  "clientName": your client Name,
  "discoveryURL": your Discovery endpoint,
  "redirectURL": "<protocol>://<hostname>/server_side_api/discovery_callback",
  "xRedirect": "True",
  "includeRequestIP": "False",
  "apiVersion": api version: "mc_v1.1", "mc_v2.0" or "mc_di_r2_v2.3",
  "scope": scope,
  "acrValues":  acr_values,
  "MaxDiscoveryCacheSize": max cache size
}
```

3. Open the configuration file [local path]\mobile-connect-demo\src\main\resources\config\WithoutDiscoveryData.json for setup the SDK in without discovery mode and set 17 parameters:
```posh
{
  "clientID": your client Id,
  "clientSecret": your client Secret,
  "clientName": your client Name,
  "discoveryURL": your Discovery endpoint,
  "redirectURL": "<protocol>://<hostname>/server_side_api/discovery_callback",
  "xRedirect": "True",
  "includeRequestIP": "True",
  "apiVersion": api version: "mc_v1.1", "mc_v2.0" or "mc_di_r2_v2.3",
  "scope": scope,
  "acrValues": acr_values,
  "MaxDiscoveryCacheSize": max cache size,
  "operatorUrls": {
    "authorizationUrl": authorize endpoint,
    "requestTokenUrl": token endpoint,
    "userInfoUrl": userinfo endpoint,
    "premiumInfoUri": premiuminfo endpoint,
    "providerMetadataUri": provider metadata endpoint
  }
}
```

4. Open sector_identifier_uri.json file and specify the value of sector_identifier_uri with a single JSON array of redirect_uri values.
```posh
["<protocol>://<hostname>/server_side_api/discovery_callback"]
```

5. Download and install any missing dependencies.

6. Build the server side SDK using Maven repository:
```posh
cd java-server-side-sdk
mvn clean package
```

7. Deploy mobile-connect.war

8. Prepare client side application (IOS or Android application) or Demo App for Server Side application.

## Support

If you encounter any issues which are not resolved by consulting the resources below then [send us a message](https://developer.mobileconnect.io/content/contact-us)

## Resources

- [MobileConnect Java Server Side Library](https://developer.mobileconnect.io/content/java-server-side-library)
- [MobileConnect Android Client Side Library](https://developer.mobileconnect.io/content/android-client-side-library)
- [MobileConnect iOS Client Side Library](https://integration.developer.mobileconnect.io/mobile-connect-library-for-ios)

- [MobileConnect Discovery API Information](https://developer.mobileconnect.io/discovery-api)
- [MobileConnect Authentication API Information](https://developer.mobileconnect.io/mobile-connect-api)
- [MobileConnect Authentication API (v2.0) Information](https://developer.mobileconnect.io/mobile-connect-profile-v2-0)


