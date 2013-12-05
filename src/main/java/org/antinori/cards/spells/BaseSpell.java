package org.antinori.cards.spells;

import org.antinori.cards.BaseFunctions;
import org.antinori.cards.Card;
import org.antinori.cards.CardImage;
import org.antinori.cards.Cards;
import org.antinori.cards.Spell;

public class BaseSpell extends BaseFunctions implements Spell {
	
	CardImage targetedCardImage;
		
	public BaseSpell(Cards game, Card card, CardImage cardImage, boolean isComputer) {
		
		this.game = game;
		this.card = card;
		this.cardImage = cardImage;
		this.isComputer = isComputer;
		
		this.enemyCards = isComputer?game.getBottomSlotCards():game.getTopSlotCards();
		this.teamCards = isComputer?game.getTopSlotCards():game.getBottomSlotCards();
		
		this.opposingPlayer = isComputer?game.player.getPlayerInfo():game.opponent.getPlayerInfo();
		this.ownerPlayer = isComputer?game.opponent.getPlayerInfo():game.player.getPlayerInfo();
		
		this.owner = isComputer?game.opponent:game.player;
		this.opponent = isComputer?game.player:game.opponent;
		
	}
	
	public void setTargeted(CardImage target) {
		targetedCardImage = target;
	}

	public void onCast() {
		
		System.out.println("onCast: " + card);
		
		ownerPlayer.decrementStrength(card.getType(), card.getCost());

		moveCardActorOnMagic();

	}
	

}
