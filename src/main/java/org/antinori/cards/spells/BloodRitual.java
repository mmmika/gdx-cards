package org.antinori.cards.spells;

import org.antinori.cards.PlayerImage;

import org.antinori.cards.Card;
import org.antinori.cards.CardImage;
import org.antinori.cards.Cards;
import org.antinori.cards.GameOverException;
import org.antinori.cards.characters.BaseCreature;

public class BloodRitual extends BaseSpell {
	public BloodRitual(Cards game, Card card, CardImage cardImage, PlayerImage owner, PlayerImage opponent) {
		super(game, card, cardImage, owner, opponent);
	}

	public void onCast() throws GameOverException {
		super.onCast();
		
		int damage = 0;
		
		if (targetedCardImage != null) {
			BaseCreature bc = (BaseCreature) targetedCardImage.getCreature();
			damage = targetedCardImage.getCard().getLife();
			disposeCardImage(opponent, bc.slotIndex);
		}
		
		damage = adjustDamage(damage);
		
		if (damage > 32) damage = 32;
		
		damageAll(opponent, damage);
		
		
	}
}
