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
import XCTest


class DDLFieldBoolean_Tests: XCTestCase {

	private let spanishLocale = NSLocale(localeIdentifier: "es_ES")
	private let usLocale = NSLocale(localeIdentifier: "en_US")


	//MARK: ConvertFromString

	func test_ConvertFromString_ShouldReturnNil_WhenNilStringIsSupplied() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		XCTAssertNil(boolField.convert(fromString: nil))
	}

	func test_ConvertFromString_ShouldReturnBool_WhenTrueStringIsSupplied() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		let convertedValue:AnyObject? = boolField.convert(fromString: "true")

		XCTAssertNotNil(convertedValue)
		XCTAssertTrue(convertedValue is Bool)
		XCTAssertTrue(convertedValue as! Bool)
	}

	func test_ConvertFromString_ShouldReturnBool_WhenFalseStringIsSupplied() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		let convertedValue:AnyObject? = boolField.convert(fromString: "false")

		XCTAssertNotNil(convertedValue)
		XCTAssertTrue(convertedValue is Bool)
		XCTAssertFalse(convertedValue as! Bool)
	}


	//MARK: ConvertFromCurrentValue

	func test_ConvertFromCurrentValue_ShouldReturnNil_WhenNilIsSupplied() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		XCTAssertNil(boolField.convert(fromCurrentValue: nil))
	}

	func test_ConvertFromCurrentValue_ShouldReturnTrueString_WhenTrueIsSupplied() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		let convertedValue = boolField.convert(fromCurrentValue: true)

		XCTAssertNotNil(convertedValue)
		XCTAssertEqual("true", convertedValue!)
	}

	func test_ConvertFromCurrentValue_ShouldReturnFalseString_WhenFalseIsSupplied() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		let convertedValue = boolField.convert(fromCurrentValue: false)

		XCTAssertNotNil(convertedValue)
		XCTAssertEqual("false", convertedValue!)
	}


	//MARK CurrentValueAsString

	func test_CurrentValueAsString_ShouldReturnNil_WhenNoValueIsSupplied() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		XCTAssertNil(boolField.currentValueAsString)
	}

	func test_CurrentValueAsString_ShouldReturnTrueString_WhenTrueIsSupplied() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		boolField.currentValue = true

		XCTAssertNotNil(boolField.currentValueAsString)
		XCTAssertEqual("true", boolField.currentValueAsString!)
	}

	func test_CurrentValueAsString_ShouldReturnFalseString_WhenFalseIsSupplied() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		boolField.currentValue = false

		XCTAssertNotNil(boolField.currentValueAsString)
		XCTAssertEqual("false", boolField.currentValueAsString!)
	}


	//MARK CurrentValueAsLabel

	func test_CurrentValueAsLabel_ShouldReturnNil_WhenNoValueIsSupplied() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		XCTAssertNil(boolField.currentValueAsLabel)
	}

	func test_CurrentValueAsLabel_ShouldReturnTrueString_WhenTrueIsSupplied() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		boolField.currentValue = true

		XCTAssertNotNil(boolField.currentValueAsLabel)

		// We assume the simulator is configured in en_US
		XCTAssertEqual("Yes", boolField.currentValueAsLabel!)
	}

	func test_CurrentValueAsLabel_ShouldReturnFalseString_WhenFalseIsSupplied() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		boolField.currentValue = false

		XCTAssertNotNil(boolField.currentValueAsLabel)
		XCTAssertEqual("No", boolField.currentValueAsLabel!)
	}

	func test_CurrentValueAsLabel_ShouldBeNil_WhenSetInvalidLabel() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		boolField.currentValueAsLabel = "invalid_bool"

		XCTAssertNil(boolField.currentValue)
	}

	func test_CurrentValueAsLabel_ShouldBeTrue_WhenSetYesLabel() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		boolField.currentValueAsLabel = "Yes"

		XCTAssertNotNil(boolField.currentValue)
		XCTAssertTrue(boolField.currentValue as! Bool)
	}

	func test_CurrentValueAsLabel_ShouldBeFalse_WhenSetNoLabel() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		boolField.currentValueAsLabel = "No"

		XCTAssertNotNil(boolField.currentValue)
		XCTAssertFalse(boolField.currentValue as! Bool)
	}

	func test_CurrentValueAsLabel_ShouldBeTrue_WhenSetYesLabelWithCase() {
		let boolField = DDLFieldBoolean(attributes: [:], locale: usLocale)

		boolField.currentValueAsLabel = "yEs"

		XCTAssertNotNil(boolField.currentValue)
		XCTAssertTrue(boolField.currentValue as! Bool)
	}


	//MARK: Parse

	func test_Parse_ShouldExtractValues() {
		let json = "{\"availableLanguageIds\": [\"en_US\"]," +
			"\"defaultLanguageId\": \"en_US\"," +
			"\"fields\": [{" +
			"\"label\": {\"en_US\": \"A Boolean\"}," +
			"\"tip\": {\"en_US\": \"The tip\"}," +
			"\"predefinedValue\": {\"en_US\": true}," +
			"\"dataType\": \"boolean\"," +
			"\"indexType\": \"keyword\"," +
			"\"localizable\": true," +
			"\"name\": \"A_Boolean\"," +
			"\"readOnly\": false," +
			"\"repeatable\": true," +
			"\"required\": false," +
			"\"showLabel\": true," +
			"\"type\": \"checkbox\"}]}"

		let fields = DDLJSONParser().parse(json, locale: spanishLocale)

		XCTAssertTrue(fields != nil)
		XCTAssertEqual(1, fields!.count)
		XCTAssertTrue(fields![0] is DDLFieldBoolean)

		let booleanField = fields![0] as! DDLFieldBoolean

		XCTAssertEqual(DDLField.DataType.DDLBoolean, booleanField.dataType)
		XCTAssertEqual(DDLField.Editor.Checkbox, booleanField.editorType)
		XCTAssertEqual("A_Boolean", booleanField.name)
		XCTAssertEqual("A Boolean", booleanField.label)
		XCTAssertEqual("The tip", booleanField.tip)
		XCTAssertNotNil(booleanField.predefinedValue)
		XCTAssertTrue(booleanField.predefinedValue is Bool)
		XCTAssertTrue(booleanField.predefinedValue as! Bool)
		XCTAssertFalse(booleanField.readOnly)
		XCTAssertTrue(booleanField.repeatable)
		XCTAssertFalse(booleanField.required)
		XCTAssertTrue(booleanField.showLabel)
	}


	//MARK: Validate

	func test_Validate_ShouldFail_WhenRequiredValueIsNil() {
		let json = "{\"availableLanguageIds\": [\"en_US\"]," +
			"\"defaultLanguageId\": \"en_US\"," +
			"\"fields\": [{" +
			"\"label\": {\"en_US\": \"A Boolean\"}," +
			"\"dataType\": \"boolean\"," +
			"\"indexType\": \"keyword\"," +
			"\"localizable\": true," +
			"\"name\": \"A_Boolean\"," +
			"\"readOnly\": false," +
			"\"repeatable\": true," +
			"\"required\": true," +
			"\"showLabel\": true," +
			"\"type\": \"checkbox\"}]}"

		let fields = DDLJSONParser().parse(json, locale: spanishLocale)

		let booleanField = fields![0] as! DDLFieldBoolean

		XCTAssertTrue(booleanField.currentValue == nil)

		XCTAssertFalse(booleanField.validate())
	}

}
