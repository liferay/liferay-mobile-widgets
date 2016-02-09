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

#import "LRScreensassetentryService_v70.h"

/**
 * @author Bruno Farache
 */
@implementation LRScreensassetentryService_v70

- (NSArray *)getAssetEntriesWithAssetEntryQuery:(LRJSONObjectWrapper *)assetEntryQuery locale:(NSString *)locale error:(NSError **)error {
	NSMutableDictionary *_params = [NSMutableDictionary dictionaryWithDictionary:@{
		@"locale": [self checkNull: locale]
	}];

	[self mangleWrapperWithParams:_params name:@"assetEntryQuery" className:@"com.liferay.portlet.asset.service.persistence.AssetEntryQuery" wrapper:assetEntryQuery];
	NSDictionary *_command = @{@"/screens.screensassetentry/get-asset-entries": _params};

	return (NSArray *)[self.session invoke:_command error:error];
}

- (NSArray *)getAssetEntriesWithCompanyId:(long long)companyId groupId:(long long)groupId portletItemName:(NSString *)portletItemName locale:(NSString *)locale max:(int)max error:(NSError **)error {
	NSMutableDictionary *_params = [NSMutableDictionary dictionaryWithDictionary:@{
		@"companyId": @(companyId),
		@"groupId": @(groupId),
		@"portletItemName": [self checkNull: portletItemName],
		@"locale": [self checkNull: locale],
		@"max": @(max)
	}];

	NSDictionary *_command = @{@"/screens.screensassetentry/get-asset-entries": _params};

	return (NSArray *)[self.session invoke:_command error:error];
}

@end