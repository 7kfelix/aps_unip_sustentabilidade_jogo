package br.unip.aps.sustentabilidade;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Torre extends EntidadeJogo {

    public enum Tipo { SEMENTEIRA, MACACO, PLANTA, BAMBU, FILTRO, ARVORE }

    private Tipo tipo;
    private float alcance;
    private int dano;
    private float taxaDeTiro;
    private float tempoDesdeUltimoTiro;
    private List<EntidadeJogo> todasEntidades;
    private TelaJogo jogoPrincipal;
    private int custo;

    private boolean atacando = false;
    private float tempoAnimacaoAtual = 0f;
    private float tempoAnimacaoContinua = 0f; // --- NOVO: Cronômetro que nunca para!
    private final float DURACAO_ANIMACAO = 0.4f;
    private Inimigo alvoTravado;

    private TextureRegion[] framesAnimacao;
    private Projetil.Efeito efeitoTiro;

    // --- LÓGICA DE ORIENTAÇÃO ---
    private boolean olharParaDireitaPadrao;

    public Torre(float x, float y, Tipo tipo, List<EntidadeJogo> todasEntidades, List<Vector2> rotaDoMapa, TelaJogo jogoPrincipal) {
        super(x, y, null);
        this.tipo = tipo;
        this.todasEntidades = todasEntidades;
        this.jogoPrincipal = jogoPrincipal;
        this.tempoDesdeUltimoTiro = 0f;

        // 1. Define os atributos base
        configurarAtributos(tipo);

        // 2. Define para onde olhar (Estrada mais próxima)
        this.olharParaDireitaPadrao = calcularDirecaoInicial(x, y, rotaDoMapa);

        this.custo = getCusto(tipo);

        // 3. Carregamento de Sprites
        carregarSprites(tipo);
    }

    private boolean calcularDirecaoInicial(float x, float y, List<Vector2> rota) {
        if (rota == null || rota.isEmpty()) return false;

        float menorDistancia = Float.MAX_VALUE;
        Vector2 pontoMaisProximo = rota.get(0);

        // Encontra o ponto da estrada que está mais perto da torre
        for (Vector2 ponto : rota) {
            float dist = Vector2.dst(x, y, ponto.x, ponto.y);
            if (dist < menorDistancia) {
                menorDistancia = dist;
                pontoMaisProximo = ponto;
            }
        }
        // Se a estrada estiver à direita da torre, ela deve inverter o sprite (olhar para direita)
        return pontoMaisProximo.x > x;
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        float tamanhoVisual = 100f;
        if (this.tipo == Tipo.ARVORE || this.tipo == Tipo.FILTRO || this.tipo == Tipo.BAMBU) {
            tamanhoVisual = 140f;
        }

        int tamColisao = getTamanhoColisao(this.tipo);

        // --- AQUI ESTÁ A CORREÇÃO MÁGICA ---
        // A compensação agora é SÓ no eixo X (para a torre não ficar torta para os lados)
        float compensacaoX = (tamanhoVisual - tamColisao) / 2f;
        float desenhoX = this.x - compensacaoX;

        // O Y não tem mais compensação para baixo! A base do sprite fica no this.y exato.
        float desenhoY = this.y;

        // --- LÓGICA DE DIREÇÃO INTELIGENTE ---
        boolean deveInverter;

        if (alvoTravado != null && alvoTravado.isAtivo()) {
            deveInverter = alvoTravado.x > this.x;
        } else {
            deveInverter = olharParaDireitaPadrao;
        }

        if (framesAnimacao != null) {
            TextureRegion frameParaDesenhar;

            if (this.tipo == Tipo.ARVORE || this.tipo == Tipo.FILTRO) {
                int frameAtual = (int) ((tempoAnimacaoContinua / 0.15f) % framesAnimacao.length);
                frameParaDesenhar = framesAnimacao[frameAtual];
            }
            else if (atacando && framesAnimacao.length > 1) {
                int frameAtual = (int) ((tempoAnimacaoAtual / DURACAO_ANIMACAO) * framesAnimacao.length);
                if (frameAtual >= framesAnimacao.length) frameAtual = framesAnimacao.length - 1;
                frameParaDesenhar = framesAnimacao[frameAtual];
            } else {
                frameParaDesenhar = framesAnimacao[0];
            }

            batch.draw(frameParaDesenhar, desenhoX, desenhoY, tamanhoVisual / 2f, tamanhoVisual / 2f,
                tamanhoVisual, tamanhoVisual, deveInverter ? -1f : 1f, 1f, 0f);

        } else if (this.textura != null) {
            batch.draw(textura, desenhoX, desenhoY, tamanhoVisual, tamanhoVisual);
        }
    }

    private void configurarAtributos(Tipo tipo) {
        switch (tipo) {
            case MACACO: this.alcance = 150.0f; this.dano = 80; this.taxaDeTiro = 1.2f; this.efeitoTiro = Projetil.Efeito.AREA; break;
            case PLANTA: this.alcance = 120.0f; this.dano = 10; this.taxaDeTiro = 0.5f; this.efeitoTiro = Projetil.Efeito.PARALISAR; break;
            case BAMBU: this.alcance = 1500.0f; this.dano = 450; this.taxaDeTiro = 0.2f; this.efeitoTiro = Projetil.Efeito.NORMAL; break;
            case FILTRO: this.alcance = 100.0f; this.dano = 35; this.taxaDeTiro = 0.2f; this.efeitoTiro = Projetil.Efeito.NORMAL; break;
            case ARVORE: this.alcance = 0f; this.dano = 0; this.taxaDeTiro = 10.0f; this.efeitoTiro = Projetil.Efeito.NORMAL; break;
            case SEMENTEIRA: default: this.alcance = 150.0f; this.dano = 50; this.taxaDeTiro = 0.8f; this.efeitoTiro = Projetil.Efeito.NORMAL; break;
        }
    }

    private void carregarSprites(Tipo tipo) {
        String nomeArquivo = "sementeira.png";
        switch (tipo) {
            case ARVORE: nomeArquivo = "arvore.png"; break;
            case BAMBU: nomeArquivo = "bambu.png"; break;
            case FILTRO: nomeArquivo = "fonte.png"; break;
            case MACACO: nomeArquivo = "macaco.png"; break;
            case PLANTA: nomeArquivo = "planta.png"; break;
        }

        try {
            Texture spriteSheet = new Texture(nomeArquivo);
            int tamFrame = spriteSheet.getHeight();
            int qtd = spriteSheet.getWidth() / tamFrame;
            framesAnimacao = new TextureRegion[qtd];
            for (int i = 0; i < qtd; i++) framesAnimacao[i] = new TextureRegion(spriteSheet, i * tamFrame, 0, tamFrame, tamFrame);
        } catch (Exception e) {
            this.textura = criarTexturaQuadrada(40, Color.GRAY);
        }
    }

    public Tipo getTipo() { return this.tipo; }
    public static int getTamanhoColisao(Tipo tipo) {
        return (tipo == Tipo.ARVORE || tipo == Tipo.FILTRO || tipo == Tipo.BAMBU) ? 55 : 40;
    }

    public static int getCusto(Tipo tipo) {
        switch (tipo) {
            case ARVORE: return 200;
            case BAMBU: return 650;
            case MACACO: return 120;
            case PLANTA: return 100;
            case FILTRO: return 80;
            case SEMENTEIRA: default: return 50;
        }
    }

    public static float getAlcanceParaPreview(Tipo tipo) {
        if (tipo == Tipo.ARVORE) return 0f;
        if (tipo == Tipo.BAMBU) return 1500f;
        if (tipo == Tipo.MACACO) return 150f;
        if (tipo == Tipo.PLANTA) return 120f;
        if (tipo == Tipo.FILTRO) return 100f;
        return 150f;
    }

    public void buffAlcance(float m) { this.alcance *= m; }
    public void buffRecarga(float m) { this.taxaDeTiro *= m; }
    public void buffDano(float m) { this.dano = (int)(this.dano * m); }

    private Texture criarTexturaQuadrada(int tamanho, Color cor) {
        Pixmap pixmap = new Pixmap(tamanho, tamanho, Pixmap.Format.RGBA8888);
        pixmap.setColor(cor);
        pixmap.fillRectangle(0, 0, tamanho, tamanho);
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    @Override
    public void atualizar(float deltaTime) {

        // --- NOVO: Cronômetro principal (Roda o tempo todo) ---
        tempoAnimacaoContinua += deltaTime;

        if (this.tipo == Tipo.ARVORE) {
            tempoDesdeUltimoTiro += deltaTime;
            if (tempoDesdeUltimoTiro >= taxaDeTiro) {
                GerenciadorAudio.tocarSom(GerenciadorAudio.somRenda); // <-- SOM DA RENDA AQUI
                jogoPrincipal.adicionarMoedas(20);
                tempoDesdeUltimoTiro = 0f;
            }
            return;
        }

        if (atacando) {
            tempoAnimacaoAtual += deltaTime;
            if (tempoAnimacaoAtual >= DURACAO_ANIMACAO) {

                // 1. Lógica do FILTRO (Dano Direto + Número Flutuante)
                if (this.tipo == Tipo.FILTRO && alvoTravado != null && alvoTravado.isAtivo()) {
                    GerenciadorAudio.tocarSom(GerenciadorAudio.somAguaFonte); // <-- SOM DA ÁGUA DO FILTRO

                    alvoTravado.setMolhado(2.0f); // Fica azul transparente por 2 segundos
                    alvoTravado.setCorTemporaria(Color.RED, 0.1f); // Pisca vermelho rápido

                    boolean tiroFatal = alvoTravado.receberDano(this.dano);

                    // O Filtro ataca direto com a água, então colocamos azul (Color.CYAN)
                    jogoPrincipal.adicionarDanoFlutuante(alvoTravado.x, alvoTravado.y, this.dano, Color.CYAN);

                    if (tiroFatal) {
                        GerenciadorAudio.tocarSom(GerenciadorAudio.somMorte); // <-- INIMIGO MORRE
                        jogoPrincipal.adicionarMoedas(alvoTravado.getRecompensa());
                    }
                }
                // 2. LÓGICA DAS OUTRAS TORRES
                else if (alvoTravado != null && alvoTravado.isAtivo()) {
                    // --- TOCA O SOM DO TIRO DEPENDENDO DA TORRE ---
                    if (this.tipo == Tipo.BAMBU) {
                        GerenciadorAudio.tocarSom(GerenciadorAudio.somSniper);
                    } else if (this.tipo == Tipo.SEMENTEIRA || this.tipo == Tipo.MACACO) {
                        GerenciadorAudio.tocarSom(GerenciadorAudio.somTiro);
                    } else if (this.tipo == Tipo.PLANTA) {
                        GerenciadorAudio.tocarSom(GerenciadorAudio.somFolhas);
                    }

                    todasEntidades.add(new Projetil(this.x + 15, this.y + 15, alvoTravado, this.dano, this.efeitoTiro, todasEntidades, jogoPrincipal));
                }

                atacando = false;
                tempoDesdeUltimoTiro = 0f;
            }
            return;
        }

        tempoDesdeUltimoTiro += deltaTime;
        if (tempoDesdeUltimoTiro >= taxaDeTiro) {
            for (EntidadeJogo ent : todasEntidades) {
                if (ent instanceof Inimigo && ent.isAtivo()) {
                    Inimigo inimigo = (Inimigo) ent;
                    if (Vector2.dst(this.x, this.y, inimigo.x, inimigo.y) <= alcance) {
                        this.alvoTravado = inimigo;
                        this.atacando = true;
                        this.tempoAnimacaoAtual = 0f; // Zera o cronômetro de ataque normal
                        break;
                    }
                }
            }
        }
    }
}
