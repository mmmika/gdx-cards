package org.antinori.cards.spells;

import org.antinori.cards.PlayerImage;

import org.antinori.cards.Card;
import org.antinori.cards.CardImage;
import org.antinori.cards.CardType;
import org.antinori.cards.Cards;

public class WrathofGod extends BaseSpell {
	public WrathofGod(Cards game, Card card, CardImage cardImage, PlayerImage owner, PlayerImage opponent) {
		super(game, card, cardImage, owner, opponent);
	}

	public void onCast() {
		super.onCast();
		
		int inc = 0;
		for (int index = 0; index < 6; index++) {
			CardImage ci = opponent.getSlotCards()[index];
			if (ci == null)	continue;
			ci.decrementLife(adjustDamage(12), game);
			
			int remainingLife = ci.getCard().getLife();
			boolean died = (remainingLife < 1);

			if (died) {
				disposeCardImage(opponent, index);
			} else {
				inc ++;
			}
			
		}
		
		owner.getPlayerInfo().incrementStrength(CardType.HOLY, inc, !remoteEvent);
	}
}
