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

    // Lista que vai guardar todas as coisas que existem na tela do jogo;
    private List<EntidadeJogo> entidades;

    @Override
    public void create() {
        batch = new SpriteBatch();
        entidades = new ArrayList<>();

        Texture texturaPoluicao = new Texture("libgdx.png");

        // 1. Desenhamos a rota do mapa (Waypoints)
        // O inimigo começa fora da tela, vai pro meio, sobe, e vai pro canto direito
        List<Vector2> rotaDoMapa = new ArrayList<>();
        rotaDoMapa.add(new Vector2(300, 200)); // Ponto 1: Anda para a direita
        rotaDoMapa.add(new Vector2(300, 400)); // Ponto 2: Faz a curva para cima
        rotaDoMapa.add(new Vector2(600, 400)); // Ponto 3: Faz a curva para a direita

        // 2. Instanciamos o inimigo passando a posição inicial (0, 200) e a rota
        Inimigo primeiroInimigo = new Inimigo(0, 200, texturaPoluicao, rotaDoMapa);

        entidades.add(primeiroInimigo);
    }

    @Override
    public void render() {
        // Limpa a tela a cada frame (colocamos um tom de verde musgo para o tema sustentável)
        ScreenUtils.clear(0.1f, 0.3f, 0.15f, 1f);

        // deltaTime é a fração de segundo desde o último frame (ex: 0.016s se estiver a 60 FPS)
        float deltaTime = Gdx.graphics.getDeltaTime();

        // 1. FASE DE LÓGICA: Atualiza posição, colisão e vida
        for (EntidadeJogo entidade : entidades) {
            entidade.atualizar(deltaTime);
        }

        // 2. FASE DE RENDERIZAÇÃO: Manda desenhar na tela
        batch.begin();
        for (EntidadeJogo entidade : entidades) {
            entidade.renderizar(batch);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        // Limpa a memória das texturas quando o jogador fechar o jogo
        for (EntidadeJogo entidade : entidades) {
            entidade.destruir();
        }
    }
}
