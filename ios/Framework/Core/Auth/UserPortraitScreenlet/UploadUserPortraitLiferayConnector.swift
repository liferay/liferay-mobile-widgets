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
import UIKit
import LRMobileSDK


public class UploadUserPortraitLiferayConnector: ServerConnector {

	private let userId: Int64
	private var image: UIImage?

	var uploadResult: [String:AnyObject]?

	private let maxSize = 300 * 1024
	private var fileTooLarge = false


	public init(userId: Int64, image: UIImage) {
		self.userId = userId
		self.image = image

		super.init()
	}


	//MARK: ServerConnector

	override public func validateData() -> ValidationError? {
		let error = super.validateData()

		if error == nil {
			if self.image == nil {
				return ValidationError("userportrait-screenlet", "undefined-image")
			}
		}

		return error
	}

	override public func doRun(session session: LRSession) {
		if let imageBytes = reduceImage(self.image!, factor: 1) {
			self.image = nil
			uploadBytes(imageBytes, withSession: session)
		}
		else {
			fileTooLarge = true
			uploadResult = nil
			lastError = NSError.errorWithCause(
					.AbortedDueToPreconditions, message: "User portrait image is too large")
		}
	}


	//MARK: Private methods

	private func reduceImage(src: UIImage, factor: Double) -> NSData? {
		if (src.size.width < 100 && src.size.height < 100) || factor < 0.1  {
			return nil
		}

		if let imageReduced = src.resizeImage(toWidth: Int(src.size.width * CGFloat(factor))),
				imageBytes = UIImageJPEGRepresentation(imageReduced, 1) {

				return (imageBytes.length < maxSize)
					? imageBytes
					: reduceImage(src, factor: factor - 0.1)
		}

		return nil
	}

	public func uploadBytes(imageBytes: NSData, withSession session: LRSession) {
	}

}


public class Liferay62UploadUserPortraitConnector: UploadUserPortraitLiferayConnector {

	override public func uploadBytes(imageBytes: NSData, withSession session: LRSession) {
		let service = LRUserService_v62(session: session)

		do {
			let result = try service.updatePortraitWithUserId(self.userId,
				bytes: imageBytes)

			if let result = result as? [String:AnyObject] {
				uploadResult = result
				lastError = nil
			}
			else {
				lastError = NSError.errorWithCause(.InvalidServerResponse)
			}
		}
		catch let error as NSError {
			lastError = error
		}
	}
	
}

public class Liferay70UploadUserPortraitConnector: UploadUserPortraitLiferayConnector {

	override public func uploadBytes(imageBytes: NSData, withSession session: LRSession) {
		let service = LRUserService_v7(session: session)

		do {
			let result = try service.updatePortraitWithUserId(self.userId,
				bytes: imageBytes)

			if let result = result as? [String:AnyObject] {
				uploadResult = result
				lastError = nil
			}
			else {
				lastError = NSError.errorWithCause(.InvalidServerResponse)
			}
		}
		catch let error as NSError {
			lastError = error
		}
	}
	
}
