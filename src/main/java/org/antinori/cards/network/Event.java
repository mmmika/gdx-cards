package org.antinori.cards.network;

public enum Event {
	
	REMOTE_PLAYER_INFO_INIT,
	REMOTE_PLAYER_CARDS_INIT,
	
	PLAYER_INCR_LIFE,
	PLAYER_DECR_LIFE,
	
	PLAYER_DECR_STRENGTH,
	PLAYER_INCR_STRENGTH,
	PLAYER_INCR_STRENGTH_ALL,
	
	CARD_ADDED,
	CARD_REMOVED,
	
	CARD_SET_LIFE,
	CARD_INCR_LIFE,
	CARD_DECR_LIFE,
	
	CARD_SET_ATTACK,
	
	SPELL_CAST;

}
