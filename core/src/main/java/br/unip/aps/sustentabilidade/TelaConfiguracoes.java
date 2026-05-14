package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TelaConfiguracoes implements Screen {

    private SustentabilidadeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    // A MÁGICA AQUI: Guarda a tela que chamou as configurações
    private Screen telaAnterior;

    private int[][] resolucoes = {
        {800, 600},
        {1280, 720},
        {1600, 900},
        {1920, 1080}
    };

    private int indiceResolucaoAtual = 1;
    private boolean telaCheia = false;

    // CONSTRUTOR ATUALIZADO: Agora exige saber quem é a telaAnterior
    public TelaConfiguracoes(SustentabilidadeGame game, Screen telaAnterior) {
        this.game = game;
        this.telaAnterior = telaAnterior;

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(1280 / 2f, 720 / 2f, 0);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.2f, 0.3f, 1f);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.fonte.setColor(Color.WHITE);

        game.fonte.getData().setScale(2.0f);
        game.fonte.draw(game.batch, "CONFIGURACOES", 450, 650);

        game.fonte.getData().setScale(1.5f);

        String textoResolucao = "1. Resolucao: " + resolucoes[indiceResolucaoAtual][0] + "x" + resolucoes[indiceResolucaoAtual][1];
        game.fonte.draw(game.batch, textoResolucao, 400, 500);

        String textoModo = "2. Modo: " + (telaCheia ? "Tela Cheia" : "Janela");
        game.fonte.draw(game.batch, textoModo, 400, 400);

        game.fonte.setColor(Color.GRAY);
        game.fonte.draw(game.batch, "3. Som: Ligado (Em breve)", 400, 300);

        // BOTÃO DE VOLTAR: Mudei o texto para ficar genérico, já que pode voltar pro jogo agora
        game.fonte.setColor(Color.YELLOW);
        game.fonte.draw(game.batch, "4. Voltar", 400, 150);

        game.batch.end();

        if (Gdx.input.justTouched()) {
            Vector3 toque = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(toque);

            if (toque.y > 450 && toque.y < 550) {
                indiceResolucaoAtual++;
                if (indiceResolucaoAtual >= resolucoes.length) {
                    indiceResolucaoAtual = 0;
                }
                aplicarConfiguracoesGraficas();
            }

            else if (toque.y > 350 && toque.y < 450) {
                telaCheia = !telaCheia;
                aplicarConfiguracoesGraficas();
            }

            else if (toque.y > 100 && toque.y < 200) {
                // VOLTA PARA A TELA QUE CHAMOU ELA (Menu ou Jogo)
                game.setScreen(telaAnterior);
                dispose(); // Limpa as configs da memória
            }
        }
    }

    private void aplicarConfiguracoesGraficas() {
        if (telaCheia) {
            DisplayMode displayMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(displayMode);
        } else {
            int largura = resolucoes[indiceResolucaoAtual][0];
            int altura = resolucoes[indiceResolucaoAtual][1];
            Gdx.graphics.setWindowedMode(largura, altura);
        }
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }

    @Override public void show() {}

    @Override public void pause() {}

    @Override public void resume() {}

    @Override public void hide() {}

    @Override public void dispose() {}

}
