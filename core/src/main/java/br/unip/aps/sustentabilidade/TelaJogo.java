package br.unip.aps.sustentabilidade;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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

    private Texture texturaMapa;

    // --- TEXTURAS DOS ÍCONES DO MENU ---
    private Texture iconeSementeira;
    private Texture iconeMacaco;
    private Texture iconePlanta;
    private Texture iconeBambu;
    private Texture iconeFonte;
    private Texture iconeArvore;

    private List<EntidadeJogo> entidades;
    private GeradorDeOndas geradorDeOndas;
    private Torre.Tipo torreSelecionada = Torre.Tipo.SEMENTEIRA;
    private GerenciadorDeConstrucao gerenciadorConstrucao;

    private int ecoMoedas;
    private int vidaBase;
    private boolean isGameOver;
    private List<Vector2> rotaDoMapa;
    private boolean isPaused = false;

    private boolean velocidade2x = false;

    private boolean menuAberto = true;

    private boolean upScanner = false;
    private boolean upMuralha = false;
    private boolean upLentes = false;
    private boolean upEngrenagens = false;
    private boolean upAdubo = false;

    private final int CUSTO_SCANNER = 300;
    private final int CUSTO_MURALHA = 400;
    private final int CUSTO_LENTES = 500;
    private final int CUSTO_ENGRENAGENS = 600;
    private final int CUSTO_ADUBO = 800;

    private List<DanoFlutuante> danosNaTela;
    private List<AvisoFlutuante> avisosNaTela;

    private boolean upVisorDano = false;
    private final int CUSTO_VISOR = 350;

    private GerenciadorDeCodigos gerenciadorCodigos;

    public TelaJogo(SustentabilidadeGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(1280 / 2f, 720 / 2f, 0);

        texturaMapa = new Texture("mapa.jpg");

        // --- CARREGANDO OS ÍCONES ---
        try { iconeSementeira = new Texture("icone_lancasementes.png"); } catch (Exception e) {}
        try { iconeMacaco = new Texture("icone_macaco.png"); } catch (Exception e) {}
        try { iconePlanta = new Texture("icone_planta.png"); } catch (Exception e) {}
        try { iconeBambu = new Texture("icone_bambu.png"); } catch (Exception e) {}
        try { iconeFonte = new Texture("icone_fonte.png"); } catch (Exception e) {}
        try { iconeArvore = new Texture("icone_arvore.png"); } catch (Exception e) {}

        entidades = new ArrayList<>();
        danosNaTela = new ArrayList<>();
        avisosNaTela = new ArrayList<>();

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

        gerenciadorCodigos = new GerenciadorDeCodigos(this);

        GerenciadorAudio.tocarMusicaJogo();
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

        Vector3 toque = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(toque);
        gerenciadorConstrucao.atualizarMouse(toque.x, toque.y);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!isGameOver) {
                isPaused = !isPaused;
                if (isPaused) {
                    GerenciadorAudio.pausarMusicaJogo();
                } else {
                    GerenciadorAudio.despausarMusicaJogo();
                }
            }
        }

        if (!isGameOver && !isPaused) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) { torreSelecionada = Torre.Tipo.SEMENTEIRA; gerenciadorConstrucao.setTorreSelecionada(torreSelecionada); }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) { torreSelecionada = Torre.Tipo.MACACO; gerenciadorConstrucao.setTorreSelecionada(torreSelecionada); }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) { torreSelecionada = Torre.Tipo.PLANTA; gerenciadorConstrucao.setTorreSelecionada(torreSelecionada); }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) { torreSelecionada = Torre.Tipo.BAMBU; gerenciadorConstrucao.setTorreSelecionada(torreSelecionada); }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) { torreSelecionada = Torre.Tipo.FILTRO; gerenciadorConstrucao.setTorreSelecionada(torreSelecionada); }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) { torreSelecionada = Torre.Tipo.ARVORE; gerenciadorConstrucao.setTorreSelecionada(torreSelecionada); }

            if (Gdx.input.isKeyJustPressed(Input.Keys.V)) { velocidade2x = !velocidade2x; }

            gerenciadorCodigos.verificarCodigos();

            if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                for (int i = entidades.size() - 1; i >= 0; i--) {
                    EntidadeJogo ent = entidades.get(i);
                    if (ent instanceof Torre) {
                        Torre t = (Torre) ent;
                        int tamColisao = Torre.getTamanhoColisao(t.getTipo());

                        if (toque.x >= t.x && toque.x <= t.x + tamColisao &&
                            toque.y >= t.y && toque.y <= t.y + tamColisao) {

                            int valorVenda = Torre.getCusto(t.getTipo()) / 2;
                            ecoMoedas += valorVenda;
                            GerenciadorAudio.tocarSom(GerenciadorAudio.somInserir);
                            entidades.remove(i);

                            System.out.println("Torre vendida! Reembolso de $" + valorVenda);
                            break;
                        }
                    }
                }
            }

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

                if (toque.x >= 1150 && toque.y >= 680) {
                    GerenciadorAudio.tocarSom(GerenciadorAudio.somInserir);
                    menuAberto = !menuAberto;
                    gerenciadorConstrucao.setMenuAberto(menuAberto);
                }
                else if (menuAberto && toque.x > 1050) {
                    if (toque.y > 620 && toque.y <= 670) { torreSelecionada = Torre.Tipo.SEMENTEIRA; }
                    else if (toque.y > 560 && toque.y <= 610) { torreSelecionada = Torre.Tipo.MACACO; }
                    else if (toque.y > 500 && toque.y <= 550) { torreSelecionada = Torre.Tipo.PLANTA; }
                    else if (toque.y > 440 && toque.y <= 490) { torreSelecionada = Torre.Tipo.BAMBU; }
                    else if (toque.y > 380 && toque.y <= 430) { torreSelecionada = Torre.Tipo.FILTRO; }
                    else if (toque.y > 320 && toque.y <= 370) { torreSelecionada = Torre.Tipo.ARVORE; }

                    else if (toque.y > 250 && toque.y <= 290) {
                        if (!upScanner && ecoMoedas >= CUSTO_SCANNER) {
                            ecoMoedas -= CUSTO_SCANNER; upScanner = true;
                            GerenciadorAudio.tocarSom(GerenciadorAudio.somInserir);
                        }
                    }
                    else if (toque.y > 210 && toque.y <= 250) {
                        if (!upMuralha && ecoMoedas >= CUSTO_MURALHA) {
                            ecoMoedas -= CUSTO_MURALHA; vidaBase += 10; upMuralha = true;
                            GerenciadorAudio.tocarSom(GerenciadorAudio.somInserir);
                        }
                    }
                    else if (toque.y > 170 && toque.y <= 210) {
                        if (!upLentes && ecoMoedas >= CUSTO_LENTES) {
                            ecoMoedas -= CUSTO_LENTES; upLentes = true;
                            GerenciadorAudio.tocarSom(GerenciadorAudio.somInserir);
                            for (EntidadeJogo ent : entidades) if (ent instanceof Torre) ((Torre) ent).buffAlcance(1.25f);
                        }
                    }
                    else if (toque.y > 130 && toque.y <= 170) {
                        if (!upEngrenagens && ecoMoedas >= CUSTO_ENGRENAGENS) {
                            ecoMoedas -= CUSTO_ENGRENAGENS; upEngrenagens = true;
                            GerenciadorAudio.tocarSom(GerenciadorAudio.somInserir);
                            for (EntidadeJogo ent : entidades) if (ent instanceof Torre) ((Torre) ent).buffRecarga(0.80f);
                        }
                    }
                    else if (toque.y > 90 && toque.y <= 130) {
                        if (!upAdubo && ecoMoedas >= CUSTO_ADUBO) {
                            ecoMoedas -= CUSTO_ADUBO; upAdubo = true;
                            GerenciadorAudio.tocarSom(GerenciadorAudio.somInserir);
                            for (EntidadeJogo ent : entidades) if (ent instanceof Torre) ((Torre) ent).buffDano(1.50f);
                        }
                    }
                    else if (toque.y > 50 && toque.y <= 90) {
                        if (!upVisorDano && ecoMoedas >= CUSTO_VISOR) {
                            ecoMoedas -= CUSTO_VISOR; upVisorDano = true;
                            GerenciadorAudio.tocarSom(GerenciadorAudio.somInserir);
                        }
                    }
                    else if (toque.y >= 10 && toque.y <= 50) {
                        GerenciadorAudio.tocarSom(GerenciadorAudio.somInserir);
                        velocidade2x = !velocidade2x;
                    }
                    gerenciadorConstrucao.setTorreSelecionada(torreSelecionada);
                }
                else {
                    int tamanhoConstrucao = Torre.getTamanhoColisao(torreSelecionada);
                    float posRealX = toque.x - (tamanhoConstrucao / 2f);
                    float posRealY = toque.y;

                    if (gerenciadorConstrucao.podeConstruirAqui(posRealX, posRealY)) {
                        int custo = Torre.getCusto(torreSelecionada);

                        if (ecoMoedas >= custo) {
                            ecoMoedas -= custo;
                            GerenciadorAudio.tocarSom(GerenciadorAudio.somInserir);

                            Torre novaTorre = new Torre(posRealX, posRealY, torreSelecionada, entidades, rotaDoMapa, this);

                            if (upLentes) novaTorre.buffAlcance(1.25f);
                            if (upEngrenagens) novaTorre.buffRecarga(0.80f);
                            if (upAdubo) novaTorre.buffDano(1.50f);

                            entidades.add(novaTorre);
                            geradorDeOndas.iniciarOndas();

                        } else {
                            System.out.println("Faltam Eco-Moedas!");
                            avisosNaTela.add(new AvisoFlutuante(posRealX, posRealY + 50, "SEM DINHEIRO!", Color.RED));
                        }
                    }
                }
            }

            float tempoDoJogo = velocidade2x ? delta * 2.0f : delta;

            geradorDeOndas.atualizar(tempoDoJogo);
            for (int i = 0; i < entidades.size(); i++) {
                entidades.get(i).atualizar(tempoDoJogo);
            }
            for (int i = entidades.size() - 1; i >= 0; i--) {
                if (!entidades.get(i).isAtivo()) {
                    entidades.remove(i);
                }
            }

            for (int i = danosNaTela.size() - 1; i >= 0; i--) {
                danosNaTela.get(i).atualizar(tempoDoJogo);
                if (danosNaTela.get(i).deveSair()) {
                    danosNaTela.remove(i);
                }
            }

            for (int i = avisosNaTela.size() - 1; i >= 0; i--) {
                avisosNaTela.get(i).atualizar(tempoDoJogo);
                if (avisosNaTela.get(i).deveSair()) {
                    avisosNaTela.remove(i);
                }
            }

        } else if (isPaused && !isGameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || (Gdx.input.justTouched() && toque.y > 350 && toque.y < 450)) {
                isPaused = false;
                GerenciadorAudio.despausarMusicaJogo();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || (Gdx.input.justTouched() && toque.y > 250 && toque.y < 350)) {
                game.setScreen(new TelaConfiguracoes(game, this));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || (Gdx.input.justTouched() && toque.y > 150 && toque.y < 250)) {
                game.setScreen(new TelaMenu(game));
                dispose();
            }
        }
        else if (isGameOver) {
            if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                GerenciadorAudio.pararMusicaJogo();
                game.setScreen(new TelaJogo(game));
                dispose();
            }
        }

        // ==========================================
        // RENDERIZAÇÃO: CENÁRIO E INTERFACE
        // ==========================================
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(texturaMapa, 0, 0, 1280, 720);
        game.batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);

        // --- DESENHA A BORDINHA AMARELA DA SELEÇÃO E O FUNDO ESCURO ---
        if (menuAberto) {
            game.shape.setColor(0.1f, 0.1f, 0.1f, 0.85f);
            game.shape.rect(1050, 0, 230, 720);

            desenharFundoIcone(1060, 640, torreSelecionada == Torre.Tipo.SEMENTEIRA);
            desenharFundoIcone(1060, 580, torreSelecionada == Torre.Tipo.MACACO);
            desenharFundoIcone(1060, 520, torreSelecionada == Torre.Tipo.PLANTA);
            desenharFundoIcone(1060, 460, torreSelecionada == Torre.Tipo.BAMBU);
            desenharFundoIcone(1060, 400, torreSelecionada == Torre.Tipo.FILTRO);
            desenharFundoIcone(1060, 340, torreSelecionada == Torre.Tipo.ARVORE);

            game.shape.setColor(velocidade2x ? Color.YELLOW : Color.DARK_GRAY);
            game.shape.rect(1060, 25, 210, 40);
        }

        game.shape.setColor(0.2f, 0.2f, 0.2f, 0.9f);
        game.shape.rect(1150, 680, 130, 40);

        game.shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.batch.begin();

        // --- DESENHA OS ÍCONES POR CIMA DOS FUNDOS ---
        if (menuAberto) {
            if (iconeSementeira != null) game.batch.draw(iconeSementeira, 1060, 640, 40, 40);
            if (iconeMacaco != null) game.batch.draw(iconeMacaco, 1060, 580, 40, 40);
            if (iconePlanta != null) game.batch.draw(iconePlanta, 1060, 520, 40, 40);
            if (iconeBambu != null) game.batch.draw(iconeBambu, 1060, 460, 40, 40);
            if (iconeFonte != null) game.batch.draw(iconeFonte, 1060, 400, 40, 40);
            if (iconeArvore != null) game.batch.draw(iconeArvore, 1060, 340, 40, 40);
        }

        entidades.sort((e1, e2) -> Float.compare(e2.y, e1.y));

        for (EntidadeJogo ent : entidades) {
            ent.renderizar(game.batch);
        }

        for (DanoFlutuante df : danosNaTela) {
            df.renderizar(game.fonte, game.batch);
        }

        for (AvisoFlutuante aviso : avisosNaTela) {
            aviso.renderizar(game.fonte, game.batch);
        }

        game.fonte.setColor(Color.WHITE);
        game.fonte.getData().setScale(1.5f);
        game.fonte.draw(game.batch, "Eco-Moedas: " + ecoMoedas, 20, 700);
        game.fonte.draw(game.batch, "Vida: " + vidaBase, 20, 670);
        game.fonte.draw(game.batch, "Onda: " + geradorDeOndas.getOndaAtual(), 20, 640);

        if (!geradorDeOndas.isOndasIniciadas()) {
            game.fonte.setColor(Color.YELLOW);
            game.fonte.draw(game.batch, "Construa sua primeira defesa para iniciar as ondas!", 300, 680);
        }

        if (menuAberto) {
            game.fonte.getData().setScale(1.0f);
            game.fonte.draw(game.batch, "Sementeira\n$50 | Dano:50", 1110, 675);
            game.fonte.draw(game.batch, "Macaco Baloeiro\n$120 | Balao", 1110, 615);
            game.fonte.draw(game.batch, "Mano CC\n$100 | Prende", 1110, 555);
            game.fonte.draw(game.batch, "Bambu Sniper\n$650 | Dano:450", 1110, 495);
            game.fonte.draw(game.batch, "Fonte Pura\n$80 | Dano Direto", 1110, 435);
            game.fonte.draw(game.batch, "Mr Tree\n$200 | Renda", 1110, 375);

            game.fonte.setColor(Color.LIGHT_GRAY);
            game.fonte.draw(game.batch, "--- UPGRADES ---", 1110, 310);

            game.fonte.setColor(upScanner ? Color.GOLD : Color.WHITE);
            game.fonte.draw(game.batch, "1. Scanner Vida: " + (upScanner ? "OK" : "$300"), 1060, 280);

            game.fonte.setColor(upMuralha ? Color.GOLD : Color.WHITE);
            game.fonte.draw(game.batch, "2. Muralha: " + (upMuralha ? "OK" : "$400"), 1060, 240);

            game.fonte.setColor(upLentes ? Color.GOLD : Color.WHITE);
            game.fonte.draw(game.batch, "3. Lentes (+Alc): " + (upLentes ? "OK" : "$500"), 1060, 200);

            game.fonte.setColor(upEngrenagens ? Color.GOLD : Color.WHITE);
            game.fonte.draw(game.batch, "4. Engrenagens: " + (upEngrenagens ? "OK" : "$600"), 1060, 160);

            game.fonte.setColor(upAdubo ? Color.GOLD : Color.WHITE);
            game.fonte.draw(game.batch, "5. Adubo (+Dano): " + (upAdubo ? "OK" : "$800"), 1060, 120);

            game.fonte.setColor(upVisorDano ? Color.GOLD : Color.WHITE);
            game.fonte.draw(game.batch, "6. Visor Dano: " + (upVisorDano ? "OK" : "$350"), 1060, 80);

            game.fonte.setColor(velocidade2x ? Color.BLACK : Color.WHITE);
            game.fonte.draw(game.batch, velocidade2x ? "VELOCIDADE: 2X" : "VELOCIDADE: 1X", 1095, 52);
        }

        game.fonte.setColor(Color.YELLOW);
        game.fonte.getData().setScale(1.2f);
        game.fonte.draw(game.batch, menuAberto ? ">> Fechar" : "<< Loja", 1170, 708);

        if (isGameOver) {
            game.fonte.setColor(Color.RED);
            game.fonte.getData().setScale(3.0f);
            game.fonte.draw(game.batch, "GAME OVER", 1280 / 2f - 150, 720 / 2f + 50);

            game.fonte.setColor(Color.WHITE);
            game.fonte.getData().setScale(1.5f);
            game.fonte.draw(game.batch, "Clique na tela ou aperte ENTER para recomecar", 1280 / 2f - 260, 720 / 2f - 20);
        }
        game.batch.end();

        if (upScanner) {
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

        if (!isPaused && !isGameOver) {
            gerenciadorConstrucao.renderizarPreview();
        }

        if (isPaused && !isGameOver) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            game.shape.setProjectionMatrix(camera.combined);
            game.shape.begin(ShapeRenderer.ShapeType.Filled);
            game.shape.setColor(0, 0, 0, 0.8f);
            game.shape.rect(0, 0, 1280, 720);
            game.shape.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            game.batch.begin();
            game.fonte.setColor(Color.YELLOW);
            game.fonte.getData().setScale(3.0f);
            game.fonte.draw(game.batch, "PAUSADO", 1280 / 2f - 110, 550);

            game.fonte.setColor(Color.WHITE);
            game.fonte.getData().setScale(1.5f);
            game.fonte.draw(game.batch, "1. Continuar", 1280 / 2f - 80, 400);
            game.fonte.draw(game.batch, "2. Configuracoes", 1280 / 2f - 110, 300);
            game.fonte.draw(game.batch, "3. Voltar ao Menu", 1280 / 2f - 115, 200);

            game.batch.end();
        }
    }

    public void pularParaOnda(int onda) {
        entidades.removeIf(entidade -> entidade instanceof Inimigo || entidade instanceof Projetil);
        geradorDeOndas.pularParaOnda(onda);
    }

    // --- NOVO MÉTODO PARA DESENHAR O FUNDO DO ÍCONE ---
    private void desenharFundoIcone(float x, float y, boolean selecionada) {
        if (selecionada) {
            game.shape.setColor(Color.YELLOW);
            game.shape.rect(x - 5, y - 5, 50, 50); // Bordinha amarela
        }
        // Fundo escuro atrás do ícone para dar destaque (se a imagem não cobrir tudo)
        game.shape.setColor(Color.DARK_GRAY);
        game.shape.rect(x, y, 40, 40);
    }

    public void adicionarDanoFlutuante(float x, float y, int dano, Color cor) {
        if (upVisorDano) {
            danosNaTela.add(new DanoFlutuante(x, y + 40, dano, cor));
        }
    }

    public void adicionarEntidadeAoJogo(EntidadeJogo novaEntidade) {
        entidades.add(novaEntidade);
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void show() {}

    @Override
    public void pause() {
        GerenciadorAudio.pausarMusicaJogo();
    }

    @Override
    public void resume() {
        if (!isGameOver && !isPaused) {
            GerenciadorAudio.despausarMusicaJogo();
        }
    }

    @Override public void hide() {}

    @Override public void dispose() {
        if (texturaMapa != null) texturaMapa.dispose();

        // --- LIMPANDO OS ÍCONES DA MEMÓRIA ---
        if (iconeSementeira != null) iconeSementeira.dispose();
        if (iconeMacaco != null) iconeMacaco.dispose();
        if (iconePlanta != null) iconePlanta.dispose();
        if (iconeBambu != null) iconeBambu.dispose();
        if (iconeFonte != null) iconeFonte.dispose();
        if (iconeArvore != null) iconeArvore.dispose();
    }
}
