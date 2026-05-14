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

    // --- TEXTURAS DO MENU ---
    private Texture fundoMenu;
    private Texture btnJogar;
    private Texture btnEngrenagem;
    private Texture btnExclamacao; // NOVO ÍCONE!

    private boolean mostrarSobre = false;

    public TelaMenu(SustentabilidadeGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(1280 / 2f, 720 / 2f, 0);

        try { fundoMenu = new Texture("menu.png"); } catch (Exception e) { System.out.println("Aviso: menu.png não encontrado."); }
        try { btnJogar = new Texture("jogar.png"); } catch (Exception e) { System.out.println("Aviso: jogar.png não encontrado."); }
        try { btnEngrenagem = new Texture("engrenagem.png"); } catch (Exception e) { System.out.println("Aviso: engrenagem.png não encontrado."); }
        try { btnExclamacao = new Texture("exclamacao.png"); } catch (Exception e) { System.out.println("Aviso: exclamacao.png não encontrado."); }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();

        Vector3 toque = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(toque);
        float mouseX = toque.x;
        float mouseY = toque.y;

        // --- ÁREAS DE CLIQUE DOS BOTÕES ---
        boolean hoverJogar = (mouseX >= 540 && mouseX <= 740 && mouseY >= 100 && mouseY <= 180);
        boolean hoverConfig = (mouseX >= 20 && mouseX <= 80 && mouseY >= 640 && mouseY <= 700);
        boolean hoverSobre = (mouseX >= 100 && mouseX <= 160 && mouseY >= 640 && mouseY <= 700);

        // --- LÓGICA DE CLIQUE ---
        if (Gdx.input.justTouched()) {
            if (mostrarSobre) {
                mostrarSobre = false;
            } else {
                if (hoverJogar) {
                    game.setScreen(new TelaJogo(game));
                    dispose();
                } else if (hoverConfig) {
                    game.setScreen(new TelaConfiguracoes(game, null));
                } else if (hoverSobre) {
                    mostrarSobre = true;
                }
            }
        }

        // ==========================================
        // RENDERIZAÇÃO
        // ==========================================

        // 1. DESENHA O FUNDO E AS ARTES (SPRITES)
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        if (fundoMenu != null) game.batch.draw(fundoMenu, 0, 0, 1280, 720);

        // Botão JOGAR com efeito Hover (muda a cor da imagem)
        if (btnJogar != null) {
            game.batch.setColor(hoverJogar ? Color.LIGHT_GRAY : Color.WHITE);
            game.batch.draw(btnJogar, 540, 100, 200, 80);
        }

        // Botão ENGRENAGEM com efeito Hover
        if (btnEngrenagem != null) {
            game.batch.setColor(hoverConfig ? Color.LIGHT_GRAY : Color.WHITE);
            game.batch.draw(btnEngrenagem, 20, 640, 60, 60);
        }

        // Botão EXCLAMAÇÃO com efeito Hover
        if (btnExclamacao != null) {
            game.batch.setColor(hoverSobre ? Color.LIGHT_GRAY : Color.WHITE);
            game.batch.draw(btnExclamacao, 100, 640, 60, 60);
        }

        game.batch.setColor(Color.WHITE); // Reseta a cor para não bugar o resto!
        game.batch.end();

        // 2. DESENHA AS FORMAS GEOMÉTRICAS RESTANTES (Pop-up e suporte)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);

        // Se a arte da exclamação falhar, desenha o quadrado de fundo como segurança
        if (btnExclamacao == null) {
            game.shape.setColor(hoverSobre ? new Color(0.4f, 0.4f, 0.4f, 0.9f) : new Color(0.2f, 0.2f, 0.2f, 0.8f));
            game.shape.rect(100, 640, 60, 60);
        }

        // Fundo do Pop-up Sobre
        if (mostrarSobre) {
            game.shape.setColor(0, 0, 0, 0.85f);
            game.shape.rect(0, 0, 1280, 720);

            game.shape.setColor(0.1f, 0.1f, 0.15f, 1f);
            game.shape.rect(340, 200, 600, 350);

            game.shape.set(ShapeRenderer.ShapeType.Line);
            game.shape.setColor(Color.WHITE);
            game.shape.rect(340, 200, 600, 350);
        }

        game.shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // 3. TEXTOS
        game.batch.begin();

        // Só desenha o "!" de texto se não tiver achado a imagem
        if (btnExclamacao == null) {
            game.fonte.getData().setScale(2.0f);
            game.fonte.setColor(Color.WHITE);
            game.fonte.draw(game.batch, "!", 122, 685);
        }

        if (btnJogar == null) {
            game.fonte.getData().setScale(2.5f);
            game.fonte.draw(game.batch, "JOGAR", 580, 155);
        }

        if (mostrarSobre) {
            game.fonte.setColor(Color.YELLOW);
            game.fonte.getData().setScale(2.0f);
            game.fonte.draw(game.batch, "DESENVOLVEDORES", 480, 500);

            game.fonte.setColor(Color.WHITE);
            game.fonte.getData().setScale(1.5f);
            game.fonte.draw(game.batch, "1. Seu Nome - RA / Função", 400, 420);
            game.fonte.draw(game.batch, "2. Nome do Amigo - RA / Função", 400, 370);
            game.fonte.draw(game.batch, "3. Nome do Amigo - RA / Função", 400, 320);

            game.fonte.setColor(Color.LIGHT_GRAY);
            game.fonte.getData().setScale(1.0f);
            game.fonte.draw(game.batch, "(Clique em qualquer lugar para fechar)", 470, 240);
        }

        game.batch.end();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (fundoMenu != null) fundoMenu.dispose();
        if (btnJogar != null) btnJogar.dispose();
        if (btnEngrenagem != null) btnEngrenagem.dispose();
        if (btnExclamacao != null) btnExclamacao.dispose();
    }
}
