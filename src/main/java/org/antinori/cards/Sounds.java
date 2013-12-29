package org.antinori.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Sounds {
	
	public static Map<Sound, Music> sounds = new HashMap<Sound, Music>();
	public static Map<Spell, Music> spells = new HashMap<Spell, Music>();
	
	public static List<Sound> backgroundList = new ArrayList<Sound>();
	public static boolean backgroundPlaying = true;
	
	static {
		backgroundList.add(Sound.BACKGROUND1);
		backgroundList.add(Sound.BACKGROUND2);
		backgroundList.add(Sound.BACKGROUND3);
	}
	
	public static Music.OnCompletionListener listener = new Music.OnCompletionListener() {
		public void onCompletion(Music music) {
			backgroundPlaying = false;
		}
	};
	
	public static void startBackGroundMusic() {
		new Thread() {
			public void run() {
				while (true) {
					for (Sound sound : backgroundList) {
						Music m = play(sound);
						backgroundPlaying = true;
						m.setOnCompletionListener(listener);
						while (backgroundPlaying) {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
							}
						}
					}
				}
			}
		}.start();		
	}
	
	public static Music play(Sound sound) {
		Music m = sounds.get(sound);
		if (m == null) {
			m = Gdx.audio.newMusic(Gdx.files.classpath(sound.getFile()));
			m.setVolume(sound.getVolume());
			m.setLooping(sound.getLooping());

			sounds.put(sound, m);
		}
		m.play();
		return m;
	}
	
	public static Music play(Spell spell) {
		Music m = spells.get(spell);
		if (m == null) {
			String file = "audio/" + spell.getClass().getSimpleName() + ".ogg";
			
			m = Gdx.audio.newMusic(Gdx.files.classpath(file));
			
			if (m != null) {
				m.setVolume(0.3f);
				m.setLooping(false);
				//System.out.println(file + "\t\t\t\t" + m);
			} else {
				m = sounds.get(Sound.MAGIC);
				//System.out.println(file + "\t\t\t\t" + "NULL");
			}
			
			spells.put(spell, m);
		}
		
		m.play();
		return m;
	}

}
