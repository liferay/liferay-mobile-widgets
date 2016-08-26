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

package com.liferay.mobile.screens.auth.login.interactor;

import com.liferay.mobile.screens.BuildConfig;
import com.liferay.mobile.screens.RobolectricManifestTestRunner;
import com.liferay.mobile.screens.auth.BasicAuthMethod;
import com.liferay.mobile.screens.auth.login.LoginListener;
import com.liferay.mobile.screens.auth.login.connector.UserConnector;
import com.liferay.mobile.screens.base.thread.event.BasicThreadEvent;
import com.liferay.mobile.screens.base.thread.event.ErrorThreadEvent;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.liferay.mobile.screens.context.User;
import com.liferay.mobile.screens.util.MockFactory;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * @author Silvio Santos
 */
@RunWith(Enclosed.class)
public class LoginInteractorTest {

	private static final int _TARGET_SCREENLET_ID = 0;
	private static final String _LOGIN_EMAIL = "test@liferay.com";
	private static final String _LOGIN_PASSWORD = "test";
	private static final String _LOGIN_SCREEN_NAME = "test_screen_name";
	private static final long _LOGIN_USER_ID = 10658;
	private static final long _companyId = LiferayServerContext.getCompanyId();

	@Config(constants = BuildConfig.class, sdk = 18)
	@RunWith(RobolectricManifestTestRunner.class)
	public static class WhenBasicAuthMethodIsEmail {

		@Test
		public void shouldCallGetUserByEmailService() throws Exception {
			LoginBasicInteractor interactorSpy = MockFactory.spyLoginInteractor();

			UserConnector serviceMock = MockFactory.mockUserConnector();

			doReturn(serviceMock).when(interactorSpy).getUserConnector(_LOGIN_EMAIL, _LOGIN_PASSWORD);

			interactorSpy.execute(_LOGIN_EMAIL, _LOGIN_PASSWORD, BasicAuthMethod.EMAIL);

			verify(serviceMock).getUserByEmailAddress(_companyId, _LOGIN_EMAIL);
		}
	}

	@RunWith(RobolectricTestRunner.class)
	@Config(constants = BuildConfig.class, sdk = 18)
	public static class WhenBasicAuthMethodIsId {

		@Test
		public void shouldCallGetUserByIdService() throws Exception {
			LoginBasicInteractor interactorSpy = MockFactory.spyLoginInteractor();

			UserConnector serviceMock = MockFactory.mockUserConnector();

			String userId = String.valueOf(_LOGIN_USER_ID);

			doReturn(serviceMock).when(interactorSpy).getUserConnector(userId, _LOGIN_PASSWORD);

			interactorSpy.execute(userId, _LOGIN_PASSWORD, BasicAuthMethod.USER_ID);

			verify(serviceMock).getUserById(_LOGIN_USER_ID);
		}
	}

	@RunWith(RobolectricTestRunner.class)
	@Config(constants = BuildConfig.class, sdk = 18)
	public static class WhenBasicAuthMethodIsScreenName {

		@Test
		public void shouldCallGetUserByScreenNameService() throws Exception {
			LoginBasicInteractor interactorSpy = MockFactory.spyLoginInteractor();

			UserConnector serviceMock = MockFactory.mockUserConnector();

			doReturn(serviceMock).when(interactorSpy).getUserConnector(_LOGIN_SCREEN_NAME, _LOGIN_PASSWORD);

			interactorSpy.execute(_LOGIN_SCREEN_NAME, _LOGIN_PASSWORD, BasicAuthMethod.SCREEN_NAME);

			verify(serviceMock).getUserByScreenName(_companyId, _LOGIN_SCREEN_NAME);
		}
	}

	@RunWith(RobolectricTestRunner.class)
	@Config(constants = BuildConfig.class, sdk = 18)
	public static class WhenLoginMethodIsCalled {

		@Test
		public void shouldCallValidate() throws Exception {
			LoginBasicInteractor interactorSpy = MockFactory.spyLoginInteractor();

			doReturn(MockFactory.mockUserConnector()).when(interactorSpy)
				.getUserConnector(_LOGIN_EMAIL, _LOGIN_PASSWORD);

			interactorSpy.execute(_LOGIN_EMAIL, _LOGIN_PASSWORD, BasicAuthMethod.EMAIL);

			verify(interactorSpy).validate(_LOGIN_EMAIL, _LOGIN_PASSWORD, BasicAuthMethod.EMAIL);
		}
	}

	@RunWith(RobolectricTestRunner.class)
	@Config(constants = BuildConfig.class, sdk = 18)
	public static class WhenLoginRequestCompletes {

		@Test
		public void shouldCallListenerSuccess() throws Exception {
			LoginListener listener = MockFactory.mockLoginListener();
			JSONObject result = new JSONObject();
			BasicThreadEvent event = new BasicThreadEvent(result);

			_loginWithResponseEvent(event, listener);

			verify(listener).onLoginSuccess(any(User.class));
		}

		@Test
		public void shouldCallListenerFailure() throws Exception {
			LoginListener listener = MockFactory.mockLoginListener();
			Exception e = new Exception();
			BasicThreadEvent event = new ErrorThreadEvent(e);

			_loginWithResponseEvent(event, listener);

			verify(listener).onLoginFailure(e);
		}

		private void _loginWithResponseEvent(final BasicThreadEvent event, LoginListener listener) throws Exception {

			final LoginBasicInteractor interactorSpy = MockFactory.spyLoginInteractor();

			UserConnector serviceMock = MockFactory.mockUserConnector();

			doReturn(serviceMock).when(interactorSpy).getUserConnector(_LOGIN_EMAIL, _LOGIN_PASSWORD);

			interactorSpy.onScreenletAttached(listener);

			doAnswer(new Answer<Void>() {
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {

					interactorSpy.onEventMainThread(event);

					return null;
				}
			}).when(serviceMock).getUserByEmailAddress(_companyId, _LOGIN_EMAIL);

			interactorSpy.execute(_LOGIN_EMAIL, _LOGIN_PASSWORD, BasicAuthMethod.EMAIL);
		}
	}

	@RunWith(RobolectricTestRunner.class)
	@Config(constants = BuildConfig.class, sdk = 18)
	public static class WhenValidateMethodIsCalled {

		@Test(expected = IllegalArgumentException.class)
		public void shouldRaiseExceptionOnNullLogin() {
			LoginBasicInteractor interactorSpy = MockFactory.spyLoginInteractor();

			interactorSpy.validate(null, _LOGIN_PASSWORD, BasicAuthMethod.EMAIL);
		}

		@Test(expected = IllegalArgumentException.class)
		public void shouldRaiseExceptionOnNullPassword() {
			LoginBasicInteractor interactorSpy = MockFactory.spyLoginInteractor();

			interactorSpy.validate(_LOGIN_EMAIL, null, BasicAuthMethod.EMAIL);
		}

		@Test(expected = IllegalArgumentException.class)
		public void shouldRaiseExceptionOnNullBasicAuthMethod() {
			LoginBasicInteractor interactorSpy = MockFactory.spyLoginInteractor();

			interactorSpy.validate(_LOGIN_EMAIL, _LOGIN_PASSWORD, null);
		}
	}
}