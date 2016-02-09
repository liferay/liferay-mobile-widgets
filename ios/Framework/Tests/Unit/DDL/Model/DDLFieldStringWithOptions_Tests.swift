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


class DDLFieldStringWithOptions_Tests: XCTestCase {

	private let spanishLocale = NSLocale(localeIdentifier: "es_ES")


	//MARK: Parse

	func test_Parse_ShouldExtractValues() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		XCTAssertTrue(fields != nil)
		XCTAssertEqual(1, fields!.count)
		XCTAssertTrue(fields![0] is DDLFieldStringWithOptions)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		XCTAssertEqual(DDLField.DataType.DDLString, stringField.dataType)
		XCTAssertEqual(DDLField.Editor.Select, stringField.editorType)
		XCTAssertEqual("A_Select", stringField.name)
		XCTAssertEqual("A Select", stringField.label)
		XCTAssertTrue(stringField.multiple)
		XCTAssertFalse(stringField.readOnly)
		XCTAssertTrue(stringField.repeatable)
		XCTAssertTrue(stringField.required)
		XCTAssertTrue(stringField.showLabel)
	}

	func test_Parse_ShouldExtractOptions() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		XCTAssertEqual(3, stringField.options.count)

		var option = stringField.options[0]

		XCTAssertEqual("value 1", option.value)
		XCTAssertEqual("Option 1", option.label)

		option = stringField.options[1]

		XCTAssertEqual("value 2", option.value)
		XCTAssertEqual("Option 2", option.label)

		option = stringField.options[2]

		XCTAssertEqual("value 3", option.value)
		XCTAssertEqual("Option 3", option.label)
	}

	func test_Parse_ShouldExtractPredefinedOptions() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		XCTAssertTrue(stringField.predefinedValue is [DDLFieldStringWithOptions.Option])
		let predefinedOptions = stringField.predefinedValue as! [DDLFieldStringWithOptions.Option]

		//FIXME only support one predefined value
		XCTAssertEqual(1, predefinedOptions.count)

		let predefinedOption = predefinedOptions[0]

		XCTAssertEqual("value 1", predefinedOption.value)
		XCTAssertEqual("Option 1", predefinedOption.label)
	}


	//MARK: CurrentValue

	func test_CurrentValue_ShouldBeTheSameAsPredefinedValue_WhenTheParsingIsDone() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		XCTAssertTrue(stringField.predefinedValue is [DDLFieldStringWithOptions.Option])
		let predefinedOptions = stringField.predefinedValue as! [DDLFieldStringWithOptions.Option]

		XCTAssertTrue(stringField.currentValue is [DDLFieldStringWithOptions.Option])
		let currentOptions = stringField.currentValue as! [DDLFieldStringWithOptions.Option]

		XCTAssertEqual(currentOptions.count, predefinedOptions.count)

		for (index,option) in currentOptions.enumerate() {
			let predefinedOption = predefinedOptions[index]

			XCTAssertEqual(option.label, predefinedOption.label)
			XCTAssertEqual(option.value, predefinedOption.value)
		}
	}

	func test_CurrentValue_ShouldBeChanged_AfterChangedToExistingOptionLabel() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValue = "Option 3"

		XCTAssertTrue(stringField.currentValue is [DDLFieldStringWithOptions.Option])
		let currentOptions = stringField.currentValue as! [DDLFieldStringWithOptions.Option]

		XCTAssertEqual(1, currentOptions.count)

		XCTAssertEqual("Option 3", currentOptions.first!.label)
		XCTAssertEqual("value 3", currentOptions.first!.value)
	}

	func test_CurrentValue_ShouldBeEmpty_AfterChangedToNonExistingOptionLabel() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValue = "this is not a valid option label"

		XCTAssertTrue(stringField.currentValue is [DDLFieldStringWithOptions.Option])
		XCTAssertTrue((stringField.currentValue as! [DDLFieldStringWithOptions.Option]).isEmpty)
	}


	//MARK: CurrentValueAsString

	func test_CurrentValueAsString_ShouldContainTheArrayOfValues() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValue = "Option 3"

		XCTAssertEqual("[\"value 3\"]", stringField.currentValueAsString!)
	}

	func test_CurrentValueAsString_ShouldContainEmptyArray_WhenCurrentValueWasSetToEmptyString() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValue = nil

		XCTAssertEqual("[]", stringField.currentValueAsString!)
	}

	func test_CurrentValueAsString_ShouldSupportOptionLabel_WhenSettingTheStringValue() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)
		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValueAsString = "Option 3"

		XCTAssertEqual("[\"value 3\"]", stringField.currentValueAsString!)
	}

	func test_CurrentValueAsString_ShouldSupportOptionValue_WhenSettingTheStringValue() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)
		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValueAsString = "value 3"

		XCTAssertEqual("[\"value 3\"]", stringField.currentValueAsString!)
	}

	func test_CurrentValueAsString_ShouldSupportOptionValue_WhenSettingAnArrayOfValues() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)
		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValueAsString = "[\"value 3\"]"

		XCTAssertEqual("[\"value 3\"]", stringField.currentValueAsString!)
	}

	func test_CurrentValueAsString_ShouldSupportOptionValue_WhenSettingAnArrayOfUnquotedValues() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)
		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValueAsString = "[value 3]"

		XCTAssertEqual("[\"value 3\"]", stringField.currentValueAsString!)
	}

	func test_CurrentValueAsString_ShouldSupportNil_WhenSettingTheStringValue() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)
		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValueAsString = nil

		XCTAssertEqual("[]", stringField.currentValueAsString!)
	}

	func test_CurrentValueAsString_ShouldSupportNonExistingString_WhenSettingTheStringValue() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)
		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValueAsString = "this is neither a value nor a label"

		XCTAssertEqual("[]", stringField.currentValueAsString!)
	}


	//MARK: CurrentValueAsLabel

	func test_CurrentValueAsLabel_ShouldContainTheLabelOfSelectedOption() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValue = "Option 3"

		XCTAssertEqual("Option 3", stringField.currentValueAsLabel!)
	}

	func test_CurrentValueAsLabel_ShouldContainEmptyString_WhenNoOptionSelected() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValue = nil

		XCTAssertEqual("", stringField.currentValueAsLabel!)
	}

	func test_CurrentValueAsLabel_ShouldStoreTheOption_WhenSetLabelValue() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValueAsLabel = "Option 3"

		XCTAssertEqual("[\"value 3\"]", stringField.currentValueAsString!)
	}


	//MARK: Validate

	func test_Validate_ShouldFail_WhenRequiredValueIsEmptyString() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValue = nil

		XCTAssertFalse(stringField.validate())
	}

	func test_Validate_ShouldPass_WhenRequiredValueIsNotEmptyString() {
		let fields = DDLJSONParser().parse(selectWithPredefinedValuesJSON, locale: spanishLocale)

		let stringField = fields![0] as! DDLFieldStringWithOptions

		stringField.currentValue = "Option 3"

		XCTAssertTrue(stringField.validate())
	}


	private let selectWithPredefinedValuesJSON =
		"{\"availableLanguageIds\": [\"en_US\"]," +
		"\"defaultLanguageId\": \"en_US\"," +
		"\"fields\": [{" +
		"\"label\": {\"en_US\": \"A Select\"}," +
		"\"predefinedValue\": {\"en_US\": [\"value 1\", \"value 2\"]}," +
		"\"dataType\": \"string\"," +
		"\"indexType\": \"keyword\"," +
		"\"localizable\": true," +
		"\"name\": \"A_Select\"," +
		"\"readOnly\": false," +
		"\"repeatable\": true," +
		"\"required\": true," +
		"\"showLabel\": true," +
		"\"multiple\": true," +
		"\"options\": [" +
			"{\"label\": {\"en_US\": \"Option 1\"}," +
				"\"value\": \"value 1\"}," +
			"{\"label\": {\"en_US\": \"Option 2\"}," +
				"\"value\": \"value 2\"}," +
			"{\"label\": {\"en_US\": \"Option 3\"}," +
				"\"value\": \"value 3\"}" +
		"]," +
		"\"type\": \"select\"}]}"

}
