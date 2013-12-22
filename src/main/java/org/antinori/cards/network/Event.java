package org.antinori.cards.network;

public enum Event {
	
	REMOTE_PLAYER_INFO_INIT,
	
	PLAYER_INCR_LIFE,
	PLAYER_DECR_LIFE,
	
	PLAYER_DECR_STRENGTH,
	PLAYER_INCR_STRENGTH,
	PLAYER_INCR_STRENGTH_ALL,
	
	CARD_ADDED,
	CARD_REMOVED,
	CARD_ATTACKED,
		
	SPELL_CAST;

}
