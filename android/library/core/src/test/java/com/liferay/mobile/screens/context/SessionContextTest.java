/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.screens.context;

import android.content.Context;
import android.content.SharedPreferences;

import com.liferay.mobile.android.auth.basic.BasicAuthentication;
import com.liferay.mobile.android.oauth.OAuth;
import com.liferay.mobile.android.oauth.OAuthConfig;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.screens.BuildConfig;
import com.liferay.mobile.screens.RobolectricManifestTestRunner;
import com.liferay.mobile.screens.context.storage.sharedPreferences.CredentialsStoreSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.liferay.mobile.screens.context.storage.CredentialsStoreBuilder.StorageType.SHARED_PREFERENCES;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * @author Silvio Santos
 */
@RunWith(Enclosed.class)
public class SessionContextTest {

	@Config(constants = BuildConfig.class, emulateSdk = 18)
	@RunWith(RobolectricManifestTestRunner.class)
	public static class WhenCreateSession {

		@Before
		public void setUp() {
			_session = SessionContext.createBasicSession("username", "password");
			assertNotNull(_session);
		}

		@Test
		public void shouldCreateTheRightSession() throws Exception {
			assertTrue(_session.getAuthentication() instanceof BasicAuthentication);

			BasicAuthentication auth = (BasicAuthentication) _session.getAuthentication();

			assertEquals("username", auth.getUsername());
			assertEquals("password", auth.getPassword());
			assertTrue(SessionContext.hasSession());
		}

		@Test
		public void shouldCreateASessionFromCurrentSession() throws Exception {
			Session secondSession = SessionContext.createSessionFromCurrentSession();

			assertNotNull(secondSession);

			BasicAuthentication auth1 = (BasicAuthentication) _session.getAuthentication();
			BasicAuthentication auth2 = (BasicAuthentication) secondSession.getAuthentication();

			assertEquals(auth1.getUsername(), auth2.getUsername());
			assertEquals(auth1.getPassword(), auth2.getPassword());

			assertNotSame(_session, secondSession);
		}

		@Test
		public void shouldReturnTheBasicAuthentication() throws Exception {
			BasicAuthentication auth = (BasicAuthentication) SessionContext.getAuthentication();

			assertNotNull(auth);
			assertSame(_session.getAuthentication(), auth);

			assertEquals("username", auth.getUsername());
			assertEquals("password", auth.getPassword());
		}

		private Session _session;

	}

	@Config(constants = BuildConfig.class, emulateSdk = 18)
	@RunWith(RobolectricManifestTestRunner.class)
	public static class WhenClearSession {

		@Before
		public void setUp() {
			SessionContext.createBasicSession("username", "password");
			SessionContext.clearSession();
		}

		@Test
		public void shouldClearTheSession() throws Exception {
			assertFalse(SessionContext.hasSession());
		}

		@Test(expected = IllegalStateException.class)
		public void shouldNotAllowToCreateASessionFromCurrentSession() throws Exception {
			SessionContext.createSessionFromCurrentSession();
		}

	}

	@Config(constants = BuildConfig.class, emulateSdk = 18)
	@RunWith(RobolectricManifestTestRunner.class)
	public static class WhenSettingUserAttributes {

		@Before
		public void setUp() throws JSONException {
			SessionContext.createBasicSession("username", "password");
			SessionContext.setLoggedUser(new User(new JSONObject().put("userId", 123)));
		}

		@Test
		public void shouldReturnTheUserObject() throws Exception {
			assertNotNull(SessionContext.getLoggedUser());
		}

		@Test
		public void userObjectShouldContainTheUserAttributes() throws Exception {
			assertEquals(123, SessionContext.getLoggedUser().getId());
		}

		@Test
		public void shouldClearUserWhenSessionIsCleared() throws Exception {
			SessionContext.clearSession();
			assertNull(SessionContext.getLoggedUser());
		}

	}

	@Config(constants = BuildConfig.class, emulateSdk = 18)
	@RunWith(RobolectricManifestTestRunner.class)
	public static class WhenStoreSessionInSharedPreferences {

		@Test(expected = IllegalStateException.class)
		public void shouldRaiseExceptionWhenContextIsNotPresent() throws Exception {
			LiferayScreensContext.deinit();

			SessionContext.createBasicSession("user123", "pass123");
			SessionContext.setLoggedUser(new User(new JSONObject().put("userId", 123)));

			SessionContext.storeSession(SHARED_PREFERENCES);
		}

		@Test(expected = IllegalStateException.class)
		public void shouldRaiseExceptionWhenSessionIsNotPresent() throws Exception {
			Context ctx = RuntimeEnvironment.application.getApplicationContext();
			LiferayScreensContext.init(ctx);

			SessionContext.clearSession();
			SessionContext.setLoggedUser(new User(new JSONObject().put("userId", 123)));

			SessionContext.storeSession(SHARED_PREFERENCES);
		}

		@Test(expected = IllegalStateException.class)
		public void shouldRaiseExceptionWhenUserAttributesAreNotPresent() throws Exception {
			Context ctx = RuntimeEnvironment.application.getApplicationContext();
			LiferayScreensContext.init(ctx);

			SessionContext.clearSession(); // to clean user
			SessionContext.createBasicSession("user123", "pass123");

			SessionContext.storeSession(SHARED_PREFERENCES);
		}

		@Test
		public void shouldStoreBasicCredentialsInSharedPreferences() throws Exception {
			SessionContext.createBasicSession("user123", "pass123");

			Context ctx = RuntimeEnvironment.application.getApplicationContext();
			LiferayScreensContext.init(ctx);

			JSONObject userAttributes = new JSONObject().put("userId", 123);
			SessionContext.setLoggedUser(new User(userAttributes));

			SessionContext.storeSession(SHARED_PREFERENCES);

			String sharedPreferencesName = new CredentialsStoreSharedPreferences().getStoreName();
			SharedPreferences sharedPref =
				ctx.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);

			assertEquals(LiferayServerContext.getServer(), sharedPref.getString("server", "not-present"));
			assertEquals(LiferayServerContext.getGroupId(), sharedPref.getLong("groupId", 0));
			assertEquals(LiferayServerContext.getCompanyId(), sharedPref.getLong("companyId", 0));
			assertEquals(userAttributes.toString(), sharedPref.getString("attributes", "not-present"));

			assertEquals("user123", sharedPref.getString("username", "not-present"));
			assertEquals("pass123", sharedPref.getString("password", "not-present"));
		}

		@Test
		public void shouldStoreOAuthCredentialsInSharedPreferences() throws Exception {
			OAuthConfig config = new OAuthConfig(
					"my_consumerKey", "my_consumerSecret",
					"my_token", "my_tokenSecret");
			SessionContext.createOAuthSession(config);

			Context ctx = RuntimeEnvironment.application.getApplicationContext();
			LiferayScreensContext.init(ctx);

			JSONObject userAttributes = new JSONObject().put("userId", 123);
			SessionContext.setLoggedUser(new User(userAttributes));

			SessionContext.storeSession(SHARED_PREFERENCES);

			String sharedPreferencesName = new CredentialsStoreSharedPreferences().getStoreName();

			SharedPreferences sharedPref =
				ctx.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);

			assertEquals(LiferayServerContext.getServer(), sharedPref.getString("server", "not-present"));
			assertEquals(LiferayServerContext.getGroupId(), sharedPref.getLong("groupId", 0));
			assertEquals(LiferayServerContext.getCompanyId(), sharedPref.getLong("companyId", 0));
			assertEquals(userAttributes.toString(), sharedPref.getString("attributes", "not-present"));

			assertEquals("my_consumerKey", sharedPref.getString("oauth_consumerKey", "not-present"));
			assertEquals("my_consumerSecret", sharedPref.getString("oauth_consumerSecret", "not-present"));
			assertEquals("my_token", sharedPref.getString("oauth_token", "not-present"));
			assertEquals("my_tokenSecret", sharedPref.getString("oauth_tokenSecret", "not-present"));
		}

		@Test
		public void shouldLoadBasicCredentials() throws Exception {
			SessionContext.createBasicSession("user123", "pass123");

			Context ctx = RuntimeEnvironment.application.getApplicationContext();
			LiferayScreensContext.init(ctx);

			JSONObject userAttributes = new JSONObject().put("userId", 123);
			SessionContext.setLoggedUser(new User(userAttributes));

			SessionContext.storeSession(SHARED_PREFERENCES);
			SessionContext.clearSession();
			SessionContext.loadSessionFromStore(SHARED_PREFERENCES);

			String sharedPreferencesName = new CredentialsStoreSharedPreferences().getStoreName();
			SharedPreferences sharedPref =
				ctx.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);

			assertEquals(sharedPref.getString("server", "not-present"), LiferayServerContext.getServer());
			assertEquals(sharedPref.getLong("groupId", 0), LiferayServerContext.getGroupId());
			assertEquals(sharedPref.getLong("companyId", 0), LiferayServerContext.getCompanyId());
			assertEquals(sharedPref.getString("attributes", "not-present"), userAttributes.toString());

			BasicAuthentication auth = (BasicAuthentication) SessionContext.getAuthentication();

			assertEquals("user123", auth.getUsername());
			assertEquals("pass123", auth.getPassword());
		}


		@Test
		public void shouldLoadOAuthCredentials() throws Exception {
			OAuthConfig config = new OAuthConfig(
				"my_consumerKey", "my_consumerSecret",
				"my_token", "my_tokenSecret");
			SessionContext.createOAuthSession(config);

			Context ctx = RuntimeEnvironment.application.getApplicationContext();
			LiferayScreensContext.init(ctx);

			JSONObject userAttributes = new JSONObject().put("userId", 123);
			SessionContext.setLoggedUser(new User(userAttributes));

			SessionContext.storeSession(SHARED_PREFERENCES);
			SessionContext.clearSession();
			SessionContext.loadSessionFromStore(SHARED_PREFERENCES);

			String sharedPreferencesName = new CredentialsStoreSharedPreferences().getStoreName();
			SharedPreferences sharedPref =
				ctx.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);


			assertEquals(sharedPref.getString("server", "not-present"), LiferayServerContext.getServer());
			assertEquals(sharedPref.getLong("groupId", 0), LiferayServerContext.getGroupId());
			assertEquals(sharedPref.getLong("companyId", 0), LiferayServerContext.getCompanyId());
			assertEquals(sharedPref.getString("attributes", "not-present"), userAttributes.toString());

			OAuth oauth = (OAuth) SessionContext.getAuthentication();

			assertEquals("my_consumerKey", oauth.getConsumerKey());
			assertEquals("my_consumerSecret", oauth.getConsumerSecret());
			assertEquals("my_token", oauth.getToken());
			assertEquals("my_tokenSecret", oauth.getTokenSecret());
		}

	}

}