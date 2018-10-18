GSMA MobileConnect Java SDK
==============================================================================================================

Mobile Connect is a mobile identity service based on the OpenID Connect & OAuth2 where end users can authenticate themselves using their mobile phone via Mobile Connect. This allows them access to websites and applications without the need to remember passwords and usernames. It’s safe, secure and no personal information is shared without their permission.

## Minimum Requirements

MobileConnect supports Java 6 and above, and is dependent upon Jackson Databind 2.6.3 and Apache HTTP Client 4.5.2.  It uses the SLF-4J logging framework and is dependent upon SLF-4J 1.7.21.

## Getting Started

You must have first registered an account on the [MobileConnect Developer Site](https://developer.mobileconnect.io) and created an application to get your sandbox credentials.

## Using The SDK

Build the SDK using [Maven](https://maven.apache.org/) repository.

```posh
cd r2-java-sdk
cd mobile-connect-sdk
mvn clean install
```

Import the generated mobile-connect-sdk-2.4.7.jar into your java project.

To enable logging import an appropriate binding to your project, see [SLF4J](http://www.slf4j.org/) for more information.

You will need to specify your credentials via an instance of MobileConnectConfig.

```java
MobileConnectConfig config = new MobileConnectConfig.Builder()
    .withClientId("your id")
    .withClientSecret("your secret")
    .withDiscoveryUrl("your discovery URL")
    .withRedirectUrl("your application's redirect URL")
    .withXRedirect("your X-Redirect header")
    .build();
```

Most applications will choose to use either the `MobileConnectInterface` or `MobileConnectWebInterface`.  Instances of these can easily be obtained via the `MobileConnect` class.

```java
MobileConnectInterface mobileConnectInterface = MobileConnect.buildInterface(config);
```

or

```java
MobileConnectWebInterface mobileConnectWebInterface = MobileConnect.buildWebInterface(config);
```

You may opt to carry out deeper configuration of the interfaces via the builder methods available on the `MobileConnect` class, and for the other classes that it accepts.

Options for an individual request can be specified via a `MobileConnectRequestOptions` instance:

```java
MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
    .withDiscoveryOptions(new DiscoveryOptions.Builder()
        .withMsisdn("+447700900999")
        .withClientIp("192.168.0.1"))
    .withAuthenticationOptions(new AuthenticationOptions.Builder()
        .withLoginHint("my hint")));
```

The main entry points to start authorization and obtain a token are the methods that the interface provides, each of which return a `MobileConnectStatus` instance:

```java
MobileConnectStatus status;

status = mobileConnectWebInterface.attemptDiscovery(msisdn, mcc, mnc, true, options);
...
status = mobileConnectWebInterface.handleUrlRedirect(requestUri, discoveryResponse, expectedState, expectedNonce);
...
status = mobileConnectInterface.startAuthentication(discoveryResponse, subscriberId, null, null, options);
```

By querying the `status.getResponseType()` and other methods on the status object, you can understand whether the request was successful and what step you must take next. 

Please reference the [demo application](../mobile-connect-demo/) for a complete example of how to use the SDK.

## Support

If you encounter any issues which are not resolved by consulting the resources below then [send us a message](https://developer.mobileconnect.io/content/contact-us)

## Resources

- [MobileConnect Discovery API Information](https://developer.mobileconnect.io/discovery-api)
- [MobileConnect Authentication API Information](https://developer.mobileconnect.io/mobile-connect-api)
- [MobileConnect Authentication API (v2.0) Information](https://developer.mobileconnect.io/mobile-connect-profile-v2-0)
