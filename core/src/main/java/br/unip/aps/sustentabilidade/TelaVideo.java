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

public class TelaVideo implements Screen {

    private SustentabilidadeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Screen telaAnterior;

    private Texture fundoConfig;
    private Texture caixaConfig;
    private Texture btnVoltar;
    private Texture btnTelaCheia; // O botão que você vai criar!

    private boolean telaCheia;

    public TelaVideo(SustentabilidadeGame game, Screen telaAnterior) {
        this.game = game;
        this.telaAnterior = telaAnterior;
        this.telaCheia = Gdx.graphics.isFullscreen();

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(1280 / 2f, 720 / 2f, 0);

        try { fundoConfig = new Texture("menuconfig.png"); } catch (Exception e) {}
        try { caixaConfig = new Texture("caixa_config.png"); } catch (Exception e) {}
        try { btnVoltar = new Texture("btn_voltar.png"); } catch (Exception e) {}

        // Carrega a sua futura arte
        try { btnTelaCheia = new Texture("tela_cheia.png"); } catch (Exception e) { System.out.println("Aviso: tela_cheia.png não encontrado"); }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);
        camera.update();

        Vector3 toque = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(toque);
        float mouseX = toque.x;
        float mouseY = toque.y;

        // Hitbox centralizada para o único botão de configuração
        boolean hoverTelaCheia = (mouseX >= 440 && mouseX <= 840 && mouseY >= 310 && mouseY <= 410);
        boolean hoverVoltar = (mouseX >= 540 && mouseX <= 740 && mouseY >= 130 && mouseY <= 190);

        if (Gdx.input.justTouched()) {
            if (hoverTelaCheia) {
                telaCheia = !telaCheia;
                if (telaCheia) {
                    // Pega a resolução máxima do monitor e aplica
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    // Volta para o modo janela padrão
                    Gdx.graphics.setWindowedMode(1280, 720);
                }
            }
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

        // 2. DESENHA BOTÃO TELA CHEIA (Com efeito Hover)
        if (btnTelaCheia != null) {
            game.batch.setColor(hoverTelaCheia ? Color.LIGHT_GRAY : Color.WHITE);
            // Posicionei bem no meio da placa
            game.batch.draw(btnTelaCheia, 460, 320, 360, 80);
        } else {
            // Caso falte a imagem, mostra o texto temporário
            game.fonte.getData().setScale(1.5f);
            game.fonte.setColor(hoverTelaCheia ? Color.LIGHT_GRAY : Color.WHITE);
            game.fonte.draw(game.batch, "TELA CHEIA", 510, 370);
        }

        // 3. DESENHA BOTÃO VOLTAR
        if (btnVoltar != null) {
            game.batch.setColor(hoverVoltar ? Color.LIGHT_GRAY : Color.WHITE);
            game.batch.draw(btnVoltar, 540, 140, 200, 60);
        } else {
            game.fonte.getData().setScale(1.5f);
            game.fonte.setColor(hoverVoltar ? Color.WHITE : Color.YELLOW);
            game.fonte.draw(game.batch, "<< VOLTAR", 570, 180);
        }

        game.batch.setColor(Color.WHITE); // Reseta a cor

        game.batch.end();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void show() {} @Override public void pause() {} @Override public void resume() {} @Override public void hide() {}

    @Override
    public void dispose() {
        if (fundoConfig != null) fundoConfig.dispose();
        if (caixaConfig != null) caixaConfig.dispose();
        if (btnVoltar != null) btnVoltar.dispose();
        if (btnTelaCheia != null) btnTelaCheia.dispose();
    }
}
