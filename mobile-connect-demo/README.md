GSMA MobileConnect Java SDK Demo
==============================================================================================================

- This demo provides a complete code example for completing the authorization flow of MobileConnect.
- Demo code is only for example purposes

## Minimum Requirements

MobileConnect supports Java 6 and above, and is dependent upon Jackson Databind 2.6.3 and Apache HTTP Client 4.5.2.  It uses the SLF-4J logging framework and is dependent upon SLF-4J 1.7.21.

## Getting Started

You must have first registered an account on the [MobileConnect Developer Site](https://developer.mobileconnect.io) and created an application to get your sandbox credentials.
You should set your application redirectUrl to http://localhost:8080/mobileconnect.html

## Running the Application
Ensure you have build the [mobile-connect-sdk](../mobile-connect-sdk) jar using [Maven](https://maven.apache.org/) repository.

```posh
cd java-sdk-v2
mvn clean install
```
Run the MobileConnect demo.

```posh
cd java-sdk-v2
mvn spring-boot:run -pl mobile-connect-demo
```

Then navigate to http://localhost:8080 in your browser.

`DemoAppConfiguration` shows how to configure and start the MobileConnect interface.  `DemoAppController` shows the interaction with the MobileConnect interface.

## Using Application

The Application consists of three parts:
1. Demo application (default).
2. Without discovery.
3. Indian demo application.

## Using the Demo Application
Configure the MobileConnectConfig instance with your credentials from the [MobileConnect Developer Site](https://developer.mobileconnect.io) portal. 
Required parameters:

Click "Request parameters" button to see your Discovery configuration.
Choose API version which you want to use.
Default values are in `/local-path/mobile-connect-demo/src/main/resources/public/data/defaultData.json` file.

```posh
{
    "msisdn": "<provide msisdn here>",
    "clientID": "<provide client id here>",
    "clientSecret": "<provide client secret here>",
    "discoveryURL": "<provide discovery URL here>",
    "redirectURL": "http://localhost:8080/mobileconnect.html",
    "xRedirect": "True",
    "includeRequestIP": "False"
}
```
You can edit them in the UI while application is running.

## Using the Without Discovery Application
Configure the MobileConnectConfig instance with your credentials from the [MobileConnect Developer Site](https://developer.mobileconnect.io) portal.
Required parameters:

Default values are in `/local-path/mobile-connect-demo/src/main/resources/public/data/defaultDataWD.json` file.

```posh
{
	"clientID": "<provide client id>",
	"clientSecret": "<provide client secret>",
	"subscriberID": "<provide subsriber id>",
	"clientName": "<provide client name>",
	"authorizationURL": "<provide authorize endpoint>",
	"tokenURL": "<provide token endpoint>",
	"userInfoURL": "<provide user info endpoint>",
	"metadataURL": "<provide metadata endpoint>",
	"discoveryURL": "<provide discovery endpoint>",
	"redirectURL": "http://localhost:8080/mobileconnect.html"
}
```
You can edit them in the UI while application is running.

## Using the Indian Demo Application
Configure the MobileConnectConfig instance with your credentials from the [MobileConnect Developer Site](https://developer.mobileconnect.io) portal.  
Required parameters:   

Click "Request parameters" button to see your Discovery configuration.
Choose API version which you want to use.

Default values are in `/local-path/mobile-connect-demo/src/main/resources/public/data/defaultDataIndian.json` file.

```posh
{
    "msisdn": "<provide msisdn here>",
    "mcc": "<provide mcc>",
    "mnc": "<provide mnc>",
    "clientID": "<provide client id here>",
    "clientSecret": "<provide client secret here>",
    "discoveryURL": "<provide discovery URL here>",
    "redirectURL": "http://localhost:8080/mobileconnect.html",
    "xRedirect": "True",
    "includeRequestIP": "False"
}
```
You can edit them in the UI while application is running.


## Support

If you encounter any issues which are not resolved by consulting the resources below then [send us a message](https://developer.mobileconnect.io/content/contact-us)

## Resources

- [MobileConnect Discovery API Information](https://developer.mobileconnect.io/discovery-api)
- [MobileConnect Authentication API Information](https://developer.mobileconnect.io/mobile-connect-api)
- [MobileConnect Authentication API (v2.0) Information](https://developer.mobileconnect.io/mobile-connect-profile-v2-0)
