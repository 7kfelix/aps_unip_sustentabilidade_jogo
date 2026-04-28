package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class EntidadeJogo {
    // Posição no mapa (x, y)
    protected float x;
    protected float y;

    // A imagem que representa a entidade
    protected Texture textura;

    public EntidadeJogo(float x, float y, Texture textura) {
        this.x = x;
        this.y = y;
        this.textura = textura;
    }

    // Toda classe filha (Torre, Inimigo) será obrigada a dizer como se comporta a cada frame
    public abstract void atualizar(float deltaTime);

    // O metodo renderizar apenas desenha a textura na tela usando a placa de vídeo
    public void renderizar(SpriteBatch batch) {
        if (textura != null) {
            batch.draw(textura, x, y);
        }
    }

    // Libera a memória da placa de vídeo quando o objeto for destruído
    public void destruir() {
        if (textura != null) {
            textura.dispose();
        }
    }
}
