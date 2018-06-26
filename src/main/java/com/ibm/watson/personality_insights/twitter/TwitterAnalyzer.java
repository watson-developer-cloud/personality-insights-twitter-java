/*
 * Copyright 2018 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.ibm.watson.personality_insights.twitter;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import com.ibm.watson.developer_cloud.personality_insights.v3.PersonalityInsights;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.Content;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.ContentItem;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.Profile;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.ProfileOptions;
import com.ibm.watson.developer_cloud.service.security.IamOptions;

import twitter4j.Status;

/**
 * The Twitter Analyzer.
 */
public class TwitterAnalyzer {

  private static final String URL = "personality_insights.url";
  private static final String USERNAME = "personality_insights.username";
  private static final String PASSWORD = "personality_insights.password";
  private static final String IAM_APIKEY = "personality_insights.iam_apikey";
  private static final String VERSION_DATE = "2017-10-13";

  private TwitterAnalyzer() {

  }

  /**
   * The main method.
   *
   * @param args the arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {
    String handle = args.length < 1 ? "jschoudt" : args[0];
    Properties props = new Properties();
    props.load(new FileInputStream("twitter.properties"));

    PersonalityInsights service = getPersonalityInsightsService(props);

    Twitter4JHelper twitterHelper = new Twitter4JHelper(props);

    HashSet<String> langs = new HashSet<String>();
    langs.add("en");
    langs.add("es");

    System.out.println("Getting tweets for: " + handle);
    List<Status> tweets = twitterHelper.getTweets(handle, langs, 200);

    System.out.println("Got " + tweets.size() + " tweets");
    Content content = convertTweetsToContent(tweets);
    ProfileOptions options = new ProfileOptions.Builder().content(content).build();

    Profile profile = service.profile(options).execute();

    System.out.println(profile);
  }

  /**
   * Convert a list of tweets to content.
   *
   * @param tweets the tweets
   * @return The tweets as content
   * @throws Exception the exception
   */
  public static Content convertTweetsToContent(List<Status> tweets) throws Exception {
    List<ContentItem> contentItems = new ArrayList<ContentItem>();

    for (Status status : tweets) {
      ContentItem contentItem = new ContentItem.Builder()
          .id(Long.toString(status.getId()))
          .forward(status.isRetweet())
          .reply((status.getInReplyToScreenName() != null))
          .language(status.getLang())
          .contenttype("text/plain")
          .content(status.getText().replaceAll("[^(\\x20-\\x7F)]*", ""))
          .created(status.getCreatedAt().getTime())
          .build();
      contentItems.add(contentItem);
    }

    return new Content.Builder().contentItems(contentItems).build();
  }

  /**
   * Gets the personality insights service.
   *
   * @param props the props
   * @return the personality insights service
   * @throws Exception the exception
   */
  public static PersonalityInsights getPersonalityInsightsService(Properties props) throws Exception {
    PersonalityInsights service = null;

    String username = props.getProperty(USERNAME);
    String password = props.getProperty(PASSWORD);
    String iamApiKey = props.getProperty(IAM_APIKEY);
    String url = props.getProperty(URL);

    if (iamApiKey != null && !iamApiKey.isEmpty()) {
      IamOptions options = new IamOptions.Builder().apiKey(iamApiKey).build();

      service = new PersonalityInsights(VERSION_DATE, options);
    } else if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
      throw new Exception("iam_apikey or username and password should be specified in the twitter.properties file");
    } else {
      service = new PersonalityInsights(VERSION_DATE, username, password);
    }

    if (url != null && !url.isEmpty()) {
      service.setEndPoint(url);
    }

    return service;
  }

}
