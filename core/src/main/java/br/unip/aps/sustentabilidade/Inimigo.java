package br.unip.aps.sustentabilidade;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Inimigo extends EntidadeJogo {

    public enum Tipo { SACOLA, FUMACA, BARRIL, CHUVA }

    private float velocidade;
    private int vida;

    // NOSSOS NOVOS ATRIBUTOS
    private int vidaMaxima;
    private float tempoParalisado = 0f;

    private int recompensa;
    private List<Vector2> caminho;
    private int indiceAlvoAtual;
    private TelaJogo jogoPrincipal;
    private Tipo tipo;

    // onda atual adicionado
    public Inimigo(float x, float y, Tipo tipo, List<Vector2> caminho, TelaJogo jogoPrincipal, int ondaAtual) {
        super(x, y, null);
        this.caminho = caminho;
        this.jogoPrincipal = jogoPrincipal;
        this.indiceAlvoAtual = 0;
        this.tipo = tipo;

        // status base
        float velocidadeBase;
        int vidaBase;
        Color cor;

        switch (tipo) {
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
            case CHUVA:
                vidaBase = 20;
                velocidadeBase = 160.0f;
                this.recompensa = 1;
                cor = Color.PURPLE;
                break;
            case SACOLA:
            default:
                vidaBase = 100;
                velocidadeBase = 150.0f;
                this.recompensa = 4; // Economia nerfada que conversamos
                cor = Color.WHITE;
                break;
        }

        // SISTEMA DE ESCALONAMENTO CONTÍNUO
        // Aumenta vida e velocidade por onda passada
        float multiplicadorVida = 1.0f + ((ondaAtual - 1) * 0.15f);
        float multiplicadorVelocidade = 1.0f + ((ondaAtual - 1) * 0.02f);

        // 3. PICOS DE DIFICULDADE (Ondas 15 e 30)
        float multiplicadorDePico = 1.0f;
        if (ondaAtual >= 30) {
            multiplicadorDePico = 4.0f; // Dobra (x2 na 15) e dobra de novo (x2 na 30) = x4
        } else if (ondaAtual >= 15) {
            multiplicadorDePico = 2.0f; // Dobra a partir da onda 15
        }

        // 4. APLICAMOS OS BUFFS TOTAIS
        this.vida = (int) (vidaBase * multiplicadorVida * multiplicadorDePico);
        this.velocidade = velocidadeBase * multiplicadorVelocidade * multiplicadorDePico;

        // Limite de velocidade de segurança para o inimigo não "teleportar" 
        // e atravessar as curvas da estrada em ondas muito altas
        if (this.velocidade > 450.0f) {
            this.velocidade = 450.0f;
        }

        this.vidaMaxima = this.vida;
        this.textura = criarTexturaQuadrada(30, cor);
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
        if (this.vida <= 0) {
            this.ativo = false;
            return true;
        }
        return false;
    }

    public int getRecompensa() {
        return recompensa;
    }

    public void aplicarEnraizamento(float tempo) {
        this.tempoParalisado = tempo;
    }

    // GETTERS PARA A TELA DE JOGO DESENHAR A BARRA
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }

    @Override
    public void atualizar(float deltaTime) {
        if (caminho == null || indiceAlvoAtual >= caminho.size()) {
            if (this.ativo) {
                jogoPrincipal.sofrerDanoNaBase(1);
                this.ativo = false;
            }
            return;
        }

        if (tempoParalisado > 0) {
            tempoParalisado -= deltaTime;
            return;
        }

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