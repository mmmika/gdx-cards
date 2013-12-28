package org.antinori.cards.characters;

import org.antinori.cards.Card;
import org.antinori.cards.CardImage;
import org.antinori.cards.Cards;
import org.antinori.cards.GameOverException;
import org.antinori.cards.PlayerImage;

public class Initiate extends BaseCreature {
	
	public Initiate(Cards game, Card card, CardImage cardImage, int slotIndex, PlayerImage owner, PlayerImage opponent) {
		super(game, card, cardImage, slotIndex, owner, opponent);
	}

	public void onSummoned() throws GameOverException {
		super.onSummoned();
	}

	public void onAttack() throws GameOverException {
		super.onAttack();
	}

	public void onAttacked(int damage) {

		int nl = slotIndex - 1;
		int nr = slotIndex + 1;
		boolean isVampireElderStillAlve = false;

		if (nl >= 0 && owner.getSlotCards()[nl] != null) {
			String n = owner.getSlotCards()[nl].getCard().getName();
			if (n.equalsIgnoreCase("vampireelder")) {
				isVampireElderStillAlve = true;
			}
		}

		if (nr >= 0 && owner.getSlotCards()[nr] != null) {
			String n = owner.getSlotCards()[nr].getCard().getName();
			if (n.equalsIgnoreCase("vampireelder")) {
				isVampireElderStillAlve = true;
			}
		}

		if (isVampireElderStillAlve) {
			// nothing
		} else {
			super.onAttacked(damage);
		}
	}
}
