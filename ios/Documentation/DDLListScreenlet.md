# DDLListScreenlet for iOS

## Requirements

- XCode 6.3 or above
- iOS 8 SDK
- Liferay Portal 6.2 CE or EE
- [Liferay Screens' Compatibility Plugin](http://www.liferay.com/es/marketplace/-/mp/application/54365664)

## Compatibility

- iOS 7 and above

## Features

The `DDLListScreenlet` enables the following features:

- Shows a scrollable collection of DDL records.
- Implements [fluent pagination](http://www.iosnomad.com/blog/2014/4/21/fluent-pagination) with configurable page size.
- Allows filtering of records by creator.
- Supports i18n in record values.

## Module

- DDL

## Themes

- The Default theme uses a standard `UITableView` to show the scrollable list. Other themes may use a different component, such as `UICollectionView` or others, to show the items.

![The DDLList screenlet using the Default theme.](Images/ddllist.png)

## Portal Configuration

Dynamic Data Lists (DDL) and Data Types should be properly configured in the portal. For more details, please refer to the Liferay User Guide sections [Defining Data Types](https://dev.liferay.com/discover/portal/-/knowledge_base/6-2/building-a-list-platform-in-liferay-and-defining-data-) and [Creating Data Lists](https://dev.liferay.com/discover/portal/-/knowledge_base/6-2/creating-data-lists).

## Attributes

| Attribute | Data type | Explanation |
|-----------|-----------|-------------| 
| `autoLoad` | `boolean` | Whether the list loads when it's presented on the screen. The default value is `true`. |
| `refreshControl` | `boolean` | Whether a standard [UIRefreshControl](https://developer.apple.com/library/ios/documentation/UIKit/Reference/UIRefreshControl_class/) is shown when the user performs the pull to refresh gesture. The default value is `true`. |
| `firstPageSize` | `number` | The number of items retrieved from the server for display on the first page. The default value is `50`. |
| `pageSize` | `number` | The number of items retrieved from the server for display on the second and subsequent pages. The default value is `25`. |
| `recordSetId` | `number` | The ID of the DDL being called. To find the IDs for your DDLs, click *Admin* &rarr; *Content* from the Dockbar. Then click *Dynamic Data Lists*. Each DDL's ID is in the table's ID column. |
| `userId` | `number` | The ID of the user to filter records on. Records aren't filtered if the `userId` is `0`. The default value is `0`. |
| `labelFields` | `string` | The comma separated names of the DDL fields to show. Refer to the list's data definition to find the field names. To do so, click *Admin* &rarr; *Content* from the Dockbar. Then click *Dynamic Data Lists* and click the *Manage Data Definitions* button. You can view the fields by clicking on any of the data definitions in the table that appears. Note that the appearance of these values depends on the theme selected by the user. |

## Methods

| Method | Return | Explanation |
|-----------|-----------|-------------| 
|  `loadList()` | `boolean` | Starts the request to load the list of records. The list is shown when the response is received. This method returns `true` if the request is sent. |

## Delegate

The `DDLListScreenlet` delegates some events in an object that conforms to the `DDLListScreenletDelegate` protocol. This protocol lets you implement the following methods:

- `- screenlet:onDDLListResponseRecords:`: Called when a page of contents is received. Note that this method may be called more than once; once for each retrieved page.
- `- screenlet:onDDLListError:`: Called when an error occurs in the process. The `NSError` object describes the error.
- `- screenlet:onDDLSelectedRecord:`: Called when an item in the list is selected.

