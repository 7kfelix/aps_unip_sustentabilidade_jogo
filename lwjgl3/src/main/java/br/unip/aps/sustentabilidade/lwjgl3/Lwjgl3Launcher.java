package br.unip.aps.sustentabilidade.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import br.unip.aps.sustentabilidade.SustentabilidadeGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new SustentabilidadeGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("STD - Sustentabilidade Tower Defense");

        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate screen tearing.
        configuration.useVsync(true);

        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional refresh rates.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

        // ==========================================
        // MÁGICA DA TELA CHEIA AQUI:
        // Pega a resolução nativa do monitor do jogador e força o Fullscreen!
        // ==========================================
        configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return configuration;
    }
}
