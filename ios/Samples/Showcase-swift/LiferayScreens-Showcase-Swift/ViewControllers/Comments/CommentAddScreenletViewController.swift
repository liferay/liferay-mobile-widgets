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

class CommentAddScreenletViewController: UIViewController, CommentAddScreenletDelegate {

	
	//MARK: IBOutlet
	
	@IBOutlet weak var screenlet: CommentAddScreenlet? {
		didSet {
			screenlet?.delegate = self
			screenlet?.className =
				LiferayServerContext.stringPropertyForKey("commentScreenletClassName")
			screenlet?.classPK =
				LiferayServerContext.longPropertyForKey("commentScreenletClassPK")
		}
	}
	
	
	//MARK: CommentAddScreenletDelegate

	func screenlet(screenlet: CommentAddScreenlet, onCommentAdded comment: Comment) {
		LiferayLogger.logDelegateMessage(args: comment)
	}
	
	func screenlet(screenlet: CommentAddScreenlet, onAddCommentError error: NSError) {
		LiferayLogger.logDelegateMessage(args: error)
	}

}
