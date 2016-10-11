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


class WebContentDisplayScreenletViewController: UIViewController, WebContentDisplayScreenletDelegate {

	
	//MARK: IBOutlet
	
	@IBOutlet var screenlet: WebContentDisplayScreenlet! {
		didSet {
			screenlet.delegate = self
			screenlet.articleId = self.articleId ??
				LiferayServerContext.stringPropertyForKey("webContentDisplayArticleId")
		}
	}
	
	var articleId: String?


	//MARK: WebContentDisplayScreenletDelegate

	func screenlet(screenlet: WebContentDisplayScreenlet,
			onWebContentResponse html: String ) -> String? {
		LiferayLogger.logDelegateMessage(args: html)
		return nil
	}

	func screenlet(screenlet: WebContentDisplayScreenlet,
		   onRecordContentResponse record: DDLRecord) {
		LiferayLogger.logDelegateMessage(args: record)
	}

	func screenlet(screenlet: WebContentDisplayScreenlet,
			onWebContentError error: NSError) {
		LiferayLogger.logDelegateMessage(args: error)
	}
	
	
	//MARK: UIViewController
	
	override func viewWillDisappear(animated: Bool) {
		super.viewWillDisappear(animated)
		
		articleId = nil
	}

}
