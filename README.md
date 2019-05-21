# IBM Watson Personality Insights Twitter Java Sample [![Build Status](https://travis-ci.org/watson-developer-cloud/personality-insights-twitter-java.svg?branch=master)](https://travis-ci.org/watson-developer-cloud/personality-insights-twitter-java)

## DEPRECATED: this repo is no longer actively maintained. It can still be used as reference, but may contain outdated or unpatched code.

This sample shows how to get Twitter data using the [Twitter REST API](https://dev.twitter.com/rest/public)
(via the [Twitter4j client library](http://twitter4j.org/en/index.html)) and submit it to the
[Personality Insights Service](https://www.ibm.com/watson/services/personality-insights/).

For non-twitter samples and more details on how to setup your Personality Insights service in the IBM Cloud see the [official
Watson Developer Cloud sample](https://github.com/watson-developer-cloud/personality-insights-java).

To configure the sample, copy `twitter.properties.example` to `twitter.properties` and fill in
your Twitter and Personality Insights Credentials. Instructions are provided in the example properties file.

## Building the sample

To build the code, use the provided [gradle](http://gradle.org/) build file and wrapper configuration.

On Mac or Linux:

    ./gradlew build

On Windows:

    ./gradlew.bat build

## Running the sample

To run the sample, run the TwitterAnalyzer java class and pass in a twitter
user's handle (with an @ sign) or their numerical id.

Example:

    java com.ibm.watson.personality_insights.twitter.TwitterAnalyzer @jschoudt

## License
Apache-2.0
