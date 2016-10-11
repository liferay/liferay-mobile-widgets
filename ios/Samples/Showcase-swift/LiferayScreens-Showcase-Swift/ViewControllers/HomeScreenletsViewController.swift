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

class HomeScreenletsViewController: UITableViewController {

	private let data: [Int:[String]] = [
			0: ["Auth Module", "LoginScreenlet", "SignUpScreenlet", "ForgotPasswordScreenlet"],
			1: ["DDL Module", "DDLListScreenlet", "DDLFormScreenlet"],
			2: ["Web Content Module", "WebContentDisplayScreenlet", "WebContentListScreenlet"],
			3: ["Assets", "AssetListScreenlet", "AssetDisplayScreenlet"],
			4: ["Comments", "CommentListScreenlet", "CommentDisplayScreenlet"],
			5: ["Others", "UserPortraitScreenlet", "RatingScreenlet", "ImageGalleryScreenlet", "BlogDisplayScreenlet", "ImageDisplayScreenlet"]
		]

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
		return data.count
	}

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
		let length = (data[section] ?? []).count
		return length == 0 ? length : length - 1
	}

    override func tableView(tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
		return (data[section] ?? [""])[0]
	}

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
		let cell = UITableViewCell(style: .Default, reuseIdentifier: "default-cell");

		if let record = data[indexPath.section] {
			cell.textLabel?.text = record[indexPath.row + 1]
		}

		return cell
	}

	override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
		if let record = data[indexPath.section] {
			self.performSegueWithIdentifier(record[indexPath.row + 1], sender: self)
		}
	}

}

