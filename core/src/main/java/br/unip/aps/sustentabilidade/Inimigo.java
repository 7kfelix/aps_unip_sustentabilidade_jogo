package br.unip.aps.sustentabilidade;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Inimigo extends EntidadeJogo {

    public enum Tipo { SACOLA, FUMACA, BARRIL, CHUVA, ONIBUS }

    private float velocidade;
    private int vida;
    private int vidaMaxima;

    // --- LÓGICA DE STUN ---
    private float tempoParalisado = 0f;

    // --- VARIÁVEIS DE EFEITOS VISUAIS (NOVO) ---
    private Texture texturaRaizes;
    private float tempoEnraizadoVisual = 0f;
    private float tempoMolhado = 0f;
    private Color corOriginal = Color.WHITE;
    private Color corImpacto = Color.WHITE;
    private float tempoCorImpacto = 0f;

    private int recompensa;
    private List<Vector2> caminho;
    private int indiceAlvoAtual;
    private TelaJogo jogoPrincipal;
    private Tipo tipo;

    private int ondaAtual;

    // --- VARIÁVEIS DE ANIMAÇÃO ---
    private TextureRegion[] framesAnimacao;
    private float tempoAnimacaoAtual = 0f;
    private int tamanhoQuadradoFallback; // Guarda o tamanho caso precise do quadrado colorido

    public Inimigo(float x, float y, Tipo tipo, List<Vector2> caminho, TelaJogo jogoPrincipal, int ondaAtual) {
        super(x, y, null);
        this.caminho = caminho;
        this.jogoPrincipal = jogoPrincipal;
        this.indiceAlvoAtual = 0;
        this.tipo = tipo;
        this.ondaAtual = ondaAtual;

        // status base
        float velocidadeBase;
        int vidaBase;
        Color cor;
        this.tamanhoQuadradoFallback = 30; // Padrão

        switch (tipo) {
            case ONIBUS:
                vidaBase = 3500;
                velocidadeBase = 45.0f;
                this.recompensa = 250;
                cor = Color.ORANGE;
                this.tamanhoQuadradoFallback = 50;
                break;
            case FUMACA:
                vidaBase = 40;
                velocidadeBase = 280.0f;
                this.recompensa = 3;
                cor = Color.DARK_GRAY;
                break;
            case BARRIL:
                vidaBase = 500;
                velocidadeBase = 70.0f;
                this.recompensa = 30;
                cor = Color.OLIVE;
                break;
            case CHUVA: // Vamos usar a nuvem.png para a Chuva!
                vidaBase = 20;
                velocidadeBase = 160.0f;
                this.recompensa = 1;
                cor = Color.PURPLE;
                break;
            case SACOLA:
            default:
                vidaBase = 100;
                velocidadeBase = 150.0f;
                this.recompensa = 4;
                cor = Color.WHITE;
                break;
        }

        float multiplicadorVida = 1.0f + ((ondaAtual - 1) * 0.15f);
        float multiplicadorVelocidade = 1.0f + ((ondaAtual - 1) * 0.02f);
        float multiplicadorDePico = 1.0f;

        if (ondaAtual >= 30) multiplicadorDePico = 4.0f;
        else if (ondaAtual >= 15) multiplicadorDePico = 2.0f;
        else if (ondaAtual >= 60) multiplicadorDePico = 10.5f;

        this.vida = (int) (vidaBase * multiplicadorVida * multiplicadorDePico);
        this.velocidade = velocidadeBase * multiplicadorVelocidade * multiplicadorDePico;

        if (this.velocidade > 450.0f) {
            this.velocidade = 450.0f;
        }

        this.vidaMaxima = this.vida;

        // --- SISTEMA AUTOMÁTICO DE CARREGAR SPRITESHEETS ---
        String nomeArquivo = "";
        switch (tipo) {
            case FUMACA: nomeArquivo = "fumaca.png"; break;
            case BARRIL: nomeArquivo = "barril.png"; break;
            case CHUVA:  nomeArquivo = "nuvem.png"; break;
            case ONIBUS: nomeArquivo = "onibus.png"; break;
            case SACOLA: default: nomeArquivo = "sacola.png"; break;
        }

        try {
            Texture spriteSheet = new Texture(nomeArquivo);
            int tamanhoFrame = spriteSheet.getHeight();
            int qtdFrames = spriteSheet.getWidth() / tamanhoFrame;

            framesAnimacao = new TextureRegion[qtdFrames];
            for (int i = 0; i < qtdFrames; i++) {
                framesAnimacao[i] = new TextureRegion(spriteSheet, i * tamanhoFrame, 0, tamanhoFrame, tamanhoFrame);
            }
            this.textura = null;
        } catch (Exception e) {
            System.out.println("Aviso: Imagem " + nomeArquivo + " nao encontrada. Usando quadrado provisorio.");
            this.textura = criarTexturaQuadrada(this.tamanhoQuadradoFallback, cor);
        }

        // --- CARREGA ARTE DAS RAÍZES (NOVO) ---
        try {
            texturaRaizes = new Texture("raizes.png");
        } catch (Exception e) {
            // Ignora se não achar, as raízes só não vão aparecer
        }
    }

    @Override
    public void renderizar(SpriteBatch batch) {

        float tamanhoVisual = 85f;

        if (this.tipo == Tipo.ONIBUS) {
            tamanhoVisual = 170f;
        } else if (this.tipo == Tipo.BARRIL) {
            tamanhoVisual = 120f;
        }

        float compensacao = (tamanhoVisual - this.tamanhoQuadradoFallback) / 2f;
        float desenhoX = this.x - compensacao;
        float desenhoY = this.y - compensacao;

        boolean deveInverter = false;

        if (caminho != null && indiceAlvoAtual < caminho.size()) {
            Vector2 proximoAlvo = caminho.get(indiceAlvoAtual);
            float difX = proximoAlvo.x - this.x;
            float difY = proximoAlvo.y - this.y;

            if (Math.abs(difX) > Math.abs(difY)) {
                if (difX > 0) {
                    deveInverter = true;
                }
            }
        }

        // --- TINTA O INIMIGO DE ACORDO COM O IMPACTO (NOVO) ---
        if (tempoCorImpacto > 0) {
            // Pisca com a cor do tiro (Geralmente Vermelho)
            batch.setColor(corImpacto);
        } else if (tempoMolhado > 0) {
            // Fica molhado (Azul Claro) após apanhar do Macaco ou Filtro
            batch.setColor(new Color(0.4f, 0.4f, 1.0f, 0.7f));
        } else if (tempoEnraizadoVisual > 0) {
            // Opcional: Fica com tom de terra/verde enquanto preso
            batch.setColor(new Color(0.6f, 0.4f, 0.2f, 1.0f));
        } else {
            // Cor Normal
            batch.setColor(Color.WHITE);
        }

        if (framesAnimacao != null) {
            int frameAtual = 0;
            if (framesAnimacao.length > 1) {
                frameAtual = (int) ((tempoAnimacaoAtual / 0.15f) % framesAnimacao.length);
            }

            batch.draw(
                framesAnimacao[frameAtual],
                desenhoX, desenhoY,
                tamanhoVisual / 2f, tamanhoVisual / 2f,
                tamanhoVisual, tamanhoVisual,
                deveInverter ? -1f : 1f, 1f,
                0f
            );
        } else if (this.textura != null) {
            batch.draw(textura, this.x, this.y, tamanhoQuadradoFallback, tamanhoQuadradoFallback);
        }

        // Reseta o batch imediatamente para não pintar o resto do jogo!
        batch.setColor(Color.WHITE);

        // --- DESENHA AS RAÍZES POR CIMA (NOVO) ---
        if (tempoEnraizadoVisual > 0 && texturaRaizes != null) {
            // Desenha as raízes cobrindo a base do inimigo
            batch.draw(texturaRaizes, desenhoX, desenhoY, tamanhoVisual, tamanhoVisual);
        }
    }

    private Texture criarTexturaQuadrada(int tamanho, Color cor) {
        Pixmap pixmap = new Pixmap(tamanho, tamanho, Pixmap.Format.RGBA8888);
        pixmap.setColor(cor);
        pixmap.fillRectangle(0, 0, tamanho, tamanho);
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    public boolean receberDano(int quantidadeDano) {
        this.vida -= quantidadeDano;

        if (this.vida <= 0 && this.ativo) {
            this.ativo = false;
            if (this.tipo == Tipo.ONIBUS) {
                explodirOnibus();
            }
            return true;
        }
        return false;
    }

    private void explodirOnibus() {
        System.out.println("O BUSÃO EXPLODIU! SOLTANDO TROPAS!");

        int quantidadeTropas = (this.ondaAtual / 20) * 5;
        if (quantidadeTropas < 5) quantidadeTropas = 5;

        for (int i = 0; i < quantidadeTropas; i++) {
            int sorteio = (int) (Math.random() * 4);
            Tipo tipoSorteado = Tipo.values()[sorteio];

            float offsetX = (float) (Math.random() * 30 - 15);
            float offsetY = (float) (Math.random() * 30 - 15);

            Inimigo novaTropa = new Inimigo(this.x + offsetX, this.y + offsetY, tipoSorteado, this.caminho, this.jogoPrincipal, this.ondaAtual);
            novaTropa.indiceAlvoAtual = this.indiceAlvoAtual;
            jogoPrincipal.adicionarEntidadeAoJogo(novaTropa);
        }
    }

    public int getRecompensa() { return recompensa; }
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }

    // --- MÉTODOS DE EFEITOS ESPECIAIS (NOVOS) ---
    public void aplicarEnraizamento(float tempo) {
        this.tempoParalisado = tempo;
        this.tempoEnraizadoVisual = tempo;
    }

    public void setMolhado(float duracao) {
        this.tempoMolhado = duracao;
    }

    public void setCorTemporaria(Color cor, float duracao) {
        this.corImpacto = cor;
        this.tempoCorImpacto = duracao;
    }

    @Override
    public void atualizar(float deltaTime) {
        if (caminho == null || indiceAlvoAtual >= caminho.size()) {
            if (this.ativo) {
                if (this.tipo == Tipo.ONIBUS) {
                    jogoPrincipal.sofrerDanoNaBase(9999);
                    System.out.println("O ONIBUS CHEGOU NA BASE! DESTRUIÇÃO TOTAL!");
                } else {
                    jogoPrincipal.sofrerDanoNaBase(1);
                }
                this.ativo = false;
            }
            return;
        }

        // --- ATUALIZA CRONÔMETROS VISUAIS (NOVO) ---
        if (tempoEnraizadoVisual > 0) tempoEnraizadoVisual -= deltaTime;
        if (tempoMolhado > 0) tempoMolhado -= deltaTime;
        if (tempoCorImpacto > 0) tempoCorImpacto -= deltaTime;

        // Se estiver com as raízes, ele não anda nem atualiza frame!
        if (tempoParalisado > 0) {
            tempoParalisado -= deltaTime;
            return;
        }

        // --- ATUALIZA A ANIMAÇÃO DE "ANDAR" ---
        tempoAnimacaoAtual += deltaTime;

        Vector2 destino = caminho.get(indiceAlvoAtual);
        float distanciaX = destino.x - this.x;
        float distanciaY = destino.y - this.y;
        float distanciaTotal = (float) Math.sqrt((distanciaX * distanciaX) + (distanciaY * distanciaY));

        if (distanciaTotal < 5.0f) {
            indiceAlvoAtual++;
        } else {
            this.x += (distanciaX / distanciaTotal) * velocidade * deltaTime;
            this.y += (distanciaY / distanciaTotal) * velocidade * deltaTime;
        }
    }
}
