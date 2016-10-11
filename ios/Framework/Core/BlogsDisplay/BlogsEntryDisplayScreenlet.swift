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


@objc public protocol BlogsEntryDisplayScreenletDelegate : BaseScreenletDelegate {

	optional func screenlet(screenlet: BlogsEntryDisplayScreenlet,
			onBlogEntryResponse blogEntry: BlogsEntry)

	optional func screenlet(screenlet: BlogsEntryDisplayScreenlet,
			onBlogEntryError error: NSError)
}


public class BlogsEntryDisplayScreenlet: BaseScreenlet {

	@IBInspectable public var assetEntryId: Int64 = 0

	@IBInspectable public var classPK: Int64 = 0

	@IBInspectable public var autoLoad: Bool = true

	@IBInspectable public var offlinePolicy: String? = CacheStrategyType.RemoteFirst.rawValue

	public class var supportedMimeTypes: [String] {
		return ["text/html"]
	}

	public var blogsEntry: BlogsEntry? {
		didSet {
			blogsEntryViewModel?.blogsEntry = self.blogsEntry
		}
	}
	
	public var blogsEntryDisplayDelegate: BlogsEntryDisplayScreenletDelegate? {
		return delegate as? BlogsEntryDisplayScreenletDelegate
	}

	public var blogsEntryViewModel: BlogsDisplayViewModel? {
		return screenletView as? BlogsDisplayViewModel
	}

	//MARK: Public methods

	override public func onShow() {
		if autoLoad {
			load()
		}
	}

	override public func createInteractor(name name: String, sender: AnyObject?) -> Interactor? {
		if isActionRunning(name) {
			cancelInteractorsForAction(name)
		}

		let interactor: LoadAssetInteractor

		if assetEntryId != 0 {
			interactor = LoadAssetInteractor(screenlet: self, assetEntryId: assetEntryId)
		}
		else {
			interactor = LoadAssetInteractor(
				screenlet: self,
				className: AssetClasses.getClassName(AssetClassNameKey_BlogsEntry)!,
				classPK: self.classPK)
		}

		interactor.cacheStrategy = CacheStrategyType(rawValue: self.offlinePolicy ?? "") ?? .RemoteFirst

		interactor.onSuccess = {
			if let resultAsset = interactor.asset {
				self.blogsEntry = BlogsEntry(attributes: resultAsset.attributes)
				self.blogsEntryViewModel?.blogsEntry = self.blogsEntry
				self.blogsEntryDisplayDelegate?.screenlet?(self, onBlogEntryResponse: self.blogsEntry!)
			}
			else {
				self.blogsEntryDisplayDelegate?.screenlet?(self, onBlogEntryError: NSError.errorWithCause(.InvalidServerResponse))
			}
		}

		interactor.onFailure = {
			self.blogsEntryDisplayDelegate?.screenlet?(self, onBlogEntryError: $0)
		}

		return interactor
	}

	public func load() -> Bool {
		return self.performDefaultAction()
	}
}
