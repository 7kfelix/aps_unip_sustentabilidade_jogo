package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class GerenciadorAudio {
    public static Music musicaMenu;
    public static Music musicaJogo;

    // --- EFEITOS SONOROS (SFX) ---
    public static Sound somInserir;
    public static Sound somTiro;
    public static Sound somSniper;
    public static Sound somAguaFonte;
    public static Sound somBalao;
    public static Sound somEnraizar;
    public static Sound somFolhas;
    public static Sound somRenda;
    public static Sound somMorte;

    public static int volume = 100;

    public static void inicializarSons() {
        try {
            somInserir = Gdx.audio.newSound(Gdx.files.internal("inserir.mp3"));
            somTiro = Gdx.audio.newSound(Gdx.files.internal("tiro.mp3"));
            somSniper = Gdx.audio.newSound(Gdx.files.internal("tiro_sniper.mp3"));
            somAguaFonte = Gdx.audio.newSound(Gdx.files.internal("agua_fonte.mp3"));
            somBalao = Gdx.audio.newSound(Gdx.files.internal("balao.mp3"));
            somEnraizar = Gdx.audio.newSound(Gdx.files.internal("enraizar.mp3"));
            somFolhas = Gdx.audio.newSound(Gdx.files.internal("folhas_arvore.mp3"));
            somRenda = Gdx.audio.newSound(Gdx.files.internal("som_renda_arvore.mp3"));
            somMorte = Gdx.audio.newSound(Gdx.files.internal("inimigo_morre.mp3"));
        } catch (Exception e) {
            System.out.println("Aviso: Falha ao carregar alguns SFX.");
        }
    }

    // ========================================================
    // MÉTODO UNIVERSAL PARA TOCAR EFEITOS (BEM MAIS BAIXO)
    // ========================================================
    public static void tocarSom(Sound som) {
        if (som != null) {
            // Corta o som anterior caso a mesma torre atire de novo rápido demais (evita estourar o áudio)
            som.stop();

            // REDUZ O VOLUME DOS EFEITOS PARA 20% (0.2f) do volume geral
            // Assim eles ficam como um feedback suave de fundo e não irritam!
            float volumeSfx = (volume / 100f) * 0.2f;

            som.play(volumeSfx);
        }
    }

    // --- MÚSICAS ---
    public static void tocarMusicaMenu() {
        pararMusicaJogo();
        if (musicaMenu == null) {
            try {
                musicaMenu = Gdx.audio.newMusic(Gdx.files.internal("musica_menu.mp3"));
                musicaMenu.setLooping(true);
            } catch (Exception e) { return; }
        }
        // A música do menu fica um pouquinho mais alta para dar o clima inicial
        musicaMenu.setVolume((volume / 100f) * 0.4f);
        if (!musicaMenu.isPlaying()) musicaMenu.play();
    }

    public static void tocarMusicaJogo() {
        pararMusicaMenu();
        if (musicaJogo == null) {
            try {
                musicaJogo = Gdx.audio.newMusic(Gdx.files.internal("musica_jogo.mp3"));
                musicaJogo.setLooping(true);
            } catch (Exception e) { return; }
        }
        // A música do jogo fica equilibrada (0.3f) para dar espaço pros efeitos (0.2f)
        musicaJogo.setVolume((volume / 100f) * 0.3f);
        if (!musicaJogo.isPlaying()) musicaJogo.play();
    }

    public static void pararMusicaMenu() { if (musicaMenu != null && musicaMenu.isPlaying()) musicaMenu.stop(); }
    public static void pararMusicaJogo() { if (musicaJogo != null && musicaJogo.isPlaying()) musicaJogo.stop(); }
    public static void pausarMusicaJogo() { if (musicaJogo != null && musicaJogo.isPlaying()) musicaJogo.pause(); }
    public static void despausarMusicaJogo() { if (musicaJogo != null && !musicaJogo.isPlaying()) musicaJogo.play(); }

    public static void setVolume(int novoVolume) {
        volume = novoVolume;
        float multiplicador = volume / 100f;

        if (musicaMenu != null) musicaMenu.setVolume(multiplicador * 0.4f);
        if (musicaJogo != null) musicaJogo.setVolume(multiplicador * 0.3f);
        // Os sons (SFX) não precisam ser atualizados aqui porque o volume deles
        // é calculado na hora do "tocarSom()".
    }
}
