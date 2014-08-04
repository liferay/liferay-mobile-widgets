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
import Foundation

extension LRSession {

    
    // CLASS METHODS
    
	
	public class func removeStoredCredential() {
		let credentialTuple = credentialForServer(LiferayContext.instance.server)

		if let credential = credentialTuple.0 {
			NSURLCredentialStorage.sharedCredentialStorage().removeCredential(
				credential, forProtectionSpace:credentialTuple.1)
		}
	}

	public class func sessionFromStoredCredential() -> LRSession? {
		let credentialTuple = credentialForServer(LiferayContext.instance.server)
        
		if let credential = credentialTuple.0 {
			return LRSession(server:LiferayContext.instance.server, username:credential.user, password:credential.password)
		}
        
		return nil
    }
    

    // INSTANCE METHODS

    
	public func storeCredential() -> Bool {
		var success = false

		//FIXME
		// Compiler crash with compound if statement: if self.server && self.username && self.password {
		// "While emitting IR SIL function @_TFCSo9LRSession5storefS_FT_T_ for 'store' at LRSession+storage.swift:36:2"
		if self.username {
			if self.password {
				let protectionSpace = LRSession.protectionSpaceForServer(LiferayContext.instance.server)

				let credential = NSURLCredential(user:self.username!, password:self.password!,
					persistence: NSURLCredentialPersistence.Permanent)

				NSURLCredentialStorage.sharedCredentialStorage().setCredential(
					credential, forProtectionSpace:protectionSpace)

				success = true
			}
		}

		return success
	}


	// PRIVATE METHODS


	private class func credentialForServer(server:String) -> (NSURLCredential?, NSURLProtectionSpace) {
		let protectionSpace = protectionSpaceForServer(server)

		let credentialDict =
			NSURLCredentialStorage.sharedCredentialStorage().credentialsForProtectionSpace(protectionSpace)
				as NSDictionary?
		
		if let credentialDictValue = credentialDict {
			let username = credentialDictValue.keyEnumerator().nextObject() as NSString
			
			return (credentialDictValue.objectForKey(username) as? NSURLCredential, protectionSpace)
		}
		else {
			return (nil, protectionSpace)
		}
	}

	private class func protectionSpaceForServer(server:String) -> NSURLProtectionSpace {
		let url = NSURL(string: server)

		return NSURLProtectionSpace(host:url.host, port:url.port.integerValue, `protocol`:url.scheme, realm:nil,
			authenticationMethod:NSURLAuthenticationMethodHTTPDigest)
	}

}