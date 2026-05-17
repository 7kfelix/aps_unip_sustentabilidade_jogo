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

public class TelaSobre implements Screen {

    private SustentabilidadeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Screen telaAnterior;

    private Texture fundoConfig;
    private Texture caixaConfig;
    private Texture btnVoltar;

    public TelaSobre(SustentabilidadeGame game, Screen telaAnterior) {
        this.game = game;
        this.telaAnterior = telaAnterior;

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(1280 / 2f, 720 / 2f, 0);

        // Reaproveitando as artes da tela de configurações!
        try { fundoConfig = new Texture("menuconfig.png"); } catch (Exception e) {}
        try { caixaConfig = new Texture("menusobre.png"); } catch (Exception e) {}
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

        // Hitbox do botão voltar
        boolean hoverVoltar = (mouseX >= 540 && mouseX <= 740 && mouseY >= 140 && mouseY <= 200);

        if (Gdx.input.justTouched()) {
            if (hoverVoltar) {
                if (telaAnterior != null) game.setScreen(telaAnterior);
                else game.setScreen(new TelaMenu(game));
                dispose();
            }
        }

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // 1. DESENHA FUNDO E PAINEL
        if (fundoConfig != null) game.batch.draw(fundoConfig, 0, 0, 1280, 720);
        if (caixaConfig != null) game.batch.draw(caixaConfig, 340, 100, 600, 520);

        // 2. DESENHA O BOTÃO VOLTAR
        if (btnVoltar != null) {
            game.batch.setColor(hoverVoltar ? Color.LIGHT_GRAY : Color.WHITE);
            game.batch.draw(btnVoltar, 540, 140, 200, 60);
        } else {
            game.fonte.getData().setScale(1.5f);
            game.fonte.setColor(hoverVoltar ? Color.WHITE : Color.YELLOW);
            game.fonte.draw(game.batch, "<< VOLTAR", 570, 180);
        }

        game.batch.setColor(Color.WHITE); // Limpa as cores

        // 3. TEXTOS DOS DESENVOLVEDORES
        game.fonte.setColor(Color.YELLOW);
        game.fonte.getData().setScale(2.0f);
        game.fonte.draw(game.batch, "DESENVOLVEDORES", 490, 560);

        game.fonte.setColor(Color.WHITE);
        game.fonte.getData().setScale(1.5f);
        game.fonte.draw(game.batch, "Projeto APS - Ciencia da Computacao - UNIP", 370, 500);

        // === LISTA DE INTEGRANTES AQUI ===
        game.fonte.setColor(Color.LIGHT_GRAY);
        game.fonte.getData().setScale(1.3f);

        // Substitua os dados dos seus amigos!
        game.fonte.draw(game.batch, "Marcelo Felix do Vale - RA H752116 - CC3P13", 400, 430);
        game.fonte.draw(game.batch, "Nome do Amigo 2 - RA 123456", 400, 380);
        game.fonte.draw(game.batch, "Nome do Amigo 3 - RA 123456", 400, 330);
        game.fonte.draw(game.batch, "Nome do Amigo 4 - RA 123456", 400, 280);

        game.batch.end();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void show() {} @Override public void pause() {} @Override public void resume() {} @Override public void hide() {}

    @Override
    public void dispose() {
        if (fundoConfig != null) fundoConfig.dispose();
        if (caixaConfig != null) caixaConfig.dispose();
        if (btnVoltar != null) btnVoltar.dispose();
    }
}
