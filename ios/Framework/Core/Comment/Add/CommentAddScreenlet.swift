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


@objc public protocol CommentAddScreenletDelegate : BaseScreenletDelegate {

	optional func screenlet(screenlet: CommentAddScreenlet,
	                        onCommentAdded comment: Comment)

	optional func screenlet(screenlet: CommentAddScreenlet,
	                        onAddCommentError error: NSError)

}


@IBDesignable public class CommentAddScreenlet: BaseScreenlet {

	@IBInspectable public var groupId: Int64 = 0

	@IBInspectable public var className: String = ""

	@IBInspectable public var classPK: Int64 = 0

	public var commentAddDelegate: CommentAddScreenletDelegate? {
		return delegate as? CommentAddScreenletDelegate
	}

	public var viewModel: CommentAddViewModel? {
		return screenletView as? CommentAddViewModel
	}

	//MARK: Public methods

	override public func createInteractor(name name: String, sender: AnyObject?) -> Interactor? {
		let interactor = CommentAddInteractor(
			screenlet: self,
			groupId: self.groupId,
			className: self.className,
			classPK: self.classPK,
			body: self.viewModel?.body)

		interactor.onSuccess = {
			if let resultComment = interactor.resultComment {
				self.commentAddDelegate?.screenlet?(self, onCommentAdded: resultComment)
				self.viewModel?.body = ""
			}
			else {
				self.commentAddDelegate?.screenlet?(self,
					onAddCommentError: NSError.errorWithCause(.InvalidServerResponse))
			}
		}

		interactor.onFailure = {
			self.commentAddDelegate?.screenlet?(self, onAddCommentError: $0)
		}
		
		return interactor
	}
	
}
