package Project3_6481328.utils;

import javax.sound.sampled.*;
import java.io.File;

public final class AudioManager {

    private static volatile Clip    musicClip    = null;
    private static volatile boolean musicRunning = false;

    private AudioManager() {}

    // =========================
    // BACKGROUND MUSIC (looping)
    // =========================

    public static void playMusic(String path) {
        stopMusic();

        Thread t = new Thread(() -> {
            try {
                Clip clip = loadClip(path);
                if (clip == null) return;

                musicClip    = clip;
                musicRunning = true;
                applyMusicVolume();
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
                musicClip.start();

            } catch (Exception e) {
                System.out.println("[AudioManager] Music error: " + e.getMessage());
            }
        });

        t.setDaemon(true);
        t.start();
    }

    public static void stopMusic() {
        musicRunning = false;

        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }

    // =========================
    // SOUND EFFECTS (one-shot)
    // =========================

    public static void playSfx(String path) {
        Thread t = new Thread(() -> {
            try {
                Clip clip = loadClip(path);
                if (clip == null) return;

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

                applyClipVolume(clip, sfxVolume);
                clip.start();

            } catch (Exception e) {
                System.out.println("[AudioManager] SFX error (" + path + "): " + e.getMessage());
            }
        });

        t.setDaemon(true);
        t.start();
    }

    // =========================
    // VOLUME CONTROL
    // =========================

    /** Music volume: 0.0 (silent) to 1.0 (full) */
    private static float musicVolume = 0.8f;

    /** SFX volume: 0.0 (silent) to 1.0 (full) */
    private static float sfxVolume = 0.8f;

    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0f, Math.min(1f, volume));
        applyMusicVolume();
    }

    public static void setSfxVolume(float volume) {
        sfxVolume = Math.max(0f, Math.min(1f, volume));
    }

    public static float getMusicVolume() { return musicVolume; }
    public static float getSfxVolume()   { return sfxVolume; }

    private static void applyMusicVolume() {
        if (musicClip == null) return;
        try {
            FloatControl gain = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(volumeToDb(musicVolume));
        } catch (Exception e) {
            System.out.println("[AudioManager] Could not set music volume: " + e.getMessage());
        }
    }

    private static void applyClipVolume(Clip clip, float volume) {
        try {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(volumeToDb(volume));
        } catch (Exception e) {
            System.out.println("[AudioManager] Could not set clip volume: " + e.getMessage());
        }
    }

    /** Convert linear 0.0–1.0 to decibels for FloatControl */
    private static float volumeToDb(float volume) {
        if (volume <= 0f) return -80f;
        return (float) (20.0 * Math.log10(volume));
    }

    /**
     * Opens a WAV file and converts it to PCM_SIGNED if needed,
     * so Java's Clip can always play it regardless of the original encoding.
     */
    private static Clip loadClip(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("[AudioManager] File not found: " + path);
                return null;
            }

            // Step 1: try to open the raw stream
            AudioInputStream rawStream;
            try {
                rawStream = AudioSystem.getAudioInputStream(file);
            } catch (UnsupportedAudioFileException e) {
                AudioFormat.Encoding[] encodings = AudioSystem.getTargetEncodings(AudioFormat.Encoding.PCM_SIGNED);
                System.out.println("[AudioManager] UNSUPPORTED FORMAT: " + path);
                return null;
            }

            AudioFormat rawFormat = rawStream.getFormat();
            //System.out.println("[AudioManager] Loaded: " + path + " | " + rawFormat);

            // Step 2: if already PCM, use directly; otherwise convert
            AudioInputStream pcmStream;
            if (rawFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED
                    || rawFormat.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED) {
                pcmStream = rawStream;
            } else {
                AudioFormat targetFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        rawFormat.getSampleRate(),
                        16,
                        rawFormat.getChannels(),
                        rawFormat.getChannels() * 2,
                        rawFormat.getSampleRate(),
                        false
                );
                pcmStream = AudioSystem.getAudioInputStream(targetFormat, rawStream);
            }

            Clip clip = AudioSystem.getClip();
            clip.open(pcmStream);
            return clip;

        } catch (Exception e) {
            System.out.println("[AudioManager] Failed to load clip (" + path + "): " + e.getMessage());
            return null;
        }
    }
}