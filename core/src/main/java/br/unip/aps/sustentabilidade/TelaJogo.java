package br.unip.aps.sustentabilidade;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TelaJogo implements Screen {

    private SustentabilidadeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private List<EntidadeJogo> entidades;
    private GeradorDeOndas geradorDeOndas;
    private Torre.Tipo torreSelecionada = Torre.Tipo.SEMENTEIRA; // Começa com a básica
    private GerenciadorDeConstrucao gerenciadorConstrucao;
    
    private int ecoMoedas;
    private int vidaBase;
    private boolean isGameOver;
    private List<Vector2> rotaDoMapa;
    private boolean isPaused = false;

    private boolean upgradeVidaComprado = false;
    private final int CUSTO_UPGRADE_VIDA = 300;

    // --- VARIÁVEIS DO CHEAT CODE ---
    private final int[] CHEAT_SEQUENCE = {
        Input.Keys.D, Input.Keys.O, Input.Keys.N, Input.Keys.V,
        Input.Keys.I, Input.Keys.A, Input.Keys.D, Input.Keys.O
    };
    private int cheatIndex = 0;

    public TelaJogo(SustentabilidadeGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(1280 / 2f, 720 / 2f, 0);

        entidades = new ArrayList<>();
        ecoMoedas = 100;
        vidaBase = 10;
        isGameOver = false;
        

        rotaDoMapa = new ArrayList<>();
        rotaDoMapa.add(new Vector2(200, 720));
        rotaDoMapa.add(new Vector2(200, 500));
        rotaDoMapa.add(new Vector2(1000, 500));
        rotaDoMapa.add(new Vector2(1000, 200));
        rotaDoMapa.add(new Vector2(200, 200));
        rotaDoMapa.add(new Vector2(200, 0));

        gerenciadorConstrucao = new GerenciadorDeConstrucao(game, this, entidades, rotaDoMapa);
        geradorDeOndas = new GeradorDeOndas(entidades, rotaDoMapa, this);
    }

    public void sofrerDanoNaBase(int dano) {
        if (!isGameOver) {
            vidaBase -= dano;
            if (vidaBase <= 0) {
                isGameOver = true;
            }
        }
    }

    public void adicionarMoedas(int valor) {
        this.ecoMoedas += valor;
    }

   @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.3f, 0.15f, 1f);
        camera.update();

        // Captura a posição do mouse e atualiza o gerenciador logo no início
        Vector3 toque = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(toque);
        gerenciadorConstrucao.atualizarMouse(toque.x, toque.y);

        // ==========================================
        // CONTROLE DO PAUSE (TECLA ESC)
        // ==========================================
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!isGameOver) {
                isPaused = !isPaused; // Inverte o estado (Pausa/Despausa)
            }
        }

        // ==========================================
        // LÓGICA DE ENTRADA E ATUALIZAÇÃO
        // ==========================================
        // O jogo só "roda" se NÃO estiver em Game Over e NÃO estiver pausado
        if (!isGameOver && !isPaused) {

            // --- SELEÇÃO VIA TECLADO ---
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                torreSelecionada = Torre.Tipo.SEMENTEIRA;
                gerenciadorConstrucao.setTorreSelecionada(torreSelecionada);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                torreSelecionada = Torre.Tipo.MACACO;
                gerenciadorConstrucao.setTorreSelecionada(torreSelecionada);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                torreSelecionada = Torre.Tipo.PLANTA;
                gerenciadorConstrucao.setTorreSelecionada(torreSelecionada);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
                torreSelecionada = Torre.Tipo.BAMBU;
                gerenciadorConstrucao.setTorreSelecionada(torreSelecionada);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
                torreSelecionada = Torre.Tipo.FILTRO;
                gerenciadorConstrucao.setTorreSelecionada(torreSelecionada);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
                torreSelecionada = Torre.Tipo.ARVORE;
                gerenciadorConstrucao.setTorreSelecionada(torreSelecionada);
            }

            // --- MODO DEV (CHEAT CODE: DONVIADO) ---
            // Ignoramos a tecla ESC na sequência do cheat para não bugar o pause
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) && !Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                if (Gdx.input.isKeyJustPressed(CHEAT_SEQUENCE[cheatIndex])) {
                    cheatIndex++;
                    if (cheatIndex == CHEAT_SEQUENCE.length) {
                        ecoMoedas += 1000000;
                        System.out.println("MODO DEV: Código DONVIADO ativado!");
                        cheatIndex = 0;
                    }
                } else {
                    cheatIndex = Gdx.input.isKeyJustPressed(CHEAT_SEQUENCE[0]) ? 1 : 0;
                }
            }

            // --- COMPRA DO UPGRADE (TECLA 7) ---
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
                if (!upgradeVidaComprado && ecoMoedas >= CUSTO_UPGRADE_VIDA) {
                    ecoMoedas -= CUSTO_UPGRADE_VIDA;
                    upgradeVidaComprado = true;
                }
            }

            // --- CLIQUES DO MOUSE ---
            if (Gdx.input.justTouched()) {
                if (toque.x > 1050) {
                    if (toque.y > 600) {
                        torreSelecionada = Torre.Tipo.SEMENTEIRA; 
                    } else if (toque.y > 500) {
                        torreSelecionada = Torre.Tipo.MACACO; 
                    } else if (toque.y > 400) {
                        torreSelecionada = Torre.Tipo.PLANTA; 
                    } else if (toque.y > 300) {
                        torreSelecionada = Torre.Tipo.BAMBU; 
                    } else if (toque.y > 200) {
                        torreSelecionada = Torre.Tipo.FILTRO; 
                    } else if (toque.y > 100) {
                        torreSelecionada = Torre.Tipo.ARVORE; 
                    } else if (toque.y <= 100) {
                        if (!upgradeVidaComprado && ecoMoedas >= CUSTO_UPGRADE_VIDA) {
                            ecoMoedas -= CUSTO_UPGRADE_VIDA;
                            upgradeVidaComprado = true;
                        }
                    }
                    gerenciadorConstrucao.setTorreSelecionada(torreSelecionada);
                } 
                else {
                    float posRealX = toque.x - 20f;
                    float posRealY = toque.y - 20f;

                    if (gerenciadorConstrucao.podeConstruirAqui(posRealX, posRealY)) {
                        int custo = Torre.getCusto(torreSelecionada);
                        if (ecoMoedas >= custo) {
                            ecoMoedas -= custo;
                            entidades.add(new Torre(posRealX, posRealY, torreSelecionada, entidades, this));
                        } else {
                            System.out.println("Faltam Eco-Moedas!");
                        }
                    }
                }
            }

            geradorDeOndas.atualizar(delta);
            for (int i = 0; i < entidades.size(); i++) {
                entidades.get(i).atualizar(delta);
            }
            for (int i = entidades.size() - 1; i >= 0; i--) {
                if (!entidades.get(i).isAtivo()) {
                    entidades.remove(i);
                }
            }
        } else if (isGameOver) {
            // LÓGICA DE GAME OVER (RESTART)
            if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                game.setScreen(new TelaJogo(game)); 
                dispose(); 
            }
        }

        // ==========================================
        // RENDERIZAÇÃO: CENÁRIO E INTERFACE
        // (Isso sempre desenha para o fundo não sumir no pause)
        // ==========================================
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);

        // Estrada
        game.shape.setColor(Color.valueOf("#795c34"));
        for (int i = 0; i < rotaDoMapa.size() - 1; i++) {
            Vector2 p1 = rotaDoMapa.get(i);
            Vector2 p2 = rotaDoMapa.get(i + 1);
            game.shape.rectLine(p1.x, p1.y, p2.x, p2.y, 40f);
        }

        // Menu Lateral
        game.shape.setColor(0.1f, 0.1f, 0.1f, 0.8f);
        game.shape.rect(1050, 0, 230, 720);

        // Ícones da Loja
        desenharQuadradoMenu(1060, 640, Color.BLUE, torreSelecionada == Torre.Tipo.SEMENTEIRA);
        desenharQuadradoMenu(1060, 540, Color.CYAN, torreSelecionada == Torre.Tipo.MACACO);
        desenharQuadradoMenu(1060, 440, Color.FOREST, torreSelecionada == Torre.Tipo.PLANTA);
        desenharQuadradoMenu(1060, 340, Color.WHITE, torreSelecionada == Torre.Tipo.BAMBU);
        desenharQuadradoMenu(1060, 240, Color.ROYAL, torreSelecionada == Torre.Tipo.FILTRO);
        desenharQuadradoMenu(1060, 140, Color.LIME, torreSelecionada == Torre.Tipo.ARVORE);
        game.shape.end();

        // Sprites, Entidades e HUD
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (EntidadeJogo ent : entidades) {
            ent.renderizar(game.batch);
        }

        game.fonte.setColor(Color.WHITE);
        game.fonte.getData().setScale(1.5f);
        game.fonte.draw(game.batch, "Eco-Moedas: " + ecoMoedas, 20, 700);
        game.fonte.draw(game.batch, "Vida: " + vidaBase, 20, 670);
        game.fonte.draw(game.batch, "Onda: " + geradorDeOndas.getOndaAtual(), 20, 640);

        game.fonte.getData().setScale(1.0f);
        game.fonte.draw(game.batch, "Sementeira\n$50 | Dano:50", 1110, 675);
        game.fonte.draw(game.batch, "Macaco Splash\n$120 | Area", 1110, 575);
        game.fonte.draw(game.batch, "Carnivora (CC)\n$100 | Root", 1110, 475);
        game.fonte.draw(game.batch, "Bambu Sniper\n$650 | Dano:450", 1110, 375);
        game.fonte.draw(game.batch, "Filtro D'Agua\n$80 | Aura", 1110, 275);
        game.fonte.draw(game.batch, "Arvore Ancia\n$200 | Renda", 1110, 175);

        game.fonte.setColor(upgradeVidaComprado ? Color.GOLD : Color.WHITE);
        game.fonte.draw(game.batch, "7. Scanner de Vida", 1110, 75);
        game.fonte.draw(game.batch, upgradeVidaComprado ? "COMPRADO" : "$300", 1110, 55);

        if (isGameOver) {
            game.fonte.setColor(Color.RED);
            game.fonte.getData().setScale(3.0f);
            game.fonte.draw(game.batch, "GAME OVER", 1280 / 2f - 150, 720 / 2f + 50);

            game.fonte.setColor(Color.WHITE);
            game.fonte.getData().setScale(1.5f);
            game.fonte.draw(game.batch, "Clique na tela ou aperte ENTER para recomecar", 1280 / 2f - 260, 720 / 2f - 20);
        }
        game.batch.end();

        // Barras de Vida (se comprado)
        if (upgradeVidaComprado) {
            game.shape.setProjectionMatrix(camera.combined);
            game.shape.begin(ShapeRenderer.ShapeType.Filled);
            for (EntidadeJogo ent : entidades) {
                if (ent instanceof Inimigo && ent.isAtivo()) {
                    Inimigo ini = (Inimigo) ent;
                    game.shape.setColor(Color.RED);
                    game.shape.rect(ini.x, ini.y + 35, 30, 5);
                    game.shape.setColor(Color.GREEN);
                    game.shape.rect(ini.x, ini.y + 35, ((float) ini.getVida() / ini.getVidaMaxima()) * 30, 5);
                }
            }
            game.shape.end();
        }

        // Preview de construção: só aparece se NÃO estiver pausado
        if (!isPaused && !isGameOver) {
            gerenciadorConstrucao.renderizarPreview();
        }

        // ==========================================
        // SOBREPOSIÇÃO DA TELA DE PAUSE
        // ==========================================
        if (isPaused && !isGameOver) {
            // Habilita transparência
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            game.shape.setProjectionMatrix(camera.combined);
            game.shape.begin(ShapeRenderer.ShapeType.Filled);
            game.shape.setColor(0, 0, 0, 0.7f); // Preto com 70% de transparência
            game.shape.rect(0, 0, 1280, 720);
            game.shape.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            game.batch.begin();
            game.fonte.setColor(Color.YELLOW);
            game.fonte.getData().setScale(3.0f);
            game.fonte.draw(game.batch, "PAUSADO", 1280 / 2f - 110, 720 / 2f + 50);

            game.fonte.setColor(Color.WHITE);
            game.fonte.getData().setScale(1.5f);
            game.fonte.draw(game.batch, "Aperte ESC para continuar", 1280 / 2f - 160, 720 / 2f - 20);
            game.batch.end();
        }
    }

    // Metodo Auxiliar para desenhar a borda amarela na torre selecionada
    private void desenharQuadradoMenu(float x, float y, Color cor, boolean selecionada) {
        if (selecionada) {
            game.shape.setColor(Color.YELLOW); // Borda amarela de seleção
            game.shape.rect(x - 5, y - 5, 50, 50); // Desenha maior por baixo
        }
        game.shape.setColor(cor);
        game.shape.rect(x, y, 40, 40); // O quadrado principal
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
