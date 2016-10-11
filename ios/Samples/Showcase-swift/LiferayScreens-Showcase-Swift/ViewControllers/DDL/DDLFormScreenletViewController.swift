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


class DDLFormScreenletViewController: UIViewController, DDLFormScreenletDelegate {

	@IBOutlet var screenlet: DDLFormScreenlet? {
		didSet {
			screenlet?.delegate = self
			screenlet?.recordSetId = LiferayServerContext.longPropertyForKey("ddlRecordSetId")
			screenlet?.structureId = LiferayServerContext.longPropertyForKey("ddlStructureId")
		}
	}
	@IBOutlet var recordIdTextField: UITextField? {
		didSet {
			recordIdTextField?.text =
				String(LiferayServerContext.longPropertyForKey("ddlRecordId"))
		}
	}

	@IBAction func loadForm(sender: AnyObject) {
		if let id = recordIdTextField?.text, recordId = Int64(id) {
			screenlet?.recordId = recordId
			recordIdTextField?.resignFirstResponder()
			screenlet?.loadRecord()
		}
		else {
			screenlet?.loadForm()
		}
	}


	//MARK: DDLFormScreenletDelegate

	func screenlet(screenlet: DDLFormScreenlet, onFormLoaded record: DDLRecord) {
		LiferayLogger.logDelegateMessage(args: record)
	}
	
	func screenlet(screenlet: DDLFormScreenlet, onFormLoadError error: NSError) {
		LiferayLogger.logDelegateMessage(args: error)
	}
	
	func screenlet(screenlet: DDLFormScreenlet, onFormSubmitError error: NSError) {
		LiferayLogger.logDelegateMessage(args: error)
	}
	
	func screenlet(screenlet: DDLFormScreenlet, onRecordLoaded record: DDLRecord) {
		LiferayLogger.logDelegateMessage(args: record)
	}
	
	func screenlet(screenlet: DDLFormScreenlet, onRecordLoadError error: NSError) {
		LiferayLogger.logDelegateMessage(args: error)
	}
	
	func screenlet(screenlet: DDLFormScreenlet, onFormSubmitted record: DDLRecord) {
		LiferayLogger.logDelegateMessage(args: record)
	}
	
	func screenlet(screenlet: DDLFormScreenlet,
	               onDocumentFieldUploadStarted field: DDMFieldDocument) {
		LiferayLogger.logDelegateMessage(args: field)
	}
	
	func screenlet(screenlet: DDLFormScreenlet,
	               onDocumentField field: DDMFieldDocument,
	               uploadError error: NSError) {
		LiferayLogger.logDelegateMessage(args: error)
	}
	
	func screenlet(screenlet: DDLFormScreenlet,
	               onDocumentField field: DDMFieldDocument,
	               uploadResult result: [String : AnyObject]) {
		LiferayLogger.logDelegateMessage(args: field, result)
	}
	
	func screenlet(screenlet: DDLFormScreenlet,
	               onDocumentField field: DDMFieldDocument,
				   uploadedBytes bytes: UInt64,
				   totalBytes total: UInt64) {
		LiferayLogger.logDelegateMessage(args: field)
	}

}
