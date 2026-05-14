package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TelaSom implements Screen {

    private SustentabilidadeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Screen telaAnterior;

    private Texture fundoConfig;
    private Texture caixaConfig;
    private Texture btnVoltar;

    private static int volume = 100;

    public TelaSom(SustentabilidadeGame game, Screen telaAnterior) {
        this.game = game;
        this.telaAnterior = telaAnterior;

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(1280 / 2f, 720 / 2f, 0);

        try { fundoConfig = new Texture("menuconfig.png"); } catch (Exception e) {}
        try { caixaConfig = new Texture("caixa_config.png"); } catch (Exception e) {}
        try { btnVoltar = new Texture("btn_voltar.png"); } catch (Exception e) {}
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);
        camera.update();

        Vector3 toque = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(toque);
        float mouseX = toque.x;
        float mouseY = toque.y;

        boolean hoverMenos = (mouseX >= 440 && mouseX <= 540 && mouseY >= 380 && mouseY <= 460);
        boolean hoverMais = (mouseX >= 740 && mouseX <= 840 && mouseY >= 380 && mouseY <= 460);
        boolean hoverVoltar = (mouseX >= 540 && mouseX <= 740 && mouseY >= 140 && mouseY <= 200);

        if (Gdx.input.justTouched()) {
            if (hoverMenos && volume > 0) volume -= 10;
            else if (hoverMais && volume < 100) volume += 10;
            else if (hoverVoltar) {
                game.setScreen(new TelaConfiguracoes(game, telaAnterior));
                dispose();
            }
        }

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // 1. DESENHA FUNDO E PAINEL
        if (fundoConfig != null) game.batch.draw(fundoConfig, 0, 0, 1280, 720);
        if (caixaConfig != null) game.batch.draw(caixaConfig, 340, 100, 600, 520);

        // 2. DESENHA BOTÃO VOLTAR
        if (btnVoltar != null) {
            game.batch.setColor(hoverVoltar ? Color.LIGHT_GRAY : Color.WHITE);
            game.batch.draw(btnVoltar, 540, 140, 200, 60);
        } else {
            game.fonte.getData().setScale(1.5f);
            game.fonte.setColor(hoverVoltar ? Color.WHITE : Color.YELLOW);
            game.fonte.draw(game.batch, "<< VOLTAR", 570, 180);
        }

        game.batch.setColor(Color.WHITE); // Reseta a cor

        // 3. TEXTOS DE SOM (Sinais - e + com efeito Hover)
        game.fonte.getData().setScale(4.0f); // Tamanho grande pros botões

        game.fonte.setColor(hoverMenos ? Color.LIGHT_GRAY : Color.WHITE);
        game.fonte.draw(game.batch, "-", 480, 440);

        game.fonte.setColor(hoverMais ? Color.LIGHT_GRAY : Color.WHITE);
        game.fonte.draw(game.batch, "+", 760, 440);

        game.fonte.getData().setScale(2.5f);
        game.fonte.setColor(volume == 0 ? Color.RED : Color.GREEN);
        game.fonte.draw(game.batch, volume + "%", 590, 430);

        game.batch.end();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void show() {} @Override public void pause() {} @Override public void resume() {} @Override public void hide() {}
    @Override public void dispose() {
        if (fundoConfig != null) fundoConfig.dispose();
        if (caixaConfig != null) caixaConfig.dispose();
        if (btnVoltar != null) btnVoltar.dispose();
    }
}
