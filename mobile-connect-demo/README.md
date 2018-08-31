GSMA MobileConnect Java Server Side
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

## Support

If you encounter any issues which are not resolved by consulting the resources below then [send us a message](https://developer.mobileconnect.io/content/contact-us)

## Resources

- [MobileConnect Discovery API Information](https://developer.mobileconnect.io/discovery-api)
- [MobileConnect Authentication API Information](https://developer.mobileconnect.io/mobile-connect-api)
- [MobileConnect Authentication API (v2.0) Information](https://developer.mobileconnect.io/mobile-connect-profile-v2-0)
