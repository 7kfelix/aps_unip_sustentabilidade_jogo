package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TelaMenu implements Screen {

    private SustentabilidadeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture fundoMenu;
    private Texture logoPrincipal;
    private Texture btnJogar;
    private Texture btnEngrenagem;
    private Texture btnExclamacao;
    private Texture btnFechar;

    public TelaMenu(SustentabilidadeGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(1280 / 2f, 720 / 2f, 0);

        try { fundoMenu = new Texture("menu.png"); } catch (Exception e) {}
        try { logoPrincipal = new Texture("logo.png"); } catch (Exception e) {}
        try { btnJogar = new Texture("jogar.png"); } catch (Exception e) {}
        try { btnEngrenagem = new Texture("engrenagem.png"); } catch (Exception e) {}
        try { btnExclamacao = new Texture("exclamacao.png"); } catch (Exception e) {}
        try { btnFechar = new Texture("fechar.png"); } catch (Exception e) {}

        // --- CHAMA O GERENCIADOR PARA TOCAR A MÚSICA GLOBAL ---
        GerenciadorAudio.tocarMusicaMenu();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();

        Vector3 toque = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(toque);
        float mouseX = toque.x;
        float mouseY = toque.y;

        boolean hoverJogar = (mouseX >= 540 && mouseX <= 740 && mouseY >= 100 && mouseY <= 180);
        boolean hoverConfig = (mouseX >= 20 && mouseX <= 80 && mouseY >= 640 && mouseY <= 700);
        boolean hoverSobre = (mouseX >= 100 && mouseX <= 160 && mouseY >= 640 && mouseY <= 700);
        boolean hoverFechar = (mouseX >= 1200 && mouseX <= 1260 && mouseY >= 640 && mouseY <= 700);

        if (Gdx.input.justTouched()) {
            GerenciadorAudio.tocarSom(GerenciadorAudio.somInserir);
            if (hoverJogar) {
                game.setScreen(new TelaTutorial(game));
                dispose();
            } else if (hoverConfig) {
                game.setScreen(new TelaConfiguracoes(game, null));
                dispose();
            } else if (hoverSobre) {
                game.setScreen(new TelaSobre(game, this));
            } else if (hoverFechar) {
                Gdx.app.exit();
            }
        }

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        if (fundoMenu != null) game.batch.draw(fundoMenu, 0, 0, 1280, 720);
        if (logoPrincipal != null) game.batch.draw(logoPrincipal, 240, 420, 800, 250);

        if (btnJogar != null) {
            game.batch.setColor(hoverJogar ? Color.LIGHT_GRAY : Color.WHITE);
            game.batch.draw(btnJogar, 540, 100, 200, 80);
        }

        if (btnEngrenagem != null) {
            game.batch.setColor(hoverConfig ? Color.LIGHT_GRAY : Color.WHITE);
            game.batch.draw(btnEngrenagem, 20, 640, 60, 60);
        }

        if (btnExclamacao != null) {
            game.batch.setColor(hoverSobre ? Color.LIGHT_GRAY : Color.WHITE);
            game.batch.draw(btnExclamacao, 100, 640, 60, 60);
        }

        if (btnFechar != null) {
            game.batch.setColor(hoverFechar ? new Color(1f, 0.4f, 0.4f, 1f) : Color.WHITE);
            game.batch.draw(btnFechar, 1200, 640, 60, 60);
        }

        game.batch.setColor(Color.WHITE);
        game.batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);

        if (btnExclamacao == null) {
            game.shape.setColor(hoverSobre ? new Color(0.4f, 0.4f, 0.4f, 0.9f) : new Color(0.2f, 0.2f, 0.2f, 0.8f));
            game.shape.rect(100, 640, 60, 60);
        }

        if (btnFechar == null) {
            game.shape.setColor(hoverFechar ? new Color(0.8f, 0.2f, 0.2f, 0.9f) : new Color(0.5f, 0.1f, 0.1f, 0.8f));
            game.shape.rect(1200, 640, 60, 60);
        }

        game.shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.batch.begin();

        if (btnExclamacao == null) {
            game.fonte.getData().setScale(2.0f);
            game.fonte.setColor(Color.WHITE);
            game.fonte.draw(game.batch, "!", 122, 685);
        }

        if (btnFechar == null) {
            game.fonte.getData().setScale(2.0f);
            game.fonte.setColor(Color.WHITE);
            game.fonte.draw(game.batch, "X", 1220, 685);
        }

        if (btnJogar == null) {
            game.fonte.getData().setScale(2.5f);
            game.fonte.draw(game.batch, "JOGAR", 580, 155);
        }

        game.batch.end();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void show() {} @Override public void pause() {} @Override public void resume() {} @Override public void hide() {}

    @Override
    public void dispose() {
        if (fundoMenu != null) fundoMenu.dispose();
        if (logoPrincipal != null) logoPrincipal.dispose();
        if (btnJogar != null) btnJogar.dispose();
        if (btnEngrenagem != null) btnEngrenagem.dispose();
        if (btnExclamacao != null) btnExclamacao.dispose();
        if (btnFechar != null) btnFechar.dispose();
    }
}
