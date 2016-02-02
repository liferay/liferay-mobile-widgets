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

#import "LRBaseService.h"

/**
 * @author Bruno Farache
 */
@interface LRDdlrecordversionService_v70 : LRBaseService

- (NSDictionary *)getRecordVersionWithRecordId:(long long)recordId version:(NSString *)version error:(NSError **)error;
- (NSDictionary *)getRecordVersionWithRecordVersionId:(long long)recordVersionId error:(NSError **)error;
- (NSArray *)getRecordVersionsWithRecordId:(long long)recordId start:(int)start end:(int)end orderByComparator:(LRJSONObjectWrapper *)orderByComparator error:(NSError **)error;
- (NSNumber *)getRecordVersionsCountWithRecordId:(long long)recordId error:(NSError **)error;

@end