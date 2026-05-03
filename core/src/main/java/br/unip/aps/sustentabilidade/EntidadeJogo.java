package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class EntidadeJogo {
    protected float x;
    protected float y;
    protected Texture textura;

    // NOVA FLAG DE ESTADO
    protected boolean ativo;

    public EntidadeJogo(float x, float y, Texture textura) {
        this.x = x;
        this.y = y;
        this.textura = textura;
        this.ativo = true; // Toda entidade nasce viva
    }

    public abstract void atualizar(float deltaTime);

    public void renderizar(SpriteBatch batch) {
        if (textura != null && ativo) {
            batch.draw(textura, x, y);
        }
    }

    public void destruir() {
        if (textura != null) {
            textura.dispose();
        }
    }

    // GETTER PARA O SISTEMA LER
    public boolean isAtivo() {
        return ativo;
    }
}
