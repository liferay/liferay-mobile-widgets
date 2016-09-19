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
import LiferayScreens

class DetailViewController: CardViewController, AssetDisplayScreenletDelegate,
	CardDeckDelegate, CardDeckDataSource {
	
	//MARK: Outlets

	@IBOutlet weak var assetDisplayScreenlet: AssetDisplayScreenlet? {
		didSet {
			self.assetDisplayScreenlet?.delegate = self
		}
	}
	@IBOutlet weak var ratingScreenlet: RatingScreenlet?
	@IBOutlet weak var cardDeck: CardDeckView? {
		didSet {
			cardDeck?.delegate = self
			cardDeck?.dataSource = self
		}
	}


	//MARK: CardViewController

	var commentsViewController: CommentsViewController? {
		didSet {
			self.addChildViewController(commentsViewController!)
		}
	}


	//MARK: Public methods

	func load(className className: String, classPK: Int64) {

		/* Load asset */
		assetDisplayScreenlet?.className = className
		assetDisplayScreenlet?.classPK = classPK
		assetDisplayScreenlet?.load()

		/* Load asset rating */
		ratingScreenlet?.className = className
		ratingScreenlet?.classPK = classPK
		ratingScreenlet?.loadRatings()

		/* Load asset comments */
		commentsViewController?.load(className: className, classPK: classPK)
	}

	func hideCommentsCard() {
		self.commentsViewController?.hideAddCommentCard()
		self.cardDeck?.cards[safe: 0]?.changeToState(.Minimized)
	}
	

	//MARK: UIViewController
	
	override func viewDidLoad() {
		super.viewDidLoad()

		self.cardDeck?.layer.zPosition = 0
		
		commentsViewController = CommentsViewController()
	}


	//MARK: Init methods

	convenience init() {
		self.init(nibName: "DetailViewController", bundle: nil)
	}


	//MARK: AssetDisplayScreenletDelegate

	func screenlet(screenlet: AssetDisplayScreenlet, onAssetResponse asset: Asset) {
		self.cardView?.changeButtonText(asset.title)
	}
	

	//MARK: CardDeckDataSource

	func numberOfCardsIn(cardDeck: CardDeckView) -> Int {
		return 1
	}

	func cardDeck(cardDeck: CardDeckView, controllerForCard position: CardPosition)
		-> CardViewController? {
			return commentsViewController
	}


	//MARK: CardDeckDelegate

	func cardDeck(cardDeck: CardDeckView, customizeCard card: CardView, atIndex index: Int) {
		if let firstCardDeck = self.cardView?.superview {
			card.normalHeight = firstCardDeck.frame.height * 0.85
		}
	}

	func cardDeck(cardDeck: CardDeckView, colorForCardIndex index: Int) -> UIColor? {
		return DefaultResources.OddColorBackground
	}

	func cardDeck(cardDeck: CardDeckView, colorForButtonIndex index: Int) -> UIColor? {
		return DefaultResources.EvenColorBackground
	}

	func cardDeck(cardDeck: CardDeckView, buttonImageForCardIndex index: Int) -> UIImage? {
		return UIImage(named: "icon_DOWN_W")
	}

	func cardDeck(cardDeck: CardDeckView, titleForCard position: CardPosition) -> String? {
		return "Comments"
	}
}