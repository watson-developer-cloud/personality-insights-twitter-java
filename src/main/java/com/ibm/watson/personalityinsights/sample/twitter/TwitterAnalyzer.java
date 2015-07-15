package com.ibm.watson.personalityinsights.sample.twitter;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import twitter4j.Status;

public class TwitterAnalyzer {

	public static void main(String[] args) throws Exception {
		String handle = args.length < 1 ? "jschoudt" : args[0];
		Properties props = new Properties();
		props.load(FileUtils.openInputStream(new File("twittersample.properties")));
		
		Twitter4JHelper twitterHelper = new Twitter4JHelper(props);
		PersonalityInsightsHelper piHelper = new PersonalityInsightsHelper(props);
		
		HashSet<String> langs = new HashSet<String>();
		langs.add("en");
		langs.add("es");
		
		List<Status> tweets = twitterHelper.getTweets(handle, langs, 200);
		String contentItemsJson = twitterHelper.convertTweetsToPIContentItems(tweets);
		String profile = piHelper.getProfileJSON(contentItemsJson, false);
		//String profile = piHelper.getProfileCSV(contentItemsJson, false);
		System.out.println(profile);
	}

}
