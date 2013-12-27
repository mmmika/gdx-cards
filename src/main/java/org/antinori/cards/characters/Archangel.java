package org.antinori.cards.characters;

import org.antinori.cards.PlayerImage;

import org.antinori.cards.Card;
import org.antinori.cards.CardImage;
import org.antinori.cards.Cards;

public class Archangel extends BaseCreature {
	public Archangel(Cards game, Card card, CardImage cardImage, int slotIndex, PlayerImage owner, PlayerImage opponent) {
		super(game, card, cardImage, slotIndex, owner, opponent);
	}

	public void onSummoned() {
		super.onSummoned();
		
		for (int index = 0; index < 6; index++) {
			CardImage ci = owner.getSlotCards()[index];
			if (ci == null || index == slotIndex)	continue;
			int inc = ci.getCard().getOriginalLife() - ci.getCard().getLife();
			ci.incrementLife(inc, game);
		}
	}

	public void onAttack() {
		super.onAttack();
	}
}
