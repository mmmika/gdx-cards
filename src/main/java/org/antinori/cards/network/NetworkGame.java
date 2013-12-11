package org.antinori.cards.network;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.antinori.cards.BaseFunctions;
import org.antinori.cards.Card;
import org.antinori.cards.CardImage;
import org.antinori.cards.CardType;
import org.antinori.cards.Cards;
import org.antinori.cards.Creature;
import org.antinori.cards.CreatureFactory;
import org.antinori.cards.Player;
import org.antinori.cards.PlayerImage;
import org.antinori.cards.SlotImage;
import org.antinori.cards.Specializations;

import com.badlogic.gdx.graphics.g2d.Sprite;


public class NetworkGame {

	private Socket socket;
	private ServerSocket serverSocket;
	private static final int SERVER_PORT = 5000;
	private Cards game;
	private boolean server = false;
	private boolean isMyTurn = false;
	private boolean playersInitialized = false;
	
	private ExecutorService executor = Executors.newFixedThreadPool(5);

	public NetworkGame(Cards game, boolean server) {

		this.game = game;
		this.server = server;
		
		if (server) {
			
			final BroadcastThread bcth = new BroadcastThread();
			bcth.start();
			
			try {
				serverSocket = new ServerSocket(SERVER_PORT);
				new Thread() {
					public void run() {
						try {
							socket = serverSocket.accept();
							
							System.out.println("Server Socket listening on port 5000");
							
							bcth.setAlive(false);
							isMyTurn = true;
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
		             
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			
		}

	}
	
	public boolean isServer() {
		return this.server;
	}
	
	public boolean isMyTurn() {
		return this.isMyTurn;
	}

	public boolean connectToServer(String remoteHost) {
		try {
			socket = new Socket(remoteHost, SERVER_PORT);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean isConnected() {
		return (socket==null?false:socket.isConnected());
	}
	
	public String getConnectedHost() {
		return (isConnected()? "not connected": "connected");
	}
	
	
	
	
	public void waitForPlayerInitHandshake() {
		
		try {
			
			if (socket == null) throw new Exception("socket is not connected (null).");
			
			NetworkEvent info = new NetworkEvent(Event.REMOTE_PLAYER_INFO_INIT, game.player.getPlayerInfo());
			
			SendInitPlayerInfoThread thread = new SendInitPlayerInfoThread(info);
			thread.start();
			
			while(!playersInitialized) {
			
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Object obj = in.readObject();
				
				System.out.println("Received: "+ obj);
				
				if (obj instanceof NetworkEvent) {
					
					NetworkEvent ne = (NetworkEvent)obj;
					
					Event evt = ne.getEvent();
					String id = ne.getId();
					String pclass = ne.getPlayerClass();
					String pimg = ne.getPlayerIcon();
					
					if (evt == Event.REMOTE_PLAYER_INFO_INIT) {
						
						game.setOpposingPlayerId(id);
						
						PlayerImage pi = game.getPlayerImage(id);
						
						pi.getPlayerInfo().setPlayerClass(Specializations.fromTitleString(pclass));
						pi.getPlayerInfo().setImgName(pimg);
						
						System.out.println("######");
						System.out.println("Got remote id: " + id);
						System.out.println("Local: " + game.player.getPlayerInfo().toString());
						System.out.println("Remote: " + game.opponent.getPlayerInfo().toString());
						System.out.println("######");
						
						Sprite sp = Cards.faceCardAtlas.createSprite(pimg);
						sp.flip(false, true);
						pi.setImg(sp);
						
						pi.getPlayerInfo().setStrengthFire(ne.getPlayer().getStrengthFire());
						pi.getPlayerInfo().setStrengthAir(ne.getPlayer().getStrengthAir());
						pi.getPlayerInfo().setStrengthWater(ne.getPlayer().getStrengthWater());
						pi.getPlayerInfo().setStrengthEarth(ne.getPlayer().getStrengthEarth());
						pi.getPlayerInfo().setStrengthSpecial(ne.getPlayer().getStrengthSpecial());
						
						setPlayerCardsAfterSerialization(ne.getPlayer());

						pi.getPlayerInfo().setCards(CardType.FIRE, ne.getPlayer().getFireCards());
						pi.getPlayerInfo().setCards(CardType.AIR, ne.getPlayer().getAirCards());
						pi.getPlayerInfo().setCards(CardType.WATER, ne.getPlayer().getWaterCards());
						pi.getPlayerInfo().setCards(CardType.EARTH, ne.getPlayer().getEarthCards());
						pi.getPlayerInfo().setCards(CardType.OTHER, ne.getPlayer().getSpecialCards());
						
						thread.setGotResponse(true);
						playersInitialized = true;
					}
					
				}
				
			}
			
			System.out.println("Player Info initialized!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public class SendInitPlayerInfoThread extends Thread {
		private NetworkEvent ne;
		private boolean gotResponse = false;
		public SendInitPlayerInfoThread(NetworkEvent ne) {
			this.ne = ne;
		}
		public void setGotResponse(boolean gotResponse) {
			this.gotResponse = gotResponse;
		}
		public void run() {
			
			try {
				while(!gotResponse) {
					Thread.sleep(2000);
					sendEvent(ne);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	
	
	public void read() {
		try {
			
			if (socket == null) throw new Exception("socket is connected (null).");
			
			boolean stillMoreToCome = true;
			while(stillMoreToCome) {
			
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Object obj = in.readObject();
				
				System.out.println("Received: "+ obj);
				
				if (obj instanceof NetworkEvent) {
					
					NetworkEvent ne = (NetworkEvent)obj;
					executor.execute(new ReadNetworkEventThread(ne));
					
				} else if (obj instanceof String) {
					stillMoreToCome = false;
				}
				
			}
			
			isMyTurn = true;
			System.out.println("It is now my turn!");

		
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	class ReadNetworkEventThread implements Runnable {
		NetworkEvent ne;
		ReadNetworkEventThread(NetworkEvent evt) {
			this.ne = evt;
		}
		public void run() {
			try {
				
				Event evt = ne.getEvent();
				int index = ne.getSlot();
				int val = ne.getAttackAffectedAmount();
				int health = ne.getHealthAffectedAmount();
				String id = ne.getId();
				

				PlayerImage pi = game.getPlayerImage(id);
				SlotImage slot = pi.getSlots()[index];
				CardImage[] cards = pi.getSlotCards();
				CardImage existingCardImage = cards[index];
					
				switch(evt) {
				
				case ATTACK_AFFECTED:
					if (val > 0) {
						existingCardImage.getCard().incrementAttack(val);
					} else {
						existingCardImage.getCard().decrementAttack(val);
					}
					break;
				case CARD_ADDED:
					
					CardImage orig = game.cs.getCardImageByName(Cards.smallCardAtlas, Cards.smallTGACardAtlas, ne.getCardName());
					CardImage ci = orig.clone();
					
					Creature sp1 = CreatureFactory.getCreatureClass(ne.getCardName(), game, ci.getCard(), ci, index, pi, game.getOpposingPlayerImage(id));
					ci.setCreature(sp1);
					
					cards[index] = ci;
					slot.setOccupied(true);
					ci.setFont(Cards.greenfont);
					ci.setFrame(Cards.ramka);
					ci.addListener(game.tl);
					ci.addListener(game.sdl);
					ci.setBounds(0, Cards.SCREEN_HEIGHT, ci.getFrame().getWidth(), ci.getFrame().getHeight());
					game.stage.addActor(ci);
					ci.addAction(sequence(moveTo(slot.getX() + 5, slot.getY() + 26, 1.0f)));
					
					break;
				case CARD_HEALTH_AFFECTED:
					if (health > 0) {
						existingCardImage.incrementLife(Math.abs(health), game);
					} else {
						existingCardImage.decrementLife(Math.abs(health), game);
						PlayerImage oi = game.getOpposingPlayerImage(id);
						game.moveCardActorOnBattle(oi.getSlotCards()[index], oi);
					}
					break;
				case CARD_REMOVED:
					
					BaseFunctions bf = (BaseFunctions)existingCardImage.getCreature();
					bf.disposeCardImage(pi, index);
					
					break;
				case PLAYER_HEALTH_AFFECTED:
					if (health > 0) {
						pi.incrementLife(Math.abs(health), game);
					} else {
						pi.decrementLife(Math.abs(health), game, ne.isDamageViaSpell());
					}
					break;
				case SPELL_CAST:
					break;
				case PLAYER_STRENGTH_AFFECTED:
					int str = ne.getStrengthAffected();
					CardType type = ne.getTypeStrengthAffected();
					if (str > 0) {
						pi.getPlayerInfo().incrementStrength(type, Math.abs(str));
					} else {
						pi.getPlayerInfo().decrementStrength(type, Math.abs(str));
					}
					break;
				case REMOTE_PLAYER_CARDS_INIT:
										
					Player pl = game.getPlayerImage(ne.getId()).getPlayerInfo();
							
					setPlayerCardsAfterSerialization(ne.getPlayer());

					pl.setCards(CardType.FIRE, ne.getPlayer().getFireCards());
					pl.setCards(CardType.AIR, ne.getPlayer().getAirCards());
					pl.setCards(CardType.WATER, ne.getPlayer().getWaterCards());
					pl.setCards(CardType.EARTH, ne.getPlayer().getEarthCards());
					pl.setCards(CardType.OTHER, ne.getPlayer().getSpecialCards());
					
					System.out.println("Set card images after serialization on player id: " + pl.getId());
					
					break;
				default:
					break;
				
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}


	
	
	public void sendEvent(NetworkEvent event) {

		try {
			if (socket == null) throw new Exception("socket is connected (null).");

			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(event);
			out.flush();
			
			System.out.println("Sent event: "+ event);
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void sendYourTurnSignal() {

		try {
			if (socket == null) throw new Exception("socket is connected (null).");

			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject("Your turn.");
			out.flush();
			
			System.out.println("Sent \"your turn\" signal.");
			isMyTurn = false;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

	private void setPlayerCardsAfterSerialization(Player player) {
		try {
			//set cards after serialized
			player.setCards(CardType.FIRE, setCardsAfterSerialization(player.getFireCards()));
			player.setCards(CardType.AIR, setCardsAfterSerialization(player.getAirCards()));
			player.setCards(CardType.WATER, setCardsAfterSerialization(player.getWaterCards()));
			player.setCards(CardType.EARTH, setCardsAfterSerialization(player.getEarthCards()));
			player.setCards(CardType.OTHER, setCardsAfterSerialization(player.getSpecialCards()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	private List<CardImage> setCardsAfterSerialization(List<CardImage> cards) throws Exception {
		
		List<CardImage> ret = new ArrayList<CardImage>();
		
		for (CardImage temp : cards) {
			CardImage ci = getCardImageAfterSerialization(temp.getCard());
			ci.setEnabled(temp.isEnabled());
			ci.setHighlighted(temp.isHighlighted());
			
			ret.add(ci);
		}
		
		CardImage.sort(ret);
		
		return ret;
	}
	
	private CardImage getCardImageAfterSerialization(Card card) throws Exception {
		String name = card.getName();
		CardImage ci = game.cs.getCardImageByName(Cards.smallCardAtlas, Cards.smallTGACardAtlas, name);
		ci.setFont(Cards.greenfont);
		ci.setFrame(ci.getCard().isSpell()?Cards.spellramka:Cards.ramka);
		ci.addListener(game.sdl);
		ci.setBounds(0, 0, ci.getFrame().getWidth(), ci.getFrame().getHeight());
		ci.setCard(card);
		return ci;
	}
	

}
