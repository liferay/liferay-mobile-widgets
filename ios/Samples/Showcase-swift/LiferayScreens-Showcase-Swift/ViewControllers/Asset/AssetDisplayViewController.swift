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
import LiferayScreens


class AssetDisplayViewController: UIViewController, AssetDisplayScreenletDelegate {
	
	
	//MARK: IBOutlet
	
	@IBOutlet var screenlet: AssetDisplayScreenlet! {
		didSet {
			screenlet.delegate = self
			screenlet.presentingViewController = self
			
			screenlet.assetEntryId = entryId ??
				LiferayServerContext.longPropertyForKey("assetDisplayEntryId")
		}
	}
	
	var entryId: Int64?
	
	
	//MARK: UIViewController
	
	override func viewDidLoad() {
		super.viewDidLoad()

		self.screenlet?.load()
	}
	
	
	//MARK: AssetDisplayScreenletDelegate
	
	func screenlet(screenlet: AssetDisplayScreenlet, onAssetResponse asset: Asset) {
		LiferayLogger.logDelegateMessage(args: asset)
	}

	func screenlet(screenlet: AssetDisplayScreenlet, onAssetError error: NSError) {
		LiferayLogger.logDelegateMessage(args: error)
	}

	func screenlet(screenlet: AssetDisplayScreenlet,
	               onConfigureScreenlet childScreenlet: BaseScreenlet?,
	               onAsset asset: Asset) {
		LiferayLogger.logDelegateMessage(args: childScreenlet, asset)
	}

	func screenlet(screenlet: AssetDisplayScreenlet, onAsset asset: Asset) -> UIView? {
		let keys = asset.attributes["object"]!.allKeys
		if keys.contains({$0 as? String == "user"}) {
			let userView = UserView()
			userView.user = User(attributes: asset.attributes)
			return userView
		}
		return nil
	}

}
