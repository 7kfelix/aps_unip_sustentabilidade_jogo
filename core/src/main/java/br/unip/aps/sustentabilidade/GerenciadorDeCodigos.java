package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class GerenciadorDeCodigos {

    private TelaJogo telaJogo;

    // --- VARIÁVEIS DO CHEAT 1: DONVIADO ---
    private final int[] CHEAT_DINHEIRO = {
        Input.Keys.D, Input.Keys.O, Input.Keys.N, Input.Keys.V,
        Input.Keys.I, Input.Keys.A, Input.Keys.D, Input.Keys.O
    };
    private int indexDinheiro = 0;

    // --- VARIÁVEIS DO CHEAT 2: BOSS ---
    private final int[] CHEAT_BOSS = {
        Input.Keys.B, Input.Keys.O, Input.Keys.S, Input.Keys.S
    };
    private int indexBoss = 0;

    public GerenciadorDeCodigos(TelaJogo telaJogo) {
        this.telaJogo = telaJogo;
    }

    public void verificarCodigos() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) && !Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

            // 1. CHECAGEM DO CÓDIGO DE DINHEIRO
            if (Gdx.input.isKeyJustPressed(CHEAT_DINHEIRO[indexDinheiro])) {
                indexDinheiro++;
                if (indexDinheiro == CHEAT_DINHEIRO.length) {
                    telaJogo.adicionarMoedas(1000000);
                    System.out.println("MODO DEV: Código DONVIADO ativado! +1.000.000 Eco-Moedas");
                    indexDinheiro = 0;
                }
            } else {
                indexDinheiro = Gdx.input.isKeyJustPressed(CHEAT_DINHEIRO[0]) ? 1 : 0;
            }

            // 2. CHECAGEM DO CÓDIGO DO BOSS
            if (Gdx.input.isKeyJustPressed(CHEAT_BOSS[indexBoss])) {
                indexBoss++;
                if (indexBoss == CHEAT_BOSS.length) {
                    // Manda a tela pular direto para a onda 20!
                    telaJogo.pularParaOnda(20);
                    System.out.println("MODO DEV: Código BOSS ativado! Invocando o Ônibus...");
                    indexBoss = 0;
                }
            } else {
                indexBoss = Gdx.input.isKeyJustPressed(CHEAT_BOSS[0]) ? 1 : 0;
            }
        }
    }
}
