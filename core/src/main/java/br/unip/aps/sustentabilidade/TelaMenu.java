package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TelaMenu implements Screen {

    private SustentabilidadeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    public TelaMenu(SustentabilidadeGame game) {
        this.game = game;

        // Criamos um mundo virtual fixo de 1280x720
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(1280 / 2f, 720 / 2f, 0);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.2f, 0.1f, 1f); // Fundo verde escuro
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.fonte.setColor(Color.WHITE);
        game.fonte.getData().setScale(2.5f);
        game.fonte.draw(game.batch, "DEFENSORES DO AMANHA", 400, 600);

        game.fonte.getData().setScale(1.5f);
        game.fonte.draw(game.batch, "1. Iniciar Jogo (Clique aqui)", 500, 400);
        game.fonte.draw(game.batch, "2. Configuracoes", 500, 300);
        game.fonte.draw(game.batch, "3. Sair", 500, 200);
        game.batch.end();

        // LÓGICA DE CLIQUE USANDO A CÂMERA
        if (Gdx.input.justTouched()) {
            Vector3 toque = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(toque);

            // Verifica se clicou no "Iniciar Jogo" (Y entre 350 e 450)
            if (toque.y > 350 && toque.y < 450) {
                game.setScreen(new TelaJogo(game));
                dispose();
            }
            // NOVO: Verifica se clicou no "Configurações" (Y entre 250 e 350)
            else if (toque.y > 250 && toque.y < 350) {
                game.setScreen(new TelaConfiguracoes(game, this));
            }

            // Verifica se clicou no "Sair" (Y entre 150 e 250)
            else if (toque.y > 150 && toque.y < 250) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    // Métodos obrigatórios da interface Screen (podem ficar vazios)
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
