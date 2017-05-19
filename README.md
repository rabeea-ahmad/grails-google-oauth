# How to set up Google Oauth Authorization Flow in Grails

I was looking into integrating oauth into a grails project I was working on. As anyone who has ever used grails, you know it has similarities to Java and so since I was not using a grails plugin for the Google API, I used a Java SDK and ran into some issues to get things working in grails. 

## Getting Started

I am using Grails 3.2.8 and Java 7 and used Gradle. 

These are some links that I found extremely helpful in learning more about the Google API Client and Oauth in general:
https://developers.google.com/api-client-library/java/

This is the Google API library I used:
https://github.com/google/google-api-java-client

! Important Note !
It's vaguely stated in the repo, but in order to use the library above, you also need to include these libraries as well:
https://github.com/google/google-oauth-java-client
https://github.com/google/google-http-java-client

These can all be included as dependencies in you build.gradle file as such: 

``` dependencies {
	    compile 'com.google.api-client:google-api-client:1.20.0'
	    compile group: 'com.google.oauth-client', name: 'google-oauth-client', version: '1.22.0'
	    compile group: 'com.google.http-client', name: 'google-http-client', version: '1.22.0'
	} ```

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details