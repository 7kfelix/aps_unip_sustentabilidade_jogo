package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AvisoFlutuante {
    public float x, y;
    public String texto;
    private float tempoDeVida;
    private final float TEMPO_MAXIMO = 1.5f; // Fica 1.5 segundos na tela
    private Color cor;

    public AvisoFlutuante(float x, float y, String texto, Color cor) {
        this.x = x;
        this.y = y;
        this.texto = texto;
        this.tempoDeVida = TEMPO_MAXIMO;
        this.cor = new Color(cor); // Copia a cor para fazer o efeito de sumir
    }

    public void atualizar(float deltaTime) {
        this.y += 40 * deltaTime; // Faz o texto flutuar para cima devagarinho
        this.tempoDeVida -= deltaTime;

        // Efeito de desaparecer (Fade out)
        this.cor.a = Math.max(0, tempoDeVida / TEMPO_MAXIMO);
    }

    public void renderizar(BitmapFont fonte, SpriteBatch batch) {
        fonte.setColor(cor);
        fonte.getData().setScale(1.5f);

        // Desenha a mensagem
        fonte.draw(batch, texto, x, y);

        // Reseta a cor da fonte
        fonte.setColor(Color.WHITE);
    }

    public boolean deveSair() {
        return tempoDeVida <= 0;
    }
}
