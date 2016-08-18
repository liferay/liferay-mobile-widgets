/**
* Copyright (c) 2000-present Liferay, Inc. All rights reserved.
*
* This library is free software; you can redistribute it and/or modify it under
* the terms of the GNU Lesser General Public License as published by the Free
* Software Foundation; either version 2.1 of the License, or (at your option)
* any later version.
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*/
import Foundation


@objc(LiferayConnectorFactory)
public protocol LiferayConnectorFactory {

	func createGetUserByEmailConnector(companyId companyId: Int64, emailAddress: String) -> GetUserByEmailLiferayConnector

	func createGetUserByScreenNameConnector(companyId companyId: Int64, screenName: String) -> GetUserByScreenNameLiferayConnector

	func createGetUserByUserIdConnector(userId userId: Int64) -> GetUserByUserIdLiferayConnector

	func createLoginByEmailConnector(
		companyId companyId: Int64,
		emailAddress: String,
		password: String) -> GetUserByEmailLiferayConnector

	func createLoginByScreenNameConnector(
		companyId companyId: Int64,
		screenName: String,
		password: String) -> GetUserByScreenNameLiferayConnector

	func createLoginByUserIdConnector(
		userId userId: Int64,
		password: String) -> GetUserByUserIdLiferayConnector

	func createForgotPasswordByEmailConnector(
		viewModel viewModel: ForgotPasswordViewModel,
		anonymousUsername: String,
		anonymousPassword: String) -> ForgotPasswordBaseLiferayConnector

	func createForgotPasswordByScreenNameConnector(
		viewModel viewModel: ForgotPasswordViewModel,
		anonymousUsername: String,
		anonymousPassword: String) -> ForgotPasswordBaseLiferayConnector

	func createForgotPasswordByUserIdConnector(
		viewModel viewModel: ForgotPasswordViewModel,
		anonymousUsername: String,
		anonymousPassword: String) -> ForgotPasswordBaseLiferayConnector

	func createSignUpConnector(
		viewModel viewModel: SignUpViewModel,
		anonymousUsername: String,
		anonymousPassword: String) -> SignUpLiferayConnector

	func createUpdateCurrentUserConnector(
		viewModel viewModel: SignUpViewModel) -> UpdateCurrentUserLiferayConnector

	func createUploadUserPortraitConnector(
		userId userId: Int64,
		image: UIImage) -> UploadUserPortraitLiferayConnector

	func createAssetListPageConnector(
		startRow startRow: Int,
		endRow: Int,
		computeRowCount: Bool) -> AssetListPageLiferayConnector

	func createWebContentListPageConnector(
		groupId groupId: Int64,
		folderId: Int64,
		startRow: Int,
		endRow: Int,
		computeRowCount: Bool) -> WebContentListPageLiferayConnector

	func createDDLListPageConnector(
		viewModel viewModel: DDLListViewModel,
		startRow: Int,
		endRow: Int,
		computeRowCount: Bool) -> DDLListPageLiferayConnector

	func createWebContentLoadHtmlConnector(
		groupId groupId: Int64,
		articleId: String) -> WebContentLoadHtmlLiferayConnector

	func createWebContentLoadStructuredConnector(
		groupId groupId: Int64,
		articleId: String,
		structureId: Int64) -> WebContentLoadStructuredLiferayConnector

	func createDDLFormLoadConnector(structureId: Int64) -> DDLFormLoadLiferayConnector

	func createDDLFormRecordLoadConnector(recordId: Int64) -> DDLFormRecordLoadLiferayConnector

	func createDDLFormSubmitConnector(
		values values: [String:AnyObject],
		viewModel: DDLFormViewModel?) -> DDLFormSubmitLiferayConnector

	func createDDLFormUploadConnector(
		document document: DDMFieldDocument,
		filePrefix: String,
		repositoryId: Int64,
		folderId: Int64,
		onProgress: DDLFormUploadLiferayConnector.OnProgress?) -> DDLFormUploadLiferayConnector
	
	func createRatingLoadByEntryIdConnector(
		entryId entryId: Int64,
		ratingsGroupCount: Int32) -> RatingLoadByEntryIdLiferayConnector?
	
	func createRatingLoadByClassPKConnector(
		classPK: Int64,
		className: String,
		ratingsGroupCount: Int32) -> RatingLoadByClassPKLiferayConnector?
	
	func createRatingUpdateConnector(
		classPK classPK: Int64,
		className: String,
		score: Double,
		ratingsGroupCount: Int32) -> RatingUpdateLiferayConnector?
	
	func createRatingDeleteConnector(
		classPK classPK: Int64,
		className: String,
		ratingsGroupCount: Int32) -> RatingDeleteLiferayConnector?

	func createCommentListPageConnector(
		groupId groupId: Int64,
		className: String,
		classPK: Int64,
		startRow: Int,
		endRow: Int,
		computeRowCount: Bool) -> CommentListPageLiferayConnector?

	func createCommentAddConnector(
		groupId groupId: Int64,
		className: String,
		classPK: Int64,
		body: String?) -> CommentAddLiferayConnector?

	func createCommentLoadConnector(
		groupId groupId: Int64,
		commentId: Int64) -> CommentLoadLiferayConnector?

	func createCommentDeleteConnector(
		commentId commentId: Int64) -> CommentDeleteLiferayConnector?

	func createCommentUpdateConnector(
		groupId groupId: Int64,
		className: String,
		classPK: Int64,
		commentId: Int64,
		body: String?) -> CommentUpdateLiferayConnector?

}


@objc(Liferay62ConnectorFactory)
public class Liferay62ConnectorFactory: NSObject, LiferayConnectorFactory {

	public func createGetUserByEmailConnector(companyId companyId: Int64, emailAddress: String) -> GetUserByEmailLiferayConnector {
		return GetUserByEmailLiferay62Connector(
			companyId: companyId,
			emailAddress: emailAddress)
	}

	public func createGetUserByScreenNameConnector(companyId companyId: Int64, screenName: String) -> GetUserByScreenNameLiferayConnector {
		return GetUserByScreenNameLiferay62Connector(
			companyId: companyId,
			screenName: screenName)
	}

	public func createGetUserByUserIdConnector(userId userId: Int64) -> GetUserByUserIdLiferayConnector {
		return GetUserByUserIdLiferay62Connector(userId: userId)
	}

	public func createLoginByEmailConnector(companyId companyId: Int64, emailAddress: String, password: String) -> GetUserByEmailLiferayConnector {
		return LoginByEmailLiferay62Connector(
			companyId: companyId,
			emailAddress: emailAddress,
			password: password)
	}

	public func createLoginByScreenNameConnector(companyId companyId: Int64, screenName: String, password: String) -> GetUserByScreenNameLiferayConnector {
		return LoginByScreenNameLiferay62Connector(
			companyId: companyId,
			screenName: screenName,
			password: password)
	}

	public func createLoginByUserIdConnector(userId userId: Int64, password: String) -> GetUserByUserIdLiferayConnector {
		return LoginByUserIdLiferay62Connector(userId: userId, password: password)
	}

	public func createForgotPasswordByEmailConnector(
			viewModel viewModel: ForgotPasswordViewModel,
			anonymousUsername: String,
			anonymousPassword: String) -> ForgotPasswordBaseLiferayConnector {
		return ForgotPasswordEmailLiferay62Connector(
			viewModel: viewModel,
			anonymousUsername: anonymousUsername,
			anonymousPassword: anonymousPassword)
	}

	public func createForgotPasswordByScreenNameConnector(
			viewModel viewModel: ForgotPasswordViewModel,
			anonymousUsername: String,
			anonymousPassword: String) -> ForgotPasswordBaseLiferayConnector {
		return ForgotPasswordScreenNameLiferay62Connector(
			viewModel: viewModel,
			anonymousUsername: anonymousUsername,
			anonymousPassword: anonymousPassword)
	}

	public func createForgotPasswordByUserIdConnector(
			viewModel viewModel: ForgotPasswordViewModel,
			anonymousUsername: String,
			anonymousPassword: String) -> ForgotPasswordBaseLiferayConnector {
		return ForgotPasswordUserIdLiferay62Connector(
			viewModel: viewModel,
			anonymousUsername: anonymousUsername,
			anonymousPassword: anonymousPassword)
	}

	public func createSignUpConnector(
			viewModel viewModel: SignUpViewModel,
			anonymousUsername: String,
			anonymousPassword: String) -> SignUpLiferayConnector {
		return Liferay62SignUpConnector(
			viewModel: viewModel,
			anonymousUsername: anonymousUsername,
			anonymousPassword: anonymousPassword)
	}

	public func createUpdateCurrentUserConnector(
		viewModel viewModel: SignUpViewModel) -> UpdateCurrentUserLiferayConnector {
		return Liferay62UpdateCurrentUserConnector(viewModel: viewModel)
	}

	public func createUploadUserPortraitConnector(
			userId userId: Int64,
			image: UIImage) -> UploadUserPortraitLiferayConnector {
		return Liferay62UploadUserPortraitConnector(
			userId: userId,
			image: image)
	}

	public func createAssetListPageConnector(
			startRow startRow: Int,
			endRow: Int,
			computeRowCount: Bool) -> AssetListPageLiferayConnector {
		return Liferay62AssetListPageConnector(
			startRow: startRow,
			endRow: endRow,
			computeRowCount: computeRowCount)
	}

	public func createWebContentListPageConnector(
			groupId groupId: Int64,
			folderId: Int64,
			startRow: Int,
			endRow: Int,
			computeRowCount: Bool) -> WebContentListPageLiferayConnector {
		return Liferay62WebContentListPageConnector(
			startRow: startRow,
			endRow: endRow,
			computeRowCount: computeRowCount,
			groupId: groupId,
			folderId: folderId)
	}

	public func createDDLListPageConnector(
			viewModel viewModel: DDLListViewModel,
			startRow: Int,
			endRow: Int,
			computeRowCount: Bool) -> DDLListPageLiferayConnector {
		return Liferay62DDLListPageConnector(
			viewModel: viewModel,
			startRow: startRow,
			endRow: endRow,
			computeRowCount: computeRowCount)
	}

	public func createWebContentLoadHtmlConnector(
			groupId groupId: Int64,
			articleId: String) -> WebContentLoadHtmlLiferayConnector {
		return Liferay62WebContentLoadHtmlConnector(
			groupId: groupId,
			articleId: articleId)
	}

	public func createWebContentLoadStructuredConnector(
			groupId groupId: Int64,
			articleId: String,
			structureId: Int64) -> WebContentLoadStructuredLiferayConnector {
		return Liferay62WebContentLoadStructuredConnector(
			groupId: groupId,
			articleId: articleId,
			structureId: structureId)
	}

	public func createDDLFormLoadConnector(structureId: Int64) -> DDLFormLoadLiferayConnector {
		return Liferay62DDLFormLoadConnector(structureId: structureId)
	}

	public func createDDLFormRecordLoadConnector(recordId: Int64) -> DDLFormRecordLoadLiferayConnector {
		return Liferay62DDLFormRecordLoadConnector(recordId: recordId)
	}

	public func createDDLFormSubmitConnector(
			values values: [String:AnyObject],
			viewModel: DDLFormViewModel?) -> DDLFormSubmitLiferayConnector {
		return Liferay62DDLFormSubmitConnector(
			values: values,
			viewModel: viewModel)
	}

	public func createDDLFormUploadConnector(
			document document: DDMFieldDocument,
			filePrefix: String,
			repositoryId: Int64,
			folderId: Int64,
			onProgress: DDLFormUploadLiferayConnector.OnProgress?) -> DDLFormUploadLiferayConnector {
		return Liferay62DDLFormUploadConnector(
			document: document,
			filePrefix: filePrefix,
			repositoryId: repositoryId,
			folderId: folderId,
			onProgress: onProgress)
	}
	
	public func createRatingLoadByEntryIdConnector(entryId entryId: Int64, ratingsGroupCount: Int32) -> RatingLoadByEntryIdLiferayConnector? {
		print("Unsupported connector in Liferay 6.2: RatingLoadByEntryIdLiferayConnector")
		return nil
	}
	
	public func createRatingLoadByClassPKConnector(classPK: Int64, className: String, ratingsGroupCount: Int32) -> RatingLoadByClassPKLiferayConnector? {
		print("Unsupported connector in Liferay 6.2: RatingLoadByClassPKLiferayConnector")
		return nil
	}
	
	public func createRatingUpdateConnector(classPK classPK: Int64, className: String, score: Double, ratingsGroupCount: Int32) -> RatingUpdateLiferayConnector? {
		print("Unsupported connector in Liferay 6.2: RatingUpdateLiferayConnector")
		return nil
	}
	
	public func createRatingDeleteConnector(classPK classPK: Int64, className: String, ratingsGroupCount: Int32) -> RatingDeleteLiferayConnector? {
		print("Unsupported connector in Liferay 6.2: RatingDeleteLiferayConnector")
		return nil
	}

	public func createCommentListPageConnector(
			groupId groupId: Int64,
			className: String,
			classPK: Int64,
			startRow: Int,
			endRow: Int,
			computeRowCount: Bool) -> CommentListPageLiferayConnector? {
		print("Unsupported connector in Liferay 6.2: CommentListPageLiferayConnector")
		return nil
	}

	public func createCommentAddConnector(groupId groupId: Int64, className: String, classPK: Int64, body: String?) -> CommentAddLiferayConnector? {
		print("Unsupported connector in Liferay 6.2: CommentAddLiferayConnector")
		return nil
	}

	public func createCommentLoadConnector(groupId groupId: Int64, commentId: Int64) -> CommentLoadLiferayConnector? {
		print("Unsupported connector in Liferay 6.2: CommentLoadLiferayConnector")
		return nil
	}

	public func createCommentDeleteConnector(commentId commentId: Int64) -> CommentDeleteLiferayConnector? {
		print("Unsupported connector in Liferay 6.2: CommentDeleteLiferayConnector")
		return nil
	}

	public func createCommentUpdateConnector(groupId groupId: Int64, className: String, classPK: Int64, commentId: Int64, body: String?) -> CommentUpdateLiferayConnector? {
		print("Unsupported connector in Liferay 6.2: CommentUpdateLiferayConnector")
		return nil
	}

}


@objc(Liferay70ConnectorFactory)
public class Liferay70ConnectorFactory: NSObject, LiferayConnectorFactory {

	public func createGetUserByEmailConnector(companyId companyId: Int64, emailAddress: String) -> GetUserByEmailLiferayConnector {
		return GetUserByEmailLiferay70Connector(
			companyId: companyId,
			emailAddress: emailAddress)
	}

	public func createGetUserByScreenNameConnector(companyId companyId: Int64, screenName: String) -> GetUserByScreenNameLiferayConnector {
		return GetUserByScreenNameLiferay70Connector(
			companyId: companyId,
			screenName: screenName)
	}

	public func createGetUserByUserIdConnector(userId userId: Int64) -> GetUserByUserIdLiferayConnector {
		return GetUserByUserIdLiferay70Connector(userId: userId)
	}

	public func createLoginByEmailConnector(companyId companyId: Int64, emailAddress: String, password: String) -> GetUserByEmailLiferayConnector {
		return LoginByEmailLiferay70Connector(
			companyId: companyId,
			emailAddress: emailAddress,
			password: password)
	}

	public func createLoginByScreenNameConnector(companyId companyId: Int64, screenName: String, password: String) -> GetUserByScreenNameLiferayConnector {
		return LoginByScreenNameLiferay70Connector(
			companyId: companyId,
			screenName: screenName,
			password: password)
	}

	public func createLoginByUserIdConnector(userId userId: Int64, password: String) -> GetUserByUserIdLiferayConnector {
		return LoginByUserIdLiferay70Connector(userId: userId, password: password)
	}

	public func createForgotPasswordByEmailConnector(
			viewModel viewModel: ForgotPasswordViewModel,
			anonymousUsername: String,
			anonymousPassword: String) -> ForgotPasswordBaseLiferayConnector {
		return ForgotPasswordEmailLiferay70Connector(
			viewModel: viewModel,
			anonymousUsername: anonymousUsername,
			anonymousPassword: anonymousPassword)
	}

	public func createForgotPasswordByScreenNameConnector(
			viewModel viewModel: ForgotPasswordViewModel,
			anonymousUsername: String,
			anonymousPassword: String) -> ForgotPasswordBaseLiferayConnector {
		return ForgotPasswordScreenNameLiferay70Connector(
			viewModel: viewModel,
			anonymousUsername: anonymousUsername,
			anonymousPassword: anonymousPassword)
	}

	public func createForgotPasswordByUserIdConnector(
			viewModel viewModel: ForgotPasswordViewModel,
			anonymousUsername: String,
			anonymousPassword: String) -> ForgotPasswordBaseLiferayConnector {
		return ForgotPasswordUserIdLiferay70Connector(
			viewModel: viewModel,
			anonymousUsername: anonymousUsername,
			anonymousPassword: anonymousPassword)
	}

	public func createSignUpConnector(
			viewModel viewModel: SignUpViewModel,
			anonymousUsername: String,
			anonymousPassword: String) -> SignUpLiferayConnector {
		return Liferay70SignUpConnector(
			viewModel: viewModel,
			anonymousUsername: anonymousUsername,
			anonymousPassword: anonymousPassword)
	}

	public func createUpdateCurrentUserConnector(
			viewModel viewModel: SignUpViewModel) -> UpdateCurrentUserLiferayConnector {
		return Liferay70UpdateCurrentUserConnector(viewModel: viewModel)
	}

	public func createUploadUserPortraitConnector(
			userId userId: Int64,
			image: UIImage) -> UploadUserPortraitLiferayConnector {
		return Liferay70UploadUserPortraitConnector(
			userId: userId,
			image: image)
	}

	public func createAssetListPageConnector(
			startRow startRow: Int,
			endRow: Int,
			computeRowCount: Bool) -> AssetListPageLiferayConnector {
		return Liferay70AssetListPageConnector(
			startRow: startRow,
			endRow: endRow,
			computeRowCount: computeRowCount)
	}

	public func createWebContentListPageConnector(
			groupId groupId: Int64,
			folderId: Int64,
			startRow: Int,
			endRow: Int,
			computeRowCount: Bool) -> WebContentListPageLiferayConnector {
		return Liferay70WebContentListPageConnector(
			startRow: startRow,
			endRow: endRow,
			computeRowCount: computeRowCount,
			groupId: groupId,
			folderId: folderId)
	}

	public func createDDLListPageConnector(
			viewModel viewModel: DDLListViewModel,
			startRow: Int,
			endRow: Int,
			computeRowCount: Bool) -> DDLListPageLiferayConnector {
		return Liferay70DDLListPageConnector(
			viewModel: viewModel,
			startRow: startRow,
			endRow: endRow,
			computeRowCount: computeRowCount)
	}

	public func createWebContentLoadHtmlConnector(
			groupId groupId: Int64,
			articleId: String) -> WebContentLoadHtmlLiferayConnector {
		return Liferay70WebContentLoadHtmlConnector(
			groupId: groupId,
			articleId: articleId)
	}

	public func createWebContentLoadStructuredConnector(
			groupId groupId: Int64,
			articleId: String,
			structureId: Int64) -> WebContentLoadStructuredLiferayConnector {
		return Liferay70WebContentLoadStructuredConnector(
			groupId: groupId,
			articleId: articleId,
			structureId: structureId)
	}

	public func createDDLFormLoadConnector(structureId: Int64) -> DDLFormLoadLiferayConnector {
		return Liferay70DDLFormLoadConnector(structureId: structureId)
	}

	public func createDDLFormRecordLoadConnector(recordId: Int64) -> DDLFormRecordLoadLiferayConnector {
		return Liferay70DDLFormRecordLoadConnector(recordId: recordId)
	}

	public func createDDLFormSubmitConnector(
			values values: [String:AnyObject],
			viewModel: DDLFormViewModel?) -> DDLFormSubmitLiferayConnector {
		return Liferay70DDLFormSubmitConnector(
			values: values,
			viewModel: viewModel)
	}

	public func createDDLFormUploadConnector(
			document document: DDMFieldDocument,
			filePrefix: String,
			repositoryId: Int64,
			folderId: Int64,
			onProgress: DDLFormUploadLiferayConnector.OnProgress?) -> DDLFormUploadLiferayConnector {
		return Liferay70DDLFormUploadConnector(
			document: document,
			filePrefix: filePrefix,
			repositoryId: repositoryId,
			folderId: folderId,
			onProgress: onProgress)
	}
	
	public func createRatingLoadByEntryIdConnector(entryId entryId: Int64, ratingsGroupCount: Int32) -> RatingLoadByEntryIdLiferayConnector? {
		return Liferay70RatingLoadByEntryIdConnector(
			entryId: entryId,
			ratingsGroupCount: ratingsGroupCount)
	}
	
	public func createRatingLoadByClassPKConnector(classPK: Int64, className: String, ratingsGroupCount: Int32) -> RatingLoadByClassPKLiferayConnector? {
		return Liferay70RatingLoadByClassPKConnector(
			classPK: classPK,
			className: className,
			ratingsGroupCount: ratingsGroupCount)
	}
	
	public func createRatingUpdateConnector(classPK classPK: Int64, className: String, score: Double, ratingsGroupCount: Int32) -> RatingUpdateLiferayConnector? {
		return Liferay70RatingUpdateConnector(
			classPK: classPK,
			className: className,
			score: score,
			ratingsGroupCount: ratingsGroupCount)
	}
	
	public func createRatingDeleteConnector(classPK classPK: Int64, className: String, ratingsGroupCount: Int32) -> RatingDeleteLiferayConnector? {
		return Liferay70RatingDeleteConnector(
			classPK: classPK,
			className: className,
			ratingsGroupCount: ratingsGroupCount)
	}

	public func createCommentListPageConnector(
		groupId groupId: Int64,
				className: String,
				classPK: Int64,
				startRow: Int,
		        endRow: Int,
		        computeRowCount: Bool) -> CommentListPageLiferayConnector? {
		return Liferay70CommentListPageConnector(
			groupId: groupId,
			className: className,
			classPK: classPK,
			startRow: startRow,
			endRow: endRow,
			computeRowCount: computeRowCount)
	}

	public func createCommentAddConnector(groupId groupId: Int64, className: String, classPK: Int64, body: String?) -> CommentAddLiferayConnector? {
		return Liferay70CommentAddConnector(
			groupId: groupId,
			className: className,
			classPK: classPK,
			body: body)
	}

	public func createCommentLoadConnector(groupId groupId: Int64, commentId: Int64) -> CommentLoadLiferayConnector? {
		return Liferay70CommentLoadConnector(
			groupId: groupId,
			commentId: commentId)
	}

	public func createCommentDeleteConnector(commentId commentId: Int64) -> CommentDeleteLiferayConnector? {
		return Liferay70CommentDeleteConnector(
			commentId: commentId)
	}

	public func createCommentUpdateConnector(groupId groupId: Int64, className: String, classPK: Int64, commentId: Int64, body: String?) -> CommentUpdateLiferayConnector? {
		return Liferay70CommentUpdateConnector(
			groupId: groupId,
			className: className,
			classPK: classPK,
			commentId: commentId,
			body: body)
	}

}