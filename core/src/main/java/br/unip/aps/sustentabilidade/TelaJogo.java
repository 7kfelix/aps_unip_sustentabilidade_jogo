package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class TelaJogo implements Screen {

    private SustentabilidadeGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private List<EntidadeJogo> entidades;
    private GeradorDeOndas geradorDeOndas;
    private Torre.Tipo torreSelecionada = Torre.Tipo.SEMENTEIRA; // Começa com a básica

    private int ecoMoedas;
    private int vidaBase;
    private boolean isGameOver;
    private List<Vector2> rotaDoMapa;

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
        ecoMoedas = 150;
        vidaBase = 5;
        isGameOver = false;

        rotaDoMapa = new ArrayList<>();
        rotaDoMapa.add(new Vector2(200, 720));
        rotaDoMapa.add(new Vector2(200, 500));
        rotaDoMapa.add(new Vector2(1000, 500));
        rotaDoMapa.add(new Vector2(1000, 200));
        rotaDoMapa.add(new Vector2(200, 200));
        rotaDoMapa.add(new Vector2(200, 0));

        geradorDeOndas = new GeradorDeOndas(entidades, rotaDoMapa, this);
    }

    public void sofrerDanoNaBase(int dano) {
        if (!isGameOver) {
            vidaBase -= dano;
            if (vidaBase <= 0) isGameOver = true;
        }
    }

    public void adicionarMoedas(int valor) {
        this.ecoMoedas += valor;
    }

    private boolean podeConstruirAqui(float clickX, float clickY) {
        // 1. BLOQUEIO DA ÁREA DO MENU (Nada de construir em cima da interface!)
        if (clickX > 1030) {
            System.out.println("Bloqueado: Área da loja.");
            return false;
        }

        float centroX = clickX + 20f;
        float centroY = clickY + 20f;

        // 2. Colisão com a Estrada
        for (int i = 0; i < rotaDoMapa.size() - 1; i++) {
            Vector2 pontoAtual = rotaDoMapa.get(i);
            Vector2 proximoPonto = rotaDoMapa.get(i + 1);
            float distanciaParaEstrada = Intersector.distanceSegmentPoint(pontoAtual.x, pontoAtual.y, proximoPonto.x, proximoPonto.y, centroX, centroY);
            if (distanciaParaEstrada < 30f) return false;
        }

        // 3. Colisão com outras Torres
        for (EntidadeJogo entidade : entidades) {
            if (entidade instanceof Torre) {
                float distanciaParaOutraTorre = Vector2.dst(centroX, centroY, entidade.x + 20f, entidade.y + 20f);
                if (distanciaParaOutraTorre < 40f) return false;
            }
        }
        return true;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.3f, 0.15f, 1f);
        camera.update();

        // ==========================================
        // LÓGICA DE ENTRADA (Mecânica do Mouse e Teclado)
        // ==========================================
        if (!isGameOver) {
            // Atalhos pelo teclado continuam funcionando
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) torreSelecionada = Torre.Tipo.SEMENTEIRA;
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) torreSelecionada = Torre.Tipo.MACACO;
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) torreSelecionada = Torre.Tipo.PLANTA;
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) torreSelecionada = Torre.Tipo.BAMBU;
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) torreSelecionada = Torre.Tipo.FILTRO;
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) torreSelecionada = Torre.Tipo.ARVORE;

            // --- MODO DEV (CHEAT CODE: DONVIADO) ---
            // Verifica se alguma tecla qualquer foi pressionada neste exato frame
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {

                // Se a tecla pressionada for a tecla correta da sequência atual
                if (Gdx.input.isKeyJustPressed(CHEAT_SEQUENCE[cheatIndex])) {
                    cheatIndex++; // Avança um passo no código

                    // Se chegou no final da palavra inteira
                    if (cheatIndex == CHEAT_SEQUENCE.length) {
                        ecoMoedas += 1000000;
                        System.out.println("MODO DEV: Código DONVIADO ativado! +1.000.000 Moedas");
                        cheatIndex = 0; // Zera para poder usar de novo
                    }
                } else {
                    // Se errou a letra, a sequência é cancelada.
                    // Porém, checamos se a tecla "errada" não é a letra 'D' recomeçando a palavra.
                    if (Gdx.input.isKeyJustPressed(CHEAT_SEQUENCE[0])) {
                        cheatIndex = 1;
                    } else {
                        cheatIndex = 0;
                    }
                }
            }

            // --- COMPRA DO UPGRADE (TECLA 7) ---
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
                if (!upgradeVidaComprado && ecoMoedas >= CUSTO_UPGRADE_VIDA) {
                    ecoMoedas -= CUSTO_UPGRADE_VIDA;
                    upgradeVidaComprado = true;
                    System.out.println("Upgrade: Barras de Vida Ativadas!");
                }
            }

            if (Gdx.input.justTouched()) {
                Vector3 toque = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(toque);

                // SE CLICOU NA DIREITA (ÁREA DO MENU) -> Escolhe a Torre
                // SE CLICOU NA DIREITA (ÁREA DO MENU) -> Escolhe a Torre
                if (toque.x > 1050) {
                    if (toque.y > 600) torreSelecionada = Torre.Tipo.SEMENTEIRA;
                    else if (toque.y > 500) torreSelecionada = Torre.Tipo.MACACO;
                    else if (toque.y > 400) torreSelecionada = Torre.Tipo.PLANTA;
                    else if (toque.y > 300) torreSelecionada = Torre.Tipo.BAMBU;
                    else if (toque.y > 200) torreSelecionada = Torre.Tipo.FILTRO;
                    else if (toque.y > 100) torreSelecionada = Torre.Tipo.ARVORE;

                        // CORREÇÃO: Área de clique do Upgrade de Vida (abaixo de 100)
                    else if (toque.y <= 100) {
                        if (!upgradeVidaComprado && ecoMoedas >= CUSTO_UPGRADE_VIDA) {
                            ecoMoedas -= CUSTO_UPGRADE_VIDA;
                            upgradeVidaComprado = true;
                            System.out.println("Upgrade comprado pelo mouse!");
                        }
                    }
                }
                // SE CLICOU NO MAPA -> Tenta Construir
                else {
                    if (podeConstruirAqui(toque.x, toque.y)) {
                        int custo = Torre.getCusto(torreSelecionada);
                        if (ecoMoedas >= custo) {
                            ecoMoedas -= custo;
                            Torre novaTorre = new Torre(toque.x, toque.y, torreSelecionada, entidades, this);
                            entidades.add(novaTorre);
                        } else {
                            System.out.println("Faltam Eco-Moedas!");
                        }
                    }
                }
            }

            geradorDeOndas.atualizar(delta);
            for (int i = 0; i < entidades.size(); i++) entidades.get(i).atualizar(delta);
            for (int i = entidades.size() - 1; i >= 0; i--) if (!entidades.get(i).isAtivo()) entidades.remove(i);
        }

        // ==========================================
        // RENDERIZAÇÃO: FUNDO E FORMAS
        // ==========================================
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);

        // 1. Desenha a Estrada
        game.shape.setColor(Color.valueOf("#795c34"));
        for (int i = 0; i < rotaDoMapa.size() - 1; i++) {
            Vector2 p1 = rotaDoMapa.get(i);
            Vector2 p2 = rotaDoMapa.get(i + 1);
            game.shape.rectLine(p1.x, p1.y, p2.x, p2.y, 40f);
        }

        // 2. Desenha o Fundo do Menu Lateral
        game.shape.setColor(0.1f, 0.1f, 0.1f, 0.8f); // Cinza escuro meio transparente
        game.shape.rect(1050, 0, 230, 720);

        // 3. Desenha os "Ícones" (Quadrados) no Menu Lateral
        desenharQuadradoMenu(1060, 640, Color.BLUE, torreSelecionada == Torre.Tipo.SEMENTEIRA);
        desenharQuadradoMenu(1060, 540, Color.CYAN, torreSelecionada == Torre.Tipo.MACACO);
        desenharQuadradoMenu(1060, 440, Color.FOREST, torreSelecionada == Torre.Tipo.PLANTA);
        desenharQuadradoMenu(1060, 340, Color.WHITE, torreSelecionada == Torre.Tipo.BAMBU);
        desenharQuadradoMenu(1060, 240, Color.ROYAL, torreSelecionada == Torre.Tipo.FILTRO);
        desenharQuadradoMenu(1060, 140, Color.LIME, torreSelecionada == Torre.Tipo.ARVORE);

        game.shape.end();

        // ==========================================
        // RENDERIZAÇÃO: SPRITES E TEXTOS
        // ==========================================
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        for (int i = 0; i < entidades.size(); i++) entidades.get(i).renderizar(game.batch);

        // Textos do Topo (HUD)
        game.fonte.setColor(Color.WHITE);
        game.fonte.getData().setScale(1.5f); // Letra grande para o painel de Vida/Dinheiro
        game.fonte.draw(game.batch, "Eco-Moedas: " + ecoMoedas, 20, 700);
        game.fonte.draw(game.batch, "Vida: " + vidaBase, 20, 670);
        game.fonte.draw(game.batch, "Onda: " + geradorDeOndas.getOndaAtual(), 20, 640);

        // Textos da Loja Lateral (Temos que diminuir a fonte para caber as descrições)
        game.fonte.getData().setScale(1.0f);

        // 1. Sementeira
        game.fonte.draw(game.batch, "Sementeira", 1110, 675);
        game.fonte.draw(game.batch, "$50 | Dano:30", 1110, 655);

        // 2. Macaco
        game.fonte.draw(game.batch, "Macaco Splash", 1110, 575);
        game.fonte.draw(game.batch, "$120 | Dano em Area", 1110, 555);

        // 3. Planta
        game.fonte.draw(game.batch, "Carnivora (CC)", 1110, 475);
        game.fonte.draw(game.batch, "$100 | Enraiza 2s", 1110, 455);

        // 4. Bambu
        game.fonte.draw(game.batch, "Bambu Sniper", 1110, 375);
        game.fonte.draw(game.batch, "$150 | Dano:200", 1110, 355);

        // 5. Filtro
        game.fonte.draw(game.batch, "Filtro D'Agua", 1110, 275);
        game.fonte.draw(game.batch, "$80 | Dano Continuo", 1110, 255);

        // 6. Arvore
        game.fonte.draw(game.batch, "Arvore Ancia", 1110, 175);
        game.fonte.draw(game.batch, "$200 | Gera Renda", 1110, 155);

        // 7. Upgrade de Vida
        game.fonte.setColor(upgradeVidaComprado ? Color.GOLD : Color.WHITE);
        game.fonte.draw(game.batch, "7. Scanner de Vida", 1110, 75);
        game.fonte.draw(game.batch, upgradeVidaComprado ? "COMPRADO" : "$" + CUSTO_UPGRADE_VIDA, 1110, 55);

        if (isGameOver) {
            game.fonte.setColor(Color.RED);
            game.fonte.getData().setScale(3.0f);
            game.fonte.draw(game.batch, "GAME OVER", 1280 / 2f - 150, 720 / 2f);
        }

        game.batch.end();

        // Desenha as barras de vida apenas se o upgrade foi comprado
        if (upgradeVidaComprado) {

            // CORREÇÃO: Reafirma as coordenadas da câmera para a placa de vídeo
            game.shape.setProjectionMatrix(camera.combined);

            game.shape.begin(ShapeRenderer.ShapeType.Filled);
            for (EntidadeJogo entidade : entidades) {
                if (entidade instanceof Inimigo && entidade.isAtivo()) {
                    Inimigo inimigo = (Inimigo) entidade;

                    // 1. Desenha o fundo vermelho (vida perdida)
                    game.shape.setColor(Color.RED);
                    game.shape.rect(inimigo.x, inimigo.y + 35, 30, 5);

                    // 2. Calcula a largura da vida atual
                    float larguraVida = ( (float) inimigo.getVida() / inimigo.getVidaMaxima() ) * 30;

                    // 3. Desenha a barra verde por cima
                    game.shape.setColor(Color.GREEN);
                    game.shape.rect(inimigo.x, inimigo.y + 35, larguraVida, 5);
                }
            }
            game.shape.end();
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

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
