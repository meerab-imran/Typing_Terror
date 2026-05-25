package sound;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private float volume = 0.8f;
    private boolean muted = false;

    private static final String SOUND_DIR = "resources/sounds/";
    private final Map<String, Clip> clips = new HashMap<>();

    private static final String[] SOUND_NAMES = {
            "correct", "miss", "powerup", "gameover", "cheer", "click", "boss"
    };

    public SoundManager() {
        loadAllSounds();
    }

    private void loadAllSounds() {
        for (String name : SOUND_NAMES) {
            try {
                File file = new File(SOUND_DIR + name + ".wav");
                if (!file.exists()) {
                    System.out.println("Sound file missing: " + file.getPath());
                    continue;
                }
                AudioInputStream ais = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clips.put(name, clip);
            } catch (Exception e) {
                System.out.println("Could not load sound '" + name + "': " + e.getMessage());
            }
        }
        System.out.println("Loaded " + clips.size() + "/" + SOUND_NAMES.length + " sounds.");
    }

    private void play(String name) {
        if (muted) return;
        Clip clip = clips.get(name);
        if (clip == null) return;
        new Thread(() -> {
            try {
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float dB = (float) (Math.log10(Math.max(volume, 0.0001f)) * 20.0);
                    gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB)));
                }
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception e) {
                System.out.println("Error playing sound '" + name + "': " + e.getMessage());
            }
        }, "sound-" + name).start();
    }

    public void playCorrect()   { play("correct");  }
    public void playMiss()      { play("miss");      }
    public void playPowerUp()   { play("powerup");   }
    public void playGameOver()  { play("gameover");  }
    public void playHighScore() { play("cheer");     }
    public void playClick()     { play("click");     }
    public void playBossSpawn() { play("boss");      }

    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
    }

    public void toggleMute() {
        muted = !muted;
    }

    public boolean isMuted()  { return muted;   }
    public float getVolume()  { return volume;  }
}
