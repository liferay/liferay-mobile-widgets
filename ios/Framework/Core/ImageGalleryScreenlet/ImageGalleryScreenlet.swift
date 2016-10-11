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
import Kingfisher

@objc public protocol ImageGalleryScreenletDelegate : BaseScreenletDelegate {

	optional func screenlet(screenlet: ImageGalleryScreenlet,
			onImageEntriesResponse imageEntries: [ImageEntry])

	optional func screenlet(screenlet: ImageGalleryScreenlet,
			onImageEntriesError error: NSError)

	optional func screenlet(screenlet: ImageGalleryScreenlet,
			onImageEntrySelected imageEntry: ImageEntry)

	optional func screenlet(screenlet: ImageGalleryScreenlet,
			onImageEntryDeleted imageEntry: ImageEntry)

	optional func screenlet(screenlet: ImageGalleryScreenlet,
			onImageEntryDeleteError error: NSError)

	optional func screenlet(screenlet: ImageGalleryScreenlet,
			onImageUploadStart imageEntryUpload: ImageEntryUpload)
							
	optional func screenlet(screenlet: ImageGalleryScreenlet,
			onImageUploadProgress imageEntryUpload: ImageEntryUpload,
			totalBytesSent: UInt64,
			totalBytesToSend: UInt64)

	optional func screenlet(screenlet: ImageGalleryScreenlet,
			onImageUploadError error: NSError)

	optional func screenlet(screenlet: ImageGalleryScreenlet,
			onImageUploaded image: ImageEntry)

	optional func screenlet(screenlet: ImageGalleryScreenlet,
			onImageUploadDetailViewCreated view: ImageUploadDetailViewBase) -> Bool
}


public class ImageGalleryScreenlet : BaseListScreenlet {

	public static let DeleteImageAction = "delete-image-action"
	public static let UploadImageAction = "upload-image-action"
	public static let EnqueueUploadAction = "enqueue-upload-action"

    @IBInspectable public var repositoryId: Int64 = -1
    @IBInspectable public var folderId: Int64 = -1
	@IBInspectable public var mimeTypes: String = ""

	@IBInspectable public var filePrefix: String = "gallery-"

	@IBInspectable public var offlinePolicy: String? = CacheStrategyType.RemoteFirst.rawValue {
		didSet {
			ImageCache.screensOfflinePolicy =
				offlinePolicy ?? CacheStrategyType.RemoteFirst.rawValue
		}
	}

	public var uploadDetailViewName = "ImageUploadDetailView"

	public let DefaultMimeTypes = ["image/png", "image/jpeg", "image/gif"]

	internal var uploadsQueue = [ImageEntryUpload]()
	internal var loadedOnce = false

	public var imageGalleryDelegate: ImageGalleryScreenletDelegate? {
		return delegate as? ImageGalleryScreenletDelegate
	}

	public var viewModel: ImageGalleryViewModel {
		return screenletView as! ImageGalleryViewModel
	}


	// MARK: Public methods

	public func cancelUploads() {
		uploadsQueue.removeAll()
		cancelInteractorsForAction(ImageGalleryScreenlet.UploadImageAction)
	}

	public func deleteImageCache() {
		KingfisherManager.sharedManager.cache.clearMemoryCache()
		KingfisherManager.sharedManager.cache.clearDiskCache()

		SessionContext.currentContext?.cacheManager.remove(
			collection: ScreenletName(self.dynamicType))
	}

	public func startMediaSelectorAndUpload() {
		if let viewController = presentingViewController {

			let takeNewPicture = LocalizedString("default",
					key: "userportrait-take-new-picture",
					obj: self)
			let chooseExisting = LocalizedString("default",
					key: "userportrait-choose-existing-picture",
					obj: self)

			let cancelText = LocalizedString("imagegallery-screenlet", key: "cancel", obj: self)

			let alert = MediaSelector(
					viewController: viewController,
					types: [
						.Camera: takeNewPicture,
						.Image: chooseExisting
					],
					cancelMessage: cancelText) { (image, _) in

				guard let image = image else {
					return
				}

				let imageUpload = ImageEntryUpload(image: image, title: "")
				self.showDetailUploadView(imageUpload)
			}
			alert.show()
		}
	}

	public func showDetailUploadView(imageUpload: ImageEntryUpload) {
		let detailUploadView = createImageUploadDetailViewFromNib()

		if let detailUploadView = detailUploadView {
			detailUploadView.image = imageUpload.image
			detailUploadView.screenlet = self

			let presented = imageGalleryDelegate?.screenlet?(
					self,
					onImageUploadDetailViewCreated: detailUploadView) ?? false

			if !presented {
				let defaultViewController =
						ImageUploadDetailViewController_default(imageUploadDetailview: detailUploadView)
				let navigationController = UINavigationController(rootViewController: defaultViewController)
				presentingViewController?.presentViewController(navigationController, animated: true) {}
			}
		}
	}

	public func deleteImageEntry(imageEntry: ImageEntry) {
		if offlinePolicy == CacheStrategyType.RemoteOnly.rawValue ||
				offlinePolicy == CacheStrategyType.RemoteFirst.rawValue {

			performAction(name: ImageGalleryScreenlet.DeleteImageAction, sender: imageEntry)
		}
		else {
			print("Error, delete only works on RemoteOnly or RemoteFirst mode")
		}
	}
	

	// MARK: BaseListScreenlet

	public override func onLoadPageError(page page: Int, error: NSError) {
		super.onLoadPageError(page: page, error: error)
		imageGalleryDelegate?.screenlet?(self, onImageEntriesError: error)
	}

	public override func onLoadPageResult(page page: Int, rows: [AnyObject], rowCount: Int) {
		super.onLoadPageResult(page: page, rows: rows, rowCount: rowCount)
		imageGalleryDelegate?.screenlet?(self, onImageEntriesResponse: rows as! [ImageEntry])
	}

	public override func onSelectedRow(row: AnyObject) {
		super.onSelectedRow(row)
		imageGalleryDelegate?.screenlet?(self, onImageEntrySelected: row as! ImageEntry)
	}


	// MARK: BaseScreenlet

	public override func onCreated() {
		super.onCreated()
		ImageCache.screensOfflinePolicy = offlinePolicy ?? CacheStrategyType.RemoteFirst.rawValue
	}

	public override func onShow() {
		// Don't reload the view if the picker is presented
		if !loadedOnce {
			loadedOnce = true
			super.onShow()
		}
	}

	public override func performAction(name name: String, sender: AnyObject?) -> Bool {
		if name == ImageGalleryScreenlet.EnqueueUploadAction {
			guard let uploadEntry = sender as? ImageEntryUpload else {
				return false
			}

			viewModel.onImageUploadEnqueued?(uploadEntry)

			if uploadsQueue.isEmpty {
				uploadsQueue.insert(uploadEntry, atIndex: 0)
				return super.performAction(
						name: ImageGalleryScreenlet.UploadImageAction,
						sender: sender)
			}
			else {
				uploadsQueue.insert(uploadEntry, atIndex: 0)
				return false
			}
		}

		return super.performAction(name: name, sender: sender)
	}

	public override func createInteractor(name name: String, sender: AnyObject?) -> Interactor? {
		switch name {
		case BaseListScreenlet.LoadInitialPageAction, BaseListScreenlet.LoadPageAction:
			return super.createInteractor(name: name, sender: sender)

		case ImageGalleryScreenlet.DeleteImageAction:
			let imageEntry = sender as! ImageEntry
			return createImageGalleryDeleteInteractor(imageEntry)

		case ImageGalleryScreenlet.UploadImageAction:
			let imageUpload = sender as! ImageEntryUpload
			return createImageUploadInteractor(imageUpload)

		default:
			return nil
		}
	}


	// MARK: Private methods

	internal func createImageGalleryDeleteInteractor(
			imageEntry: ImageEntry) -> ImageGalleryDeleteInteractor? {

		let index = viewModel.indexOf?(imageEntry: imageEntry) ?? -1

		if index != -1 {
			let page = pageFromRow(index)

			let interactor = ImageGalleryDeleteInteractor(
				screenlet: self,
				imageEntryId: imageEntry.imageEntryId,
				repositoryId: repositoryId,
				folderId: folderId,
				page: page)

			interactor.onSuccess = {
				self.imageGalleryDelegate?.screenlet?(self, onImageEntryDeleted: imageEntry)
				self.viewModel.onImageEntryDeleted?(imageEntry)
			}

			interactor.onFailure = {
				self.imageGalleryDelegate?.screenlet?(self, onImageEntryDeleteError: $0)
			}

			interactor.cacheStrategy = CacheStrategyType(rawValue: self.offlinePolicy ?? "") ?? .RemoteFirst

			return interactor
		}
		else {
			print("ERROR Image entry does not exist")

			return nil
		}
	}

	public override func createPageLoadInteractor(
			page page: Int,
			computeRowCount: Bool) -> BaseListPageLoadInteractor {

		let finalMimeTypes = mimeTypes.isEmpty ? DefaultMimeTypes : parseMimeTypes(mimeTypes)

		let interactor = ImageGalleryLoadInteractor(
				screenlet: self,
				page: page,
				computeRowCount: computeRowCount,
				repositoryId: repositoryId,
				folderId: folderId,
				mimeTypes: finalMimeTypes)

		interactor.cacheStrategy =
			CacheStrategyType(rawValue: self.offlinePolicy ?? "") ?? .RemoteFirst
		
		return interactor
	}

	internal func createImageUploadInteractor(
			imageUpload: ImageEntryUpload) -> ImageGalleryUploadInteractor {

		let rowCount = viewModel.totalEntries
		let page = pageFromRow(rowCount)

		let interactor = ImageGalleryUploadInteractor(
				screenlet: self,
				imageUpload: imageUpload,
				repositoryId: repositoryId,
				folderId: folderId,
				page: page) { (title, totalBytesSent, totalBytesToSend) in

			self.viewModel.onImageUploadProgress?(
					totalBytesSent,
					bytesToSend: totalBytesToSend,
					imageEntryUpload: imageUpload)

			self.imageGalleryDelegate?.screenlet?(
					self, onImageUploadProgress: imageUpload,
					totalBytesSent: totalBytesSent,
					totalBytesToSend: totalBytesToSend)
		}

		interactor.onSuccess = {
			guard let result = interactor.result else {
				return
			}

			let imageEntry = ImageEntry(attributes: result)
			imageEntry.image = imageUpload.thumbnail

			self.imageGalleryDelegate?.screenlet?(self, onImageUploaded: imageEntry)
			self.viewModel.onImageUploaded?(imageEntry)
			self.startNextUploadIfExist()
		}

		interactor.onFailure = {
			if $0.code == ScreensErrorCause.Cancelled.rawValue {
				self.uploadsQueue.removeAll()
			}
			else {
				self.viewModel.onImageUploadError?(imageUpload, error: $0)
				self.startNextUploadIfExist()
			}

			self.imageGalleryDelegate?.screenlet?(self, onImageUploadError: $0)
		}

		interactor.cacheStrategy =
			CacheStrategyType(rawValue: self.offlinePolicy ?? "") ?? .RemoteFirst

		self.imageGalleryDelegate?.screenlet?(self, onImageUploadStart: imageUpload)

		return interactor
	}

	internal func startNextUploadIfExist() {
		uploadsQueue.popLast()

		if !uploadsQueue.isEmpty {
			performAction(name: ImageGalleryScreenlet.UploadImageAction, sender: uploadsQueue.last)
		}
	}

	internal func createImageUploadDetailViewFromNib() -> ImageUploadDetailViewBase? {

		if let foundView = NSBundle.viewForThemeOrDefault(
				name: uploadDetailViewName,
				themeName: themeName ?? BaseScreenlet.DefaultThemeName,
				currentClass: self.dynamicType) as? ImageUploadDetailViewBase {

			return foundView
		}

		print("ERROR: Xib file doesn't found for \(uploadDetailViewName) and theme '\(themeName)'\n")
		
		return nil
	}


	internal func parseMimeTypes(mimeTypes: String) -> [String] {
		return mimeTypes.characters.split(",").map(String.init)
	}

}