package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class SustentabilidadeGame extends ApplicationAdapter {

    private SpriteBatch batch;
    private List<EntidadeJogo> entidades;
    private Texture texturaPoluicao;
    private Texture texturaTorre;

    // NOSSO NOVO GERENCIADOR
    private GeradorDeOndas geradorDeOndas;

    @Override
    public void create() {
        batch = new SpriteBatch();
        entidades = new ArrayList<>();

        texturaPoluicao = new Texture("libgdx.png");
        texturaTorre = new Texture("libgdx.png");

        List<Vector2> rotaDoMapa = new ArrayList<>();
        rotaDoMapa.add(new Vector2(300, 200));
        rotaDoMapa.add(new Vector2(300, 400));
        rotaDoMapa.add(new Vector2(600, 400));

        // EM VEZ DE CRIAR O INIMIGO AQUI, CRIAMOS O GERADOR
        geradorDeOndas = new GeradorDeOndas(entidades, rotaDoMapa, texturaPoluicao);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.1f, 0.3f, 0.15f, 1f);
        float deltaTime = Gdx.graphics.getDeltaTime();

        // CONSTRUÇÃO DE TORRES
        if (Gdx.input.justTouched()) {
            float cliqueX = Gdx.input.getX();
            float cliqueY = Gdx.graphics.getHeight() - Gdx.input.getY();
            Torre novaTorre = new Torre(cliqueX, cliqueY, texturaTorre, entidades);
            entidades.add(novaTorre);
        }

        // 0. ATUALIZA O GERADOR DE ONDAS (Ele vai jogar inimigos na lista no tempo certo)
        geradorDeOndas.atualizar(deltaTime);

        // 1. FASE DE LÓGICA (Movimento e Tiros)
        for (int i = 0; i < entidades.size(); i++) {
            entidades.get(i).atualizar(deltaTime);
        }

        // 1.5 FASE DE FAXINA (Garbage Collector Manual)
        for (int i = entidades.size() - 1; i >= 0; i--) {
            if (!entidades.get(i).isAtivo()) {
                entidades.remove(i);
            }
        }

        // 2. FASE DE RENDERIZAÇÃO
        batch.begin();
        for (int i = 0; i < entidades.size(); i++) {
            entidades.get(i).renderizar(batch);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        texturaPoluicao.dispose();
        texturaTorre.dispose();
        for (EntidadeJogo entidade : entidades) {
            entidade.destruir();
        }
    }
}
