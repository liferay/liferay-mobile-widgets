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


public class LiferayPaginationOperation: ServerOperation {

	public let startRow: Int
	public let endRow: Int
	public let computeRowCount: Bool

	public var resultPageContent: [[String:AnyObject]]?
	public var resultRowCount: Int?


	internal init(startRow: Int, endRow: Int, computeRowCount: Bool) {
		self.startRow = startRow
		self.endRow = endRow
		self.computeRowCount = computeRowCount

		super.init()
	}


	//MARK: ServerOperation

	override public func doRun(session session: LRSession) {
		let batchSession = LRBatchSession(session: session)

		resultPageContent = nil
		resultRowCount = nil
		lastError = nil

		doGetPageRowsOperation(session: batchSession, startRow: startRow, endRow: endRow)

		if batchSession.commands.count < 1 {
			lastError = NSError.errorWithCause(.AbortedDueToPreconditions, userInfo: nil)
			return
		}

		if computeRowCount {
			doGetRowCountOperation(session: batchSession)
		}

		let responses = batchSession.invoke(&lastError)

		if lastError == nil {
			if let entriesResponse = responses[0] as? [[String:AnyObject]] {
				let serverPageContent = entriesResponse
				var serverRowCount: Int?

				if responses.count > 1 {
					if let countResponse = responses[1] as? NSNumber {
						serverRowCount = countResponse.integerValue
					}
				}

				resultPageContent = serverPageContent
				resultRowCount = serverRowCount
			}
			else {
				lastError = NSError.errorWithCause(.InvalidServerResponse, userInfo: nil)
			}
		}
	}

	internal func doGetPageRowsOperation(session session: LRBatchSession, startRow: Int, endRow: Int) {
		fatalError("doGetPageRowsOperation must be overriden")
	}

	internal func doGetRowCountOperation(session session: LRBatchSession) {
		fatalError("doGetRowCountOperation must be overriden")
	}

}
