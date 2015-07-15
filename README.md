# IBM Watson Personality Insights Twitter Java Sample

This sample shows how to get Twitter data using the [Twitter REST API](https://dev.twitter.com/rest/public) 
(via the [Twitter4j client library](http://twitter4j.org/en/index.html)) and submit it to the 
(Personality Insights Service)[https://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/personality-insights.html].

For non-twitter samples and more details on how to setup your Personality Insights service in bluemix see the [official 
Watson Developer Cloud samples](https://github.com/watson-developer-cloud).

To configure the sample, copy twittersample.properties.example to twittersample.properties and fill in 
your Twitter and Personality Insights Credentials.  Instructions are provided in the example properties file.

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

    java com.ibm.watson.personalityinsights.sample.twitter.TwitterAnalyzer @jschoudt
