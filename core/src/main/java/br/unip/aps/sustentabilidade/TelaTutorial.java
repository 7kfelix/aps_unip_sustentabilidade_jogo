package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TelaTutorial implements Screen {

    private SustentabilidadeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture texturaMapa;

    // --- VARIÁVEIS DA ANIMAÇÃO ---
    private Texture spriteSheetNPC;
    private Animation<TextureRegion> animacaoNPC;
    private float tempoDaAnimacao; // Relógio da animação

    private int paginaAtual = 0;

    // As falas do seu tutorial! Você pode adicionar quantas quiser.
    private String[] dialogos = {
        "Ola, Defensor! O parque esta sob o ataque\nde poluidores. Precisamos da sua ajuda!",
        "O seu objetivo e simples: use suas Eco-Moedas\npara construir defesas e impedir o avanco deles.",
        "Clique no botao '>> Loja' no canto direito para\nabrir o menu de construcao e ver as torres.",
        "Cada torre tem uma funcao: A Sementeira atira \nde longe, o Filtro limpa em area...",
        "...A Planta prende inimigos, o Bambu da muito dano \ne a Arvore Ancia gera mais dinheiro com o tempo!",
        "Voce tambem pode comprar Upgrades na loja \npara melhorar as suas torres ja construidas.",
        "DICA: Se precisar de espaco ou dinheiro, clique \ncom o BOTAO DIREITO em uma torre para vende-la.",
        "A natureza conta com voce. Boa sorte!"
    };

    public TelaTutorial(SustentabilidadeGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(1280 / 2f, 720 / 2f, 0);

        tempoDaAnimacao = 0f;

        try { texturaMapa = new Texture("mapa.jpg"); } catch (Exception e) {}

        // --- CARREGANDO E CORTANDO O SPRITE SHEET ---
        try {
            spriteSheetNPC = new Texture("npc_tutorial.png");

            // Corta a imagem de 1200x800 em pedaços de 600x800
            TextureRegion[][] framesTemporarios = TextureRegion.split(spriteSheetNPC, 600, 800);

            // Pega a primeira linha de frames. Velocidade de 0.4 segundos por frame.
            animacaoNPC = new Animation<>(0.4f, framesTemporarios[0]);
            animacaoNPC.setPlayMode(Animation.PlayMode.LOOP);

        } catch (Exception e) {
            System.out.println("Aviso: npc_tutorial.png não encontrado.");
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();

        // --- ATUALIZA O RELÓGIO DA ANIMAÇÃO ---
        tempoDaAnimacao += delta;

        // --- LÓGICA DE AVANÇAR O TEXTO ---
        if (Gdx.input.justTouched()) {
            GerenciadorAudio.tocarSom(GerenciadorAudio.somInserir);
            paginaAtual++;

            // Se as falas acabaram, vai para o jogo!
            if (paginaAtual >= dialogos.length) {
                game.setScreen(new TelaJogo(game));
                dispose();
                return;
            }
        }

        // 1. DESENHA O MAPA NO FUNDO
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        if (texturaMapa != null) {
            game.batch.draw(texturaMapa, 0, 0, 1280, 720);
        }
        game.batch.end();

        // 2. ESCURECE A TELA PARA DESTACAR O TUTORIAL
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);

        // Fundo escuro geral
        game.shape.setColor(0, 0, 0, 0.6f);
        game.shape.rect(0, 0, 1280, 720);

        // Caixa de Diálogo (Estilo RPG) na parte de baixo
        game.shape.setColor(0.1f, 0.1f, 0.15f, 0.9f);
        game.shape.rect(50, 50, 1180, 200);

        game.shape.end();

        // Borda branca para a caixa de diálogo
        Gdx.gl.glLineWidth(3);
        game.shape.begin(ShapeRenderer.ShapeType.Line);
        game.shape.setColor(Color.WHITE);
        game.shape.rect(50, 50, 1180, 200);
        game.shape.end();
        Gdx.gl.glLineWidth(1);

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // 3. DESENHA O PERSONAGEM E OS TEXTOS
        game.batch.begin();

        // --- DESENHA A ANIMAÇÃO DA GUIA ---
        if (animacaoNPC != null) {
            // Pede o frame correto baseado no tempo
            TextureRegion frameAtual = animacaoNPC.getKeyFrame(tempoDaAnimacao);

            // Desenha com 300x400 (metade do tamanho original para ficar em HD)
            game.batch.draw(frameAtual, 80, 250, 300, 400);
        }

        // Título de quem está falando
        game.fonte.setColor(Color.YELLOW);
        game.fonte.getData().setScale(1.8f);
        game.fonte.draw(game.batch, "A Guia", 80, 230);

        // O Texto do Diálogo Atual
        game.fonte.setColor(Color.WHITE);
        game.fonte.getData().setScale(2.0f);
        game.fonte.draw(game.batch, dialogos[paginaAtual], 80, 180);

        // Instrução para avançar (piscando suavemente)
        float alphaBlink = (float) (Math.sin(System.currentTimeMillis() / 200.0) + 1) / 2f;
        game.fonte.setColor(new Color(1f, 1f, 1f, alphaBlink));
        game.fonte.getData().setScale(1.2f);
        game.fonte.draw(game.batch, "Clique na tela para continuar >>", 900, 90);

        game.batch.end();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void show() {} @Override public void pause() {} @Override public void resume() {} @Override public void hide() {}

    @Override
    public void dispose() {
        if (texturaMapa != null) texturaMapa.dispose();
        if (spriteSheetNPC != null) spriteSheetNPC.dispose(); // Limpa a folha de sprites da memória
    }
}
