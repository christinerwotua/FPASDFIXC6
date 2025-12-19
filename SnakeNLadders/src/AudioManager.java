import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class AudioManager {
    private Clip bgmClip;
    private Clip sfxClip;
    private Clip walkClip;
    private Clip winClip;

    private boolean muted = false;
    private int bgmVolumeSlider = 50; // 0..100

    public boolean isMuted() { return muted; }

    public void toggleMuteAll() {
        muted = !muted;
        if (muted) {
            stopMusic();
            stopWalkSound();
            stopSfxNow();
        } else {
            // BGM dinyalakan lagi dari MainGameGUI saat start/restart
        }
    }

    public void setBgmVolume(int sliderValue) {
        bgmVolumeSlider = sliderValue;
        setClipVolume(bgmClip, bgmVolumeSlider);
    }

    public void playBackgroundMusic(String fileName) {
        if (muted) return;
        try {
            File f = resolveFile(fileName);
            if (!f.exists()) return;

            stopMusic();

            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(ais);
            setClipVolume(bgmClip, bgmVolumeSlider);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
        } catch (Exception e) {
            System.out.println("BGM error: " + e.getMessage());
        }
    }

    public void stopMusic() {
        try {
            if (bgmClip != null) {
                bgmClip.stop();
                bgmClip.close();
            }
        } catch (Exception ignored) {}
    }

    public void playEffect(String fileName) {
        if (muted) return;
        try {
            File f = resolveFile(fileName);
            if (!f.exists()) return;

            stopSfxNow();

            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            sfxClip = AudioSystem.getClip();
            sfxClip.open(ais);
            setClipVolume(sfxClip, 85); // fixed SFX volume
            sfxClip.start();
        } catch (Exception ignored) {}
    }

    private void stopSfxNow() {
        try {
            if (sfxClip != null) {
                sfxClip.stop();
                sfxClip.close();
            }
        } catch (Exception ignored) {}
    }

    public void playWalkSound(String fileName) {
        if (muted) return;
        try {
            File f = resolveFile(fileName);
            if (!f.exists()) return;

            stopWalkSound();

            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            walkClip = AudioSystem.getClip();
            walkClip.open(ais);
            setClipVolume(walkClip, 80);
            walkClip.loop(Clip.LOOP_CONTINUOUSLY);
            walkClip.start();
        } catch (Exception ignored) {}
    }

    public void stopWalkSound() {
        try {
            if (walkClip != null) {
                walkClip.stop();
                walkClip.close();
            }
        } catch (Exception ignored) {}
    }

    public void playWinSound(String fileName) {
        if (muted) return;
        try {
            File f = resolveFile(fileName);
            if (!f.exists()) return;

            if (winClip != null) {
                winClip.stop();
                winClip.close();
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            winClip = AudioSystem.getClip();
            winClip.open(ais);
            setClipVolume(winClip, 90);
            winClip.start();
        } catch (Exception ignored) {}
    }

    private void setClipVolume(Clip clip, int sliderValue0to100) {
        if (clip == null || !clip.isOpen()) return;
        try {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            float min = gain.getMinimum();
            float max = gain.getMaximum();

            if (sliderValue0to100 <= 0) {
                gain.setValue(min);
                return;
            }

            // convert slider to dB (lebih stabil)
            double normalized = sliderValue0to100 / 100.0; // 0..1
            double db = 20.0 * Math.log10(Math.max(0.0001, normalized));
            float val = (float) Math.max(min, Math.min(max, db));
            gain.setValue(val);
        } catch (Exception ignored) {}
    }

    private File resolveFile(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) f = new File(System.getProperty("user.dir"), fileName);
        return f;
    }
}
