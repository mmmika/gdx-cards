package org.antinori.cards.characters;

import org.antinori.cards.PlayerImage;

import org.antinori.cards.Card;
import org.antinori.cards.CardImage;
import org.antinori.cards.Cards;

public class GreaterDemon extends BaseCreature {
	public GreaterDemon(Cards game, Card card, CardImage cardImage, int slotIndex, PlayerImage owner, PlayerImage opponent) {
		super(game, card, cardImage, slotIndex, owner, opponent);
	}

	public void onSummoned() {
		super.onSummoned();
		
		int power = owner.getPlayerInfo().getStrengthFire();
		if (power > 10) power = 10;
		
		damageAll(opponent,power);
		damageOpponent(power);
	}

	public void onAttack() {
		super.onAttack();
	}
}
