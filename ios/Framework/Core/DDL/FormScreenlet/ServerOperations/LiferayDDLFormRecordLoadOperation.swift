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


public class LiferayDDLFormRecordLoadOperation: ServerOperation {

	public var recordId: Int64

	public var resultRecordData: [String:AnyObject]?
	public var resultRecordAttributes: [String:AnyObject]?
	public var resultRecordId: Int64?


	public init(recordId: Int64) {
		self.recordId = recordId

		super.init()
	}


	//MARK: ServerOperation

	override public func doRun(session session: LRSession) {
		let service = LRScreensddlrecordService_v62(session: session)

		resultRecordData = nil
		resultRecordAttributes = nil
		resultRecordId = nil
		lastError = nil

		let recordDic = service.getDdlRecordWithDdlRecordId(recordId,
				locale: NSLocale.currentLocaleString,
				error: &lastError)

		if lastError == nil {
			if let resultData = recordDic["modelValues"] as? [String:AnyObject],
					resultAttributes = recordDic["modelAttributes"] as? [String:AnyObject] {
				resultRecordData = resultData
				resultRecordAttributes = resultAttributes
				resultRecordId = recordId
			}
			else if let resultData = recordDic as? [String:AnyObject] {
				// backwards compat: plugins v1.1.0 and previous (pre LPS-58800)
				resultRecordData = resultData
				resultRecordId = recordId
			}
			else {
				lastError = NSError.errorWithCause(.InvalidServerResponse)
			}
		}
	}

}
