import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    private Clip bgmClip;
    private Clip walkClip;

    private boolean muted = false;
    private float bgmVolumeDb = -10f;
    private int bgmPausedFrame = 0;
    private int walkPausedFrame = 0;


    private final Map<String, Clip> sfxCache = new HashMap<>();

    // ====== dipanggil MainGameGUI ======
    public void playBackgroundMusic(String fileName) {
        if (muted) return;
        stopMusic();
        bgmClip = loadClipSmart(fileName);
        if (bgmClip == null) return;

        applyVolume(bgmClip, bgmVolumeDb);
        bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
        bgmClip.start();
    }

    public void stopMusic() {
        stopClip(bgmClip);
        bgmClip = null;
    }

    public void playEffect(String fileName) {
        if (muted) return;

        Clip clip = sfxCache.get(fileName);
        if (clip == null) {
            clip = loadClipSmart(fileName);
            if (clip == null) return;
            sfxCache.put(fileName, clip);
        }

        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void playWalkSound() {
        if (muted) return;
        stopWalkSound();
        walkClip = loadClipSmart("walk.wav"); // sesuai nama file kamu
        if (walkClip == null) return;

        applyVolume(walkClip, -8f);
        walkClip.loop(Clip.LOOP_CONTINUOUSLY);
        walkClip.start();
    }

    public void stopWalkSound() {
        stopClip(walkClip);
        walkClip = null;
    }

    public void playWinSound() {
        playEffect("win.wav");
    }

    // tombol mute kamu sekarang harus mute SEMUA (BGM + SFX)
    public void toggleMuteAll() {
        muted = !muted;

        if (muted) {
            // PAUSE (jangan close)
            if (bgmClip != null) {
                bgmPausedFrame = bgmClip.getFramePosition();
                bgmClip.stop();
            }
            if (walkClip != null) {
                walkPausedFrame = walkClip.getFramePosition();
                walkClip.stop();
            }
        } else {
            // RESUME dari frame terakhir
            if (bgmClip != null) {
                bgmClip.setFramePosition(Math.max(0, bgmPausedFrame));
                applyVolume(bgmClip, bgmVolumeDb);
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                bgmClip.start();
            }
            if (walkClip != null) {
                walkClip.setFramePosition(Math.max(0, walkPausedFrame));
                applyVolume(walkClip, -8f);
                walkClip.loop(Clip.LOOP_CONTINUOUSLY);
                walkClip.start();
            }
        }
    }


    public boolean isMuted() {
        return muted;
    }

    public void setBgmVolume(int slider0to100) {
        float db = (slider0to100 / 100f) * 0f + (1f - (slider0to100 / 100f)) * (-40f);
        bgmVolumeDb = db;
        if (bgmClip != null) applyVolume(bgmClip, bgmVolumeDb);
    }

    // ====================== LOADER PENTING ======================
    // ini yang bikin "bisa di semua workspace"
    private Clip loadClipSmart(String fileName) {
        try {
            AudioInputStream ais = openAudioStream(fileName);
            if (ais == null) {
                System.out.println("[AUDIO] NOT FOUND: " + fileName);
                return null;
            }

            AudioFormat base = ais.getFormat();

            // force PCM_SIGNED 16-bit biar kompatibel
            AudioFormat decoded = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    base.getSampleRate(),
                    16,
                    base.getChannels(),
                    base.getChannels() * 2,
                    base.getSampleRate(),
                    false
            );

            AudioInputStream dais = AudioSystem.getAudioInputStream(decoded, ais);
            Clip clip = AudioSystem.getClip();
            clip.open(dais);
            return clip;

        } catch (Exception e) {
            System.out.println("[AUDIO] FAILED LOAD: " + fileName);
            e.printStackTrace();
            return null;
        }
    }

    // buka dari 3 tempat:
    // 1) classpath resource (/src)
    // 2) working directory
    // 3) project root (user.dir)
    private AudioInputStream openAudioStream(String fileName) {
        // (1) classpath: file ada di src -> bisa diakses via getResourceAsStream
        try {
            InputStream in = AudioManager.class.getResourceAsStream("/" + fileName);
            if (in != null) return AudioSystem.getAudioInputStream(new BufferedInputStream(in));
        } catch (Exception ignored) {}

        // (2) working dir
        try {
            File f = new File(fileName);
            if (f.exists()) return AudioSystem.getAudioInputStream(f);
        } catch (Exception ignored) {}

        // (3) project root
        try {
            File f = new File(System.getProperty("user.dir"), fileName);
            if (f.exists()) return AudioSystem.getAudioInputStream(f);
        } catch (Exception ignored) {}

        return null;
    }

    private void stopClip(Clip c) {
        if (c == null) return;
        try {
            c.stop();
            c.close();
        } catch (Exception ignored) {}
    }

    private void applyVolume(Clip clip, float dB) {
        try {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float clamped = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));
            gain.setValue(clamped);
        } catch (Exception ignored) {}
    }
}
