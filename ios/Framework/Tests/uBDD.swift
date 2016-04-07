/*
The MIT License (MIT)

Copyright (c) 2015 Jose Manuel Navarro

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
import UIKit
import XCTest


private var currentIndentationLevel = 1

private var lastDoneEvent: (name: String, result: AnyObject?)?


public enum Action {
	case TestNow
	case Pending
	case Skip
	case TestAndWaitFor(String, XCTestCase)
}

public func given(str: String, code: Void -> Void) {
	doPrint("\(currentIndentation())\(currentIcons().given) Given \(str)")
	code()
}

public func when(str: String, code: Void -> Void) {
	lastDoneEvent = nil

	doPrint("\(currentIndentation())\(currentIcons().when) When \(str)")
	code()
}

public func then(str: String, code: Void -> Void) {
	then(str, code: code, action: .TestNow)
}

public func then(str: String, code: Void -> Void, action: Action) {
	let icons = currentIcons()
	let indentation = currentIndentation()

	doPrint("\(indentation)\(icons.then) Then \(str)")

	switch action {
		case .TestAndWaitFor:
			doPrint("ERROR: TestAndWaitFor is not supported with given.when.then. Use given.when.eventually instead")
		case .Skip:
			doPrint("\(indentation)\(icons.skipped) SKIPPED")
		case .Pending:
			doPrint("\(indentation)\(icons.pending) PENDING")
		case .TestNow:
			do {
				try ObjCTryCatch.catchBlock {
					code()
				}
				doPrint("\(indentation)\(icons.passed) PASSED")
			}
			catch {
				doPrint("\(indentation)\(icons.failed) FAILED")
			}
	}
}

public func eventually(str: String, _ code: AnyObject? -> Void, _ action: Action) {
	let icons = currentIcons()
	let indentation = currentIndentation()

	switch action {
		case .TestAndWaitFor(let notificationName, let testCase):
			if lastDoneEvent?.name == notificationName {
				// already called "done"
				do {
					try ObjCTryCatch.catchBlock {
						code(lastDoneEvent?.result)
					}
					doPrint("\(indentation)\(icons.passed) PASSED")
				}
				catch {
					doPrint("\(indentation)\(icons.failed) FAILED")
				}
			}
			else {
				let expectation = testCase.expectationWithDescription("\(str)-\(NSDate().timeIntervalSince1970)")

				var signaled = false

				let observer = NSNotificationCenter.defaultCenter().addObserverForName(notificationName,
						object: nil,
						// why is this working using main queue?
						queue: NSOperationQueue.mainQueue(),
						usingBlock: { notif in

					do {
						try ObjCTryCatch.catchBlock {
							code(notif.object)
						}
					}
					catch {
						doPrint("\(indentation)\(icons.failed) FAILED")
					}

					signaled = true
					expectation.fulfill()
				})

				do {
					doPrint("\(indentation)\(icons.eventually) Eventually \(str)")

					try ObjCTryCatch.catchBlock {
						testCase.waitForExpectationsWithTimeout(5, handler: nil)
					}

					if signaled {
						doPrint("\(indentation)\(icons.passed) PASSED")
					}
					else {
						doPrint("\(indentation)\(icons.failed) FAILED (timeout)")
					}
				}
				catch {
					doPrint("\(indentation)\(icons.failed) FAILED")
				}

				NSNotificationCenter.defaultCenter().removeObserver(observer)
			}

			lastDoneEvent = nil
		default:
			then(str, code: {
				code(nil)
			}, action: action)
	}
}

public func done(notificationName: String, withResult result: AnyObject?) {
	lastDoneEvent = (notificationName, result)
	NSNotificationCenter.defaultCenter().postNotificationName(notificationName, object: result)
}

public func scenario(scenario: String, code:Void->Void) {
	doPrint("\(currentIndentation())\(currentIcons().scenario) \(scenario)")

	currentIndentationLevel += 1
	code()
	currentIndentationLevel -= 1
}

public func with(text: String, code: Void -> Void) {
	withSugar("with", text: text, level: 1, code: code)
}

public func that(text: String, code: Void -> Void) {
	withSugar("that", text: text, level: 1, code: code)
}

public func and(text: String, code: Void -> Void) {
	withSugar("and", text: text, level: 1, code: code)
}

public func but(text: String, code: Void -> Void) {
	withSugar("but", text: text, level: 1, code: code)
}

public func it(text: String, code: Void -> Void) {
	withSugar("it", text: text, level: 1, code: code)
}

public func perform(text: String, code: Void -> Void) {
	withSugar("perform", text: text, level: 1, code: code)
}

private func withSugar(sugar: String, text: String, level: Int, code: Void -> Void) {
	doPrint("\(indentation(currentIndentationLevel + level))\(currentIcons().secondLevel) \(sugar) \(text)")
	code()
}

private func doPrint(str: String) {
	// Xcode 6.3 (6D570) hangs using Swift's print.
	// Use NSLog as workaround
	// NSLog("%@", str)
	print(str)
}

public func assertThat(text: String, code: () -> ()) {
	do {
		try ObjCTryCatch.catchBlock {
			code()
		}

		// Since Xcode 7.3 (7D175), failed XCTAsserts inside assertThat don't raise any exception.
		// Because of that, the test fails, but the assetThat line appears as success

		doPrint("\(indentation(currentIndentationLevel + 1))\(currentIcons().assertPassed) assert that \(text)")
	}
	catch let error as NSError {
		doPrint("\(indentation(currentIndentationLevel + 1))\(currentIcons().assertFailed) assert that \(text)")
		let exception = NSException(
			name: String(error.code),
			reason: error.description,
			userInfo: error.userInfo)
		ObjCTryCatch.throwException(exception)
	}
}

private func currentIndentation() -> String {
	return indentation(currentIndentationLevel)
}

private func indentation(level:Int) -> String {
	return String(count: level, repeatedValue:Character("\t"))
}

public var currentIcons = simpleIcons

public typealias Icons =
	(passed: Character,
	failed: Character,
	given: Character,
	when: Character,
	then: Character,
	eventually: Character,
	timeout: Character,
	skipped: Character,
	pending: Character,
	scenario: Character,
	secondLevel: Character,
	assertPassed: Character,
	assertFailed: Character)


func asciiIcons() -> Icons {
	return (
		passed: "v",
		failed: "x",
		given: "-",
		when: "-",
		then: "-",
		eventually: "-",
		timeout: "x",
		skipped: "!",
		pending: "?",
		scenario: "*",
		secondLevel: "·",
		assertPassed: "v",
		assertFailed: "x")
}

func simpleIcons() -> Icons {
	return (
		passed: "✓",
		failed: "✘",
		given: "-",
		when: "-",
		then: "-",
		eventually: "-",
		timeout: "✘",
		skipped: "!",
		pending: "?",
		scenario: "*",
		secondLevel: "·",
		assertPassed: "✓",
		assertFailed: "✘")
}

func emojiIcons() -> Icons {
	return (
		passed: "✅",
		failed: "❌",
		given: "➜",
		when: "➜",
		then: "➜",
		eventually: "➜",
		timeout: "❌",
		skipped: "☔️",
		pending: "⚠️",
		scenario: "➜",
		secondLevel: "✓",
		assertPassed: "✓",
		assertFailed: "✘")
}

func funnyEmojiIcons() -> Icons {
	return (
		passed: "👍",
		failed: "💩",
		given: "👉",
		when: "👉",
		then: "👉",
		eventually: "👉",
		timeout: "💤",
		skipped: "🔕",
		pending: "🔜",
		scenario: "👉",
		secondLevel: "📍",
		assertPassed: "😄",
		assertFailed: "😱")
}
