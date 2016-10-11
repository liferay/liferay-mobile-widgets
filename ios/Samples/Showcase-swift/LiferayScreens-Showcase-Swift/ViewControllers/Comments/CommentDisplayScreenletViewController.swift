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

class CommentDisplayScreenletViewController: UIViewController, CommentDisplayScreenletDelegate {

	
	//MARK: IBOutlet
	
	@IBOutlet weak var screenlet: CommentDisplayScreenlet? {
		didSet {
			screenlet?.delegate = self
		}
	}
	@IBOutlet weak var commentIdText: UITextField?
	
	
	//MARK: IBAction

	@IBAction func loadComment(sender: AnyObject) {
		if let commentId = Int(commentIdText?.text ?? "") {
			screenlet?.commentId = Int64(commentId)
			screenlet?.load()
		}
	}
	
	
	//MARK: CommentDisplayScreenletDelegate

	func screenlet(screenlet: CommentDisplayScreenlet, onCommentLoaded comment: Comment) {
		LiferayLogger.logDelegateMessage(args: comment)
		screenlet.hidden = false
	}

	func screenlet(screenlet: CommentDisplayScreenlet, onLoadCommentError error: NSError) {
		LiferayLogger.logDelegateMessage(args: error)
		screenlet.hidden = true
	}

	func screenlet(screenlet: CommentDisplayScreenlet, onCommentDeleted comment: Comment) {
		LiferayLogger.logDelegateMessage(args: comment)
		screenlet.hidden = true
	}

	func screenlet(screenlet: CommentDisplayScreenlet, onDeleteComment comment: Comment,
			onError error: NSError) {
		LiferayLogger.logDelegateMessage(args: error)
	}

	func screenlet(screenlet: CommentDisplayScreenlet, onCommentUpdated comment: Comment) {
		LiferayLogger.logDelegateMessage(args: comment)
	}
	
	func screenlet(screenlet: CommentDisplayScreenlet, onUpdateComment comment: Comment,
			onError error: NSError) {
		LiferayLogger.logDelegateMessage(args: error)
	}

}
