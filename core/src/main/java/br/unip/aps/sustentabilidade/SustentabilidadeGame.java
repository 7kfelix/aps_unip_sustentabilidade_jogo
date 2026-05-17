package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; // IMPORTANTE IMPORTAR ISSO

public class SustentabilidadeGame extends Game {

    public SpriteBatch batch;
    public BitmapFont fonte;

    // NOVA FERRAMENTA DE DESENHO DE FORMAS
    public ShapeRenderer shape;

    @Override
    public void create() {
        batch = new SpriteBatch();
        fonte = new BitmapFont(Gdx.files.internal("comic.fnt"));
        fonte.getData().setScale(1.5f);
        shape = new ShapeRenderer();

        // --- CARREGA OS EFEITOS SONOROS NA MEMÓRIA ---
        GerenciadorAudio.inicializarSons();

        this.setScreen(new TelaMenu(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        fonte.dispose();
        shape.dispose(); // LIMPA A MEMÓRIA DA FERRAMENTA
    }
}
