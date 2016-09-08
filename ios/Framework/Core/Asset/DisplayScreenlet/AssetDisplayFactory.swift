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


@objc public class AssetDisplayFactory: NSObject {

	public class func createScreenlet(frame: CGRect, asset: Asset) -> BaseScreenlet? {
		if asset.isAnyMimeType(ImageDisplayScreenlet.supportedMimeTypes) {
			return ImageDisplayScreenlet(frame: frame, themeName: nil)
		}
		else if asset.isAnyMimeType(VideoDisplayScreenlet.supportedMimeTypes) {
			return VideoDisplayScreenlet(frame: frame, themeName: nil)
		}
		else if asset.isAnyMimeType(AudioDisplayScreenlet.supportedMimeTypes) {
			return AudioDisplayScreenlet(frame: frame, themeName: nil)
		}
		else if asset.isAnyMimeType(PdfDisplayScreenlet.supportedMimeTypes) {
			return PdfDisplayScreenlet(frame: frame, themeName: nil)
		}
		else if asset.isAnyMimeType(BlogsEntryDisplayScreenlet.supportedMimeTypes) {
			return BlogsEntryDisplayScreenlet(frame: frame, themeName: nil)
		}

		return nil
	}

}
