package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

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

    public Inimigo(float x, float y, Tipo tipo, List<Vector2> caminho, TelaJogo jogoPrincipal) {
        super(x, y, null);
        this.caminho = caminho;
        this.jogoPrincipal = jogoPrincipal;
        this.indiceAlvoAtual = 0;
        this.tipo = tipo;

        Color cor;
        switch (tipo) {
            case FUMACA:
                this.vida = 40;
                this.velocidade = 280.0f;
                this.recompensa = 5;
                cor = Color.DARK_GRAY;
                break;
            case BARRIL:
                this.vida = 500;
                this.velocidade = 70.0f;
                this.recompensa = 30;
                cor = Color.OLIVE;
                break;
            case CHUVA:
                this.vida = 20;
                this.velocidade = 160.0f;
                this.recompensa = 2;
                cor = Color.PURPLE;
                break;
            case SACOLA:
            default:
                this.vida = 100;
                this.velocidade = 150.0f;
                this.recompensa = 10;
                cor = Color.WHITE;
                break;
        }

        // A lógica matemática precisa ficar AQUI DENTRO do construtor!
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
