package org.antinori.cards.spells;import org.antinori.cards.PlayerImage;

import org.antinori.cards.Card;
import org.antinori.cards.CardImage;
import org.antinori.cards.Cards;
import org.antinori.cards.GameOverException;

public class PoisonousCloud extends BaseSpell {
public PoisonousCloud(Cards game, Card card, CardImage cardImage, PlayerImage owner, PlayerImage opponent) {
super(game, card, cardImage, owner, opponent);
}
public void onCast() throws GameOverException {
super.onCast();
}
}
