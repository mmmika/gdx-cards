package org.antinori.cards.characters;

import org.antinori.cards.Card;
import org.antinori.cards.CardImage;
import org.antinori.cards.Cards;

public class OrcChieftain extends BaseCreature {

	public OrcChieftain(Cards game, Card card, CardImage cardImage, boolean isComputer, int slotIndex) {
		super(game, card, cardImage, isComputer, slotIndex);
	}
	
	public void onSummoned() {
		super.onSummoned();
	}

	public void onAttack() {
		super.onAttack();
	}
}
