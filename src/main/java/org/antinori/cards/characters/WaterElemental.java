package org.antinori.cards.characters;

import org.antinori.cards.Card;
import org.antinori.cards.CardImage;
import org.antinori.cards.CardType;
import org.antinori.cards.Cards;
import org.antinori.cards.PlayerImage;

public class WaterElemental extends BaseCreature {
	public WaterElemental(Cards game, Card card, CardImage cardImage, int slotIndex, PlayerImage owner, PlayerImage opponent) {
		super(game, card, cardImage, slotIndex, owner, opponent);
	}

	public void onSummoned() {
		super.onSummoned();
		this.card.setAttack(ownerPlayer.getStrengthWater(), true);
		owner.incrementLife(10, game, true);
		ownerPlayer.incrementStrength(CardType.WATER, 1, true);
	}

	public void onAttack() {
		super.onAttack();
	}

	public void startOfTurnCheck(boolean isComputer, PlayerImage player) {
		this.card.setAttack(player.getPlayerInfo().getStrength(CardType.WATER), true);
	}
}
