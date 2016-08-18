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
import QuartzCore


@objc public protocol BaseScreenletDelegate: NSObjectProtocol {

	optional func screenlet(screenlet: BaseScreenlet,
		customInteractorForAction: String,
		withSender: AnyObject?) -> Interactor?

}


/*!
 * BaseScreenlet is the base class from which all Screenlet classes must inherit.
 * A screenlet is the container for a screenlet view.
 */
@IBDesignable public class BaseScreenlet: UIView {

	public static let DefaultAction = "defaultAction"
	public static let DefaultThemeName = "default"

	@IBOutlet public weak var delegate: BaseScreenletDelegate?

	@IBInspectable public var themeName: String? {
		set {
			_themeName = (newValue ?? BaseScreenlet.DefaultThemeName).lowercaseString

			if _runningOnInterfaceBuilder {
				_themeName = updateCurrentPreviewImage()
			}
			else {
				onPreCreate()
				loadScreenletView()
				onCreated()
			}

			screenletView?.themeName = _themeName
		}
		get {
			return _themeName
		}
	}

	public weak var screenletView: BaseScreenletView?

	public weak var presentingViewController: UIViewController? {
		didSet {
			screenletView?.presentingViewController = self.presentingViewController
		}
	}

	internal var isRunningOnInterfaceBuilder: Bool {
		return _runningOnInterfaceBuilder
	}

	private var _themeName = BaseScreenlet.DefaultThemeName
	private var _runningOnInterfaceBuilder = false
	private var _currentPreviewImage: UIImage?
	private var _previewLayer: CALayer?

	private var _runningInteractors = [String:[Interactor]]()

	private var _progressPresenter: ProgressPresenter?
	
	
	//MARK: Initializers
	
	/**
		Initializer for instantiate screenlets from code

		- parameters:
			- themeName: name of the theme to be used. If nil, default theme will be used
	*/
	public init(frame: CGRect, themeName: String?) {
		super.init(frame: frame)
		
		clipsToBounds = true
		
		self.themeName = themeName
	}

	override convenience init(frame: CGRect) {
		self.init(frame: frame, themeName: nil)
	}
	
	required public init?(coder aDecoder: NSCoder) {
		super.init(coder: aDecoder)
	}

	//MARK: UIView

	override public func awakeFromNib() {
		super.awakeFromNib()

		clipsToBounds = true

		if themeName == BaseScreenlet.DefaultThemeName {
			onPreCreate()
			loadScreenletView()
			onCreated()
		}
	}

	override public func becomeFirstResponder() -> Bool {
		return screenletView!.becomeFirstResponder()
	}

	override public func didMoveToWindow() {
		if window != nil {
			onShow()
		}
		else {
			onHide()
		}
	}


	public func refreshTranslations() {
		screenletView?.onSetTranslations()
	}


	//MARK: Interface Builder management methods

	override public func prepareForInterfaceBuilder() {
		_runningOnInterfaceBuilder = true

		_previewLayer = CALayer()

		updateCurrentPreviewImage()
	}

	//MARK: Internal methods

	internal func loadScreenletView() {
		let view = createScreenletViewFromNib()

		if let viewValue = view {
			//FIXME: full-autoresize value. Extract from UIViewAutoresizing
			let flexibleMask = UIViewAutoresizing(rawValue: 18)

			if viewValue.autoresizingMask == flexibleMask {
				viewValue.frame = self.bounds
			}
			else {
				viewValue.frame = centeredRectInView(self, size: viewValue.frame.size)
			}

			viewValue.onPerformAction = { [weak self] name, sender in
				return self!.performAction(name: name, sender: sender)
			}

			viewValue.screenlet = self
			viewValue.presentingViewController = self.presentingViewController
			viewValue.themeName = _themeName
			
			if let oldView = self.screenletView {
				oldView.removeFromSuperview()
			}

			self._progressPresenter = viewValue.createProgressPresenter()
			self.screenletView = viewValue

			addSubview(viewValue)
			sendSubviewToBack(viewValue)
		}
		else {
			self._progressPresenter = nil
			self.screenletView = nil
		}
	}

	internal func previewImageForTheme(themeName:String) -> UIImage? {
		let bundles = NSBundle.allBundles(self.dynamicType)

		for b in bundles {
			let imageName = "\(themeName)-preview-\(ScreenletName(self.dynamicType).lowercaseString)@2x"

			if let imagePath = b.pathForResource(imageName, ofType: "png") {
				if let imageData = NSData(contentsOfFile: imagePath) {
					return UIImage(data: imageData, scale: 2.0)
				}
			}
		}

		return nil
	}


	//MARK: Templated/event methods: intended to be overwritten by children classes

	/*
	 * onCreated is invoked after the screenlet is created. 
	 * Override this method to set custom values for the screenlet properties.
	 */
	public func onCreated() {
	}

	/*
	 * onPreCreate is invoked before the screenlet is created.
	 * Override this method to set create new UI components programatically.
	 *
	 */
	public func onPreCreate() {
	}

	/*
	 * onHide is invoked when the screenlet is hidden from the app window.
	 */
	public func onHide() {
	}

	/*
	 * onShow is invoked when the screenlet is displayed on the app window. 
	 * Override this method for example to reset values when the screenlet is shown.
	 */
	public func onShow() {
	}

	/*
	 * performAction is invoked when a we want to start an interaction (use case)
	 * Typically, it's called from TouchUpInside UI event or when the programmer wants to
	 * start the interaction programatically.
	 */
	public func performAction(name name: String, sender: AnyObject? = nil) -> Bool {
		let result: Bool

		let customInteractor = self.delegate?.screenlet?(self,
				customInteractorForAction: name,
				withSender: sender)

		let standardInteractor = self.createInteractor(
				name: name,
				sender: sender)

		if let interactor = customInteractor ?? standardInteractor {
			trackInteractor(interactor, withName: name)

			if let message = screenletView?.progressMessageForAction(name, messageType: .Working) {
				showHUDWithMessage(message, forInteractor: interactor)
			}

			result = onAction(name: name, interactor: interactor, sender: sender)
		}
		else {
			print("WARN: No interactor created for action \(name)\n")
			result = false
		}

		return result
	}

	public func performDefaultAction() -> Bool {
		return performAction(name: BaseScreenlet.DefaultAction, sender: nil)
	}

	/*
	 * onAction is invoked when an interaction should be started
	 */
	public func onAction(name name: String, interactor: Interactor, sender: AnyObject?) -> Bool {
		onStartInteraction()
		screenletView?.onStartInteraction()

		return interactor.start()
	}

	public func isActionRunning(name: String) -> Bool {
		var firstInteractor: Interactor? = nil

		synchronized(_runningInteractors) {
			firstInteractor = self._runningInteractors[name]?.first
		}

		return firstInteractor != nil
	}

	public func cancelInteractorsForAction(name: String) {
		let interactors = _runningInteractors[name] ?? []

		for interactor in interactors {
			interactor.cancel()
		}
	}

	public func createInteractor(name name: String, sender: AnyObject?) -> Interactor? {
		return nil
	}

	public func endInteractor(interactor: Interactor, error: NSError?) {

		func getMessage() -> String? {
			if let error = error as? ValidationError {
				return error.localizedDescription
			}

			return screenletView?.progressMessageForAction(
					interactor.actionName ?? BaseScreenlet.DefaultAction,
					messageType: error == nil ? .Success : .Failure)
		}

		untrackInteractor(interactor)

		let result: AnyObject? = interactor.interactionResult()
		onFinishInteraction(result, error: error)
		screenletView?.onFinishInteraction(result, error: error)
		hideHUDWithMessage(getMessage(), forInteractor: interactor, withError: error)
	}

	/**
	 * onStartInteraction is called just before a screenlet request is sent to server
	 */
	public func onStartInteraction() {
	}

	/**
	 * onFinishInteraction is called when the server response arrives
	 */
	public func onFinishInteraction(result: AnyObject?, error: NSError?) {
	}


	//MARK: HUD methods

	public func showHUDWithMessage(message: String?,
			forInteractor interactor: Interactor) {
		
		_progressPresenter?.showHUDInView(rootView(self),
			message: message,
			forInteractor: interactor)
	}

	public func hideHUDWithMessage(message: String?,
			forInteractor interactor: Interactor,
			withError error: NSError?) {
		
		_progressPresenter?.hideHUDFromView(rootView(self),
			message: message,
			forInteractor: interactor,
			withError: error)
	}


	//MARK: Private

	private func createScreenletViewFromNib() -> BaseScreenletView? {

		func tryLoadForTheme(themeName: String, inBundles bundles: [NSBundle]) -> BaseScreenletView? {
			for bundle in bundles {
				let viewName = "\(ScreenletName(self.dynamicType))View"
				let nibName = "\(viewName)_\(themeName)"
				let nibPath = bundle.pathForResource(nibName, ofType:"nib")

				if nibPath != nil {
					let views = bundle.loadNibNamed(nibName,
						owner:self,
						options:nil)

					assert(views.count > 0, "Malformed xib \(nibName). Without views")

					return (views[0] as? BaseScreenletView)
				}
			}

			return nil;
		}

		let bundles = NSBundle.allBundles(self.dynamicType);

		if let foundView = tryLoadForTheme(_themeName, inBundles: bundles) {
			return foundView
		}

		if let foundView = tryLoadForTheme(BaseScreenlet.DefaultThemeName, inBundles: bundles) {
			return foundView
		}

		print("ERROR: Xib file doesn't found for screenlet '\(ScreenletName(self.dynamicType))' and theme '\(_themeName)'\n")

		return nil
	}

	private func updateCurrentPreviewImage() -> String {
		var appliedTheme = _themeName

		_currentPreviewImage = previewImageForTheme(_themeName)
		if _currentPreviewImage == nil {
			if let previewImage = previewImageForTheme(BaseScreenlet.DefaultThemeName) {
				_currentPreviewImage = previewImage
				appliedTheme = BaseScreenlet.DefaultThemeName
			}
		}

		if let screenletViewValue = screenletView {
			screenletViewValue.removeFromSuperview()
		}

		if let currentPreviewImageValue = _currentPreviewImage {
			_previewLayer!.frame = centeredRectInView(self, size: currentPreviewImageValue.size)
			_previewLayer!.contents = currentPreviewImageValue.CGImage

			if _previewLayer!.superlayer != layer {
				layer.addSublayer(_previewLayer!)
			}
		}
		else {
			loadScreenletView()
		}

		setNeedsLayout()

		return appliedTheme
	}

	private func rootView(currentView:UIView) -> UIView {
		if currentView.superview == nil {
			return currentView;
		}

		return rootView(currentView.superview!)
	}

	private func trackInteractor(interactor: Interactor, withName name: String) {
		synchronized(_runningInteractors) {
			var interactors = self._runningInteractors[name]
			if interactors?.count ?? 0 == 0 {
				interactors = [Interactor]()
			}

			interactors?.append(interactor)

			self._runningInteractors[name] = interactors
			interactor.actionName = name
		}
	}

	private func untrackInteractor(interactor: Interactor) {
		synchronized(_runningInteractors) {
			let name = interactor.actionName!
			let interactors = self._runningInteractors[name] ?? []

			if let foundIndex = interactors.indexOf(interactor) {
				self._runningInteractors[name]?.removeAtIndex(foundIndex)
			}
			else {
				print("ERROR: There's no interactors tracked for name \(interactor.actionName!)\n")
			}
		}
	}

}
