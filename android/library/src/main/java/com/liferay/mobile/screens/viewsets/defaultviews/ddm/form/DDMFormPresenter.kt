/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.screens.viewsets.defaultviews.ddm.form

import com.liferay.mobile.screens.ddl.form.view.DDLFieldViewModel
import com.liferay.mobile.screens.ddl.model.*
import com.liferay.mobile.screens.ddm.form.model.*
import com.liferay.mobile.screens.util.LiferayLogger
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.InputStream
import java.util.*

/**
 * @author Victor Oliveira
 */
class DDMFormPresenter(val view: DDMFormViewContract.DDMFormView,
	serverUrl: String) : DDMFormViewContract.DDMFormViewPresenter {

	private val compositeSubscription = CompositeSubscription()
	private val dirtyFieldNames: MutableList<String> = mutableListOf()
	private val interactor = DDMFormInteractor(serverUrl)
	private var formInstanceState = FormInstanceState.IDLE

	var formInstanceRecord: FormInstanceRecord? = null

	override fun addToDirtyFields(field: Field<*>) {
		if (!dirtyFieldNames.contains(field.name)) {
			dirtyFieldNames.add(field.name)
		}
	}

	override fun checkIsDirty(field: Field<*>, fieldContext: FieldContext, fieldViewModel: DDLFieldViewModel<*>?) {
		if (dirtyFieldNames.contains(field.name)) {
			val isValid = fieldContext.isValid ?: true

			field.lastValidationResult = isValid

			if (isValid) {
				field.setValidationState(Field.ValidationState.VALID)
			} else {
				field.setValidationState(Field.ValidationState.INVALID_BY_EVALUATOR_RULE, fieldContext.errorMessage)
			}

			fieldViewModel?.onPostValidation(isValid)
		}
	}

	override fun evaluateContext(formInstance: FormInstance, fields: MutableList<Field<*>>, onComplete: (() -> Unit)?) {
		view.showModalEvaluateContextLoading()
		formInstanceState = FormInstanceState.EVALUATING_CONTEXT

		val subscription = interactor.evaluateContext(formInstance, fields).subscribe({ formContext ->
			view.hideModalLoading()

			view.updatePageEnabled(formContext)

			updateFields(formContext, fields)
			onComplete?.invoke()
			resetFormInstanceState()
		}) {
			LiferayLogger.e(it.message)

			view.hideModalLoading()
			view.showErrorMessage(it)

			onComplete?.invoke()
			resetFormInstanceState()
		}

		compositeSubscription.add(subscription)
	}

	override fun fieldModelsChanged(field: Field<*>) {
		if (!field.isTransient) {
			addToDirtyFields(field)

			formInstanceRecord?.let {
				it.fieldValues[field.name] = field.toData() ?: ""
			}
		}
	}

	override fun getFormInstanceState(): FormInstanceState {
		return formInstanceState
	}

	override fun onDestroy() {
		compositeSubscription.clear()
	}

	override fun syncForm(formInstance: FormInstance, field: Field<*>?, isDraft: Boolean, onComplete: (() -> Unit)?) {
		val fieldIsTransient = field?.isTransient ?: false
		val invalidStateForSync = formInstanceState != FormInstanceState.IDLE

		if (fieldIsTransient || invalidStateForSync) return

		if (!view.hasConnectivity()) {
			view.showOfflineWarningMessage()
			return
		}

		var fieldHasRules = field?.hasFormRules() ?: false

		if (isDraft) {
			submit(formInstance, isDraft)
		} else {
			fieldHasRules = formInstance.hasFormRules
		}

		if (fieldHasRules) {
			evaluateContext(formInstance, formInstance.ddmStructure.fields, onComplete)
		} else {
			onComplete?.invoke()
		}
	}

	override fun restore(formInstanceRecord: FormInstanceRecord?, fields: MutableList<Field<*>>,
		formInstanceState: FormInstanceState) {

		this.formInstanceState = formInstanceState
		formInstanceRecord?.let {
			this.formInstanceRecord = it
			updateFields(it.fieldValues, fields)
		}
	}

	override fun submit(formInstance: FormInstance, isDraft: Boolean) {
		val autosaveDisabled = isDraft && !view.config.autosaveDraftEnabled
		val invalidStateForSubmission = formInstanceState != FormInstanceState.IDLE

		if (autosaveDisabled || invalidStateForSubmission) return

		formInstanceState = if (isDraft) FormInstanceState.SAVING_DRAFT else FormInstanceState.SUBMITTING
		val fields = formInstance.ddmStructure.fields
		view.isSubmitEnabled(isDraft)

		val subscription = interactor.submit(formInstance, formInstanceRecord, fields, isDraft)
			.subscribe({ newFormInstanceRecord ->
				resetFormInstanceState()
				formInstanceRecord = newFormInstanceRecord

				if (!isDraft) {
					formInstance.ddmStructure.successPage?.let {
						view.showSuccessPage(formInstance.ddmStructure.successPage)
					} ?: run {
						view.showSuccessMessage()
					}
					view.isSubmitEnabled(true)
					view.ddmFormListener?.onFormSubmitted(newFormInstanceRecord)
					resetRecordState()
				} else {
					view.ddmFormListener?.onDraftSaved(newFormInstanceRecord)
				}
			}) { throwable ->
				LiferayLogger.e(throwable.message)
				resetFormInstanceState()

				if (!isDraft) {
					view.isSubmitEnabled(true)
					view.showErrorMessage(throwable)
					view.ddmFormListener?.onError(throwable)
				}
			}

		compositeSubscription.add(subscription)
	}

	override fun loadInitialContext(formInstance: FormInstance) {
		view.showModalSyncFormLoading()
		formInstanceState = FormInstanceState.SYNCING

		val fields = formInstance.ddmStructure.fields

		if (formInstance.hasDataProvider) {
			fetchDataProviders(formInstance, fields) {
				fetchLatestDraft(formInstance, fields) {
					onCompleteFetch(formInstance, fields)
				}
			}
		} else {
			fetchLatestDraft(formInstance, fields) {
				onCompleteFetch(formInstance, fields)
			}
		}
	}

	override fun uploadFile(formInstance: FormInstance, field: DocumentField, inputStream: InputStream,
		onSuccess: (DocumentRemoteFile) -> Unit, onError: (Throwable) -> Unit) {

		val subscription = interactor.uploadFile(formInstance, field, inputStream)
			.subscribe({
				onSuccess(it)
			}) {
				onError(it)
			}

		compositeSubscription.add(subscription)
	}

	private fun fetchDataProviders(formInstance: FormInstance, fields: MutableList<Field<*>>,
		onComplete: (() -> Unit)?) {

		view.showModalEvaluateContextLoading()

		val subscription = interactor.evaluateContext(formInstance, fields).subscribe({ formContext ->
			view.updatePageEnabled(formContext)

			updateFields(formContext, fields)

			onComplete?.invoke()
		}) {
			LiferayLogger.e(it.message)

			view.hideModalLoading()
			view.showErrorMessage(it)

			onComplete?.invoke()
		}

		compositeSubscription.add(subscription)
	}

	private fun fetchLatestDraft(formInstance: FormInstance, fields: MutableList<Field<*>>, onComplete: (() -> Unit)?) {
		if (!view.config.autoloadDraftEnabled) {
			onComplete?.invoke()
			return
		}

		val subscription = interactor.fetchLatestDraft(formInstance.id)
			.subscribe({ newFormRecord ->
				view.hideModalLoading()
				formInstanceRecord = newFormRecord
				updateFields(newFormRecord.fieldValues, fields)

				view.ddmFormListener?.onDraftLoaded(newFormRecord)

				onComplete?.invoke()
			}) {
				LiferayLogger.e(it.message, it)
				view.hideModalLoading()
				onComplete?.invoke()
			}

		compositeSubscription.add(subscription)
	}

	private fun onCompleteFetch(formInstance: FormInstance, fields: MutableList<Field<*>>) {
		if (formInstance.isEvaluable) {
			evaluateContext(formInstance, fields) {
				resetFormInstanceState()
			}
		} else {
			resetFormInstanceState()
		}
	}

	private fun resetFormInstanceState() {
		formInstanceState = FormInstanceState.IDLE
	}

	private fun resetRecordState() {
		formInstanceRecord = null

		view.formInstance?.also {
			loadInitialContext(it)

			val fields = it.ddmStructure.fields
			val emptyValues = fields.map { field -> FieldValue(field.name, "") }
			updateFields(emptyValues, fields)
		}
	}

	private fun setOptions(fieldContext: FieldContext, optionsField: OptionsField<*>) {
		val availableOptions = fieldContext.options as? List<Map<String, String>>

		availableOptions?.let {
			optionsField.availableOptions = ArrayList(availableOptions.map { Option(it) })
		}
	}

	private fun setValue(fieldContext: FieldContext, field: Field<*>) {
		val isValueChanged = fieldContext.isValueChanged ?: false

		if (isValueChanged) {
			addToDirtyFields(field)

			fieldContext.value?.toString()?.let {
				field.setCurrentStringValue(it)
			}
		}
	}

	private fun updateFields(fieldValues: List<FieldValue>, fields: MutableList<Field<*>>) {
		val fieldsMap = fields.map {
			Pair(it.name, it)
		}.toMap()

		fieldValues.forEach { fieldValue ->
			val field = fieldsMap[fieldValue.name]

			field?.also {
				if (!field.isTransient) {
					field.setCurrentStringValue(fieldValue.value as String)
				}
			}
		}

		view.refreshVisibleFields()
	}

	private fun updateFields(formContext: FormContext, fields: MutableList<Field<*>>) {
		val fieldContexts = formContext.pages.flatMap(FormContextPage::fields)

		val fieldsMap = fields.map {
			Pair(it.name, it)
		}.toMap()

		fieldContexts.forEach { fieldContext ->
			val field = fieldsMap[fieldContext.name]

			field?.let {
				(field as? OptionsField<*>)?.let { optionsField ->
					setOptions(fieldContext, optionsField)
				}

				if (fieldContext.isValueChanged == true) {
					setValue(fieldContext, field)
				}

				field.isReadOnly = fieldContext.isReadOnly ?: field.isReadOnly
				field.isRequired = fieldContext.isRequired ?: field.isRequired

				view.updateFieldView(fieldContext, field)
			}
		}
	}
}