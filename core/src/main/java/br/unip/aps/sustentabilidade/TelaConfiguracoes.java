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

    // MATRIZ DE RESOLUÇÕES DISPONÍVEIS
    private int[][] resolucoes = {
        {800, 600},
        {1280, 720},
        {1600, 900},
        {1920, 1080}
    };

    // VARIÁVEIS DE ESTADO
    private int indiceResolucaoAtual = 1; // Começa no 1280x720
    private boolean telaCheia = false;

    public TelaConfiguracoes(SustentabilidadeGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(1280 / 2f, 720 / 2f, 0);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.2f, 0.3f, 1f); // Fundo azul escuro para diferenciar do Menu

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.fonte.setColor(Color.WHITE);

        // TÍTULO
        game.fonte.getData().setScale(2.0f);
        game.fonte.draw(game.batch, "CONFIGURACOES", 450, 650);

        // OPÇÕES
        game.fonte.getData().setScale(1.5f);

        String textoResolucao = "1. Resolucao: " + resolucoes[indiceResolucaoAtual][0] + "x" + resolucoes[indiceResolucaoAtual][1];
        game.fonte.draw(game.batch, textoResolucao, 400, 500);

        String textoModo = "2. Modo: " + (telaCheia ? "Tela Cheia" : "Janela");
        game.fonte.draw(game.batch, textoModo, 400, 400);

        // Espaço reservado para o futuro (cinza para indicar que está desativado)
        game.fonte.setColor(Color.GRAY);
        game.fonte.draw(game.batch, "3. Som: Ligado (Em breve)", 400, 300);

        // BOTÃO DE VOLTAR
        game.fonte.setColor(Color.YELLOW);
        game.fonte.draw(game.batch, "4. Voltar ao Menu Principal", 400, 150);

        game.batch.end();

        // LÓGICA DE CLIQUES
        if (Gdx.input.justTouched()) {
            Vector3 toque = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(toque);

            // Clicou na Resolução (Y entre 450 e 550)
            if (toque.y > 450 && toque.y < 550) {
                // Avança na lista de resoluções e volta ao zero se passar do limite
                indiceResolucaoAtual++;
                if (indiceResolucaoAtual >= resolucoes.length) {
                    indiceResolucaoAtual = 0;
                }
                aplicarConfiguracoesGraficas();
            }

            // Clicou no Modo de Tela (Y entre 350 e 450)
            else if (toque.y > 350 && toque.y < 450) {
                telaCheia = !telaCheia; // Inverte o estado
                aplicarConfiguracoesGraficas();
            }

            // Clicou em Voltar (Y entre 100 e 200)
            else if (toque.y > 100 && toque.y < 200) {
                game.setScreen(new TelaMenu(game));
                dispose();
            }
        }
    }

    // METODO QUE FALA COM O SISTEMA OPERACIONAL (Windows/Mac/Linux)
    private void aplicarConfiguracoesGraficas() {
        if (telaCheia) {
            // Pega a resolução máxima do monitor do usuário
            DisplayMode displayMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(displayMode);
        } else {
            // Redimensiona a janela para os valores do nosso array
            int largura = resolucoes[indiceResolucaoAtual][0];
            int altura = resolucoes[indiceResolucaoAtual][1];
            Gdx.graphics.setWindowedMode(largura, altura);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
