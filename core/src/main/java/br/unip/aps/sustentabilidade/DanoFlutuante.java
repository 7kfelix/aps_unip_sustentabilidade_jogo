package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DanoFlutuante {
    public float x, y;
    public int dano;
    private float tempoDeVida;
    private final float TEMPO_MAXIMO = 0.8f; // Quanto tempo o número fica na tela
    private Color cor;

    public DanoFlutuante(float x, float y, int dano, Color cor) {
        // Colocamos um leve offset aleatório para os números não nascerem grudados uns nos outros
        this.x = x + (float)(Math.random() * 30 - 15);
        this.y = y + (float)(Math.random() * 20);
        this.dano = dano;
        this.tempoDeVida = TEMPO_MAXIMO;
        this.cor = new Color(cor); // Copiamos a cor para podermos alterar a transparência
    }

    public void atualizar(float deltaTime) {
        this.y += 60 * deltaTime; // Faz o número "flutuar" para cima
        this.tempoDeVida -= deltaTime; // Vai envelhecendo

        // Efeito de desaparecer (Fade out)
        this.cor.a = Math.max(0, tempoDeVida / TEMPO_MAXIMO);
    }

    public void renderizar(BitmapFont fonte, SpriteBatch batch) {
        fonte.setColor(cor);
        fonte.getData().setScale(1.2f); // Tamanho do número

        // Desenha o número negativo na tela (ex: -50)
        fonte.draw(batch, "-" + dano, x, y);

        // Reseta a cor da fonte pro jogo não bugar
        fonte.setColor(Color.WHITE);
    }

    // Retorna se o número já sumiu completamente
    public boolean deveSair() {
        return tempoDeVida <= 0;
    }
}
