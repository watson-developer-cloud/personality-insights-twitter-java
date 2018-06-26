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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * The Class Twitter4JHelper.
 */
public class Twitter4JHelper implements RateLimitStatusListener {
  private static final String CONSUMER_KEY = "twitter.consumerKey";
  private static final String CONSUMER_SECRET = "twitter.consumerSecret";
  private static final String ACCESS_TOKEN = "twitter.accessToken";
  private static final String ACCESS_SECRET = "twitter.accessSecret";
  private Twitter client = null;
  private boolean rateLimited = false;
  private long rateLimitResetTime = -1;

  /**
   * Instantiates a new twitter 4 J helper.
   *
   * @param properties the properties
   * @throws Exception the exception
   */
  public Twitter4JHelper(Properties properties) throws Exception {
    String consumerKey = properties.getProperty(CONSUMER_KEY);
    String consumerSecret = properties.getProperty(CONSUMER_SECRET);
    String accessToken = properties.getProperty(ACCESS_TOKEN);
    String accessSecret = properties.getProperty(ACCESS_SECRET);

    // Validate that these are set and throw an error if they are not
    if (consumerKey == null || consumerKey.isEmpty()) {
      throw new Exception(CONSUMER_KEY + " cannot be null");
    }
    if (consumerSecret == null || consumerSecret.isEmpty()) {
      throw new Exception(CONSUMER_SECRET + " cannot be null");
    }
    if (accessToken == null || accessToken.isEmpty()) {
      throw new Exception(ACCESS_TOKEN + " cannot be null");
    }
    if (accessSecret == null || accessSecret.isEmpty()) {
      throw new Exception(ACCESS_SECRET + " cannot be null");
    }

    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey(consumerKey)
      .setOAuthConsumerSecret(consumerSecret)
      .setOAuthAccessToken(accessToken)
      .setOAuthAccessTokenSecret(accessSecret);

    TwitterFactory twitterFactory = new TwitterFactory(cb.build());
    client = twitterFactory.getInstance();
    client.addRateLimitStatusListener(this);
  }

  /**
   * Gets the user image.
   *
   * @param status the status
   * @return the user image
   */
  public String getUserImage(Status status) {
    return status.getUser().getProfileImageURL();
  }

  /**
   * Gets the tweets.
   *
   * @param idOrHandle the id or handle
   * @param langs the langs
   * @param numberOfNonRetweets the number of non retweets
   * @return the tweets
   * @throws Exception the exception
   */
  public List<Status> getTweets(String idOrHandle, Set<String> langs, int numberOfNonRetweets) throws Exception {
    List<Status> retval = new ArrayList<Status>();
    long userId = -1;
    if (idOrHandle.startsWith("@")) {
      // Check rate limit
      checkRateLimitAndThrow();
      User user = client.showUser(idOrHandle.substring(1));
      if (user == null)
        throw new Exception("Handle " + idOrHandle + " is not a valid twitter handle.");
      userId = user.getId();
    } else {
      userId = Long.valueOf(idOrHandle);
    }

    long cursor = -1;
    Paging page = new Paging(1, 200);
    do {
      checkRateLimitAndThrow();
      ResponseList<Status> tweets = client.getUserTimeline(userId, page);
      if (tweets == null || tweets.size() == 0)
        break;
      for (int i = 0; i < tweets.size(); i++) {
        Status status = tweets.get(i);
        cursor = status.getId() - 1;

        // Ignore retweets
        if (status.isRetweet())
          continue;
        // Language
        if (!langs.contains(status.getLang()))
          continue;
        retval.add(status);
        if (retval.size() >= numberOfNonRetweets)
          return retval;
      }
      page.maxId(cursor);
    } while (true);
    return retval;
  }

  private synchronized void setRateLimitStatus(boolean rateLimitReached, long resetTime) {
    rateLimited = rateLimitReached;
    rateLimitResetTime = resetTime;
  }

  private synchronized boolean isRateLimited() {
    if (rateLimited && System.currentTimeMillis() > rateLimitResetTime) {
      rateLimited = false;
      rateLimitResetTime = -1;
    }
    return rateLimited;
  }

  private void checkRateLimitAndThrow() throws Exception {
    if (isRateLimited()) {
      SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
      String dateText = df2.format(rateLimitResetTime);

      throw new Exception("The twitter api rate limit has been hit. "
        + "No more requests will be sent until the rate limit resets at " + dateText);
    }
  }

  /**
   * On rate limit reached.
   *
   * @param rlStatusEvent the rl status event
   */
  @Override
  public void onRateLimitReached(RateLimitStatusEvent rlStatusEvent) {
    RateLimitStatus rls = rlStatusEvent.getRateLimitStatus();
    setRateLimitStatus(true, ((long) rls.getResetTimeInSeconds()) * 1000L);
    System.err
        .println("Twitter rate limit reached, stopping all requests for " + rls.getSecondsUntilReset() + " seconds");
  }

  /**
   * On rate limit status.
   *
   * @param rlStatusEvent the rl status event
   */
  @Override
  public void onRateLimitStatus(RateLimitStatusEvent rlStatusEvent) {
    @SuppressWarnings("unused")
    RateLimitStatus rls = rlStatusEvent.getRateLimitStatus();
  }

}
