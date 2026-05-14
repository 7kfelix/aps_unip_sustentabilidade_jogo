package br.unip.aps.sustentabilidade;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Inimigo extends EntidadeJogo {

    // NOVO: Adicionado o ONIBUS
    public enum Tipo { SACOLA, FUMACA, BARRIL, CHUVA, ONIBUS }

    private float velocidade;
    private int vida;

    private int vidaMaxima;
    private float tempoParalisado = 0f;

    private int recompensa;
    private List<Vector2> caminho;
    private int indiceAlvoAtual;
    private TelaJogo jogoPrincipal;
    private Tipo tipo;

    // NOVO: Precisamos salvar a onda atual para saber quantos inimigos o Boss vai cuspir
    private int ondaAtual;

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
        int tamanhoQuadrado = 30; // Padrão

        switch (tipo) {
            case ONIBUS: // STATUS DO BOSS
                vidaBase = 3500; // Vida absurda base (ainda vai multiplicar pela onda!)
                velocidadeBase = 45.0f; // Bem lento, um trator
                this.recompensa = 250;
                cor = Color.ORANGE; // Visual do busão
                tamanhoQuadrado = 50; // Maior que os outros!
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
                this.recompensa = 4;
                cor = Color.WHITE;
                break;
        }

        float multiplicadorVida = 1.0f + ((ondaAtual - 1) * 0.15f);
        float multiplicadorVelocidade = 1.0f + ((ondaAtual - 1) * 0.02f);

        float multiplicadorDePico = 1.0f;
        if (ondaAtual >= 30) {
            multiplicadorDePico = 4.0f;
        } else if (ondaAtual >= 15) {
            multiplicadorDePico = 2.0f;
        }
        else if (ondaAtual >= 60) {
            multiplicadorDePico = 10.5f;
        }

        this.vida = (int) (vidaBase * multiplicadorVida * multiplicadorDePico);
        this.velocidade = velocidadeBase * multiplicadorVelocidade * multiplicadorDePico;

        if (this.velocidade > 450.0f) {
            this.velocidade = 450.0f;
        }

        this.vidaMaxima = this.vida;

        // Passamos o tamanho variável agora para o ônibus ficar gordão
        this.textura = criarTexturaQuadrada(tamanhoQuadrado, cor);
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

            // NOVO: Se quem morreu for o Ônibus, ativa o Cavalo de Troia!
            if (this.tipo == Tipo.ONIBUS) {
                explodirOnibus();
            }

            return true;
        }
        return false;
    }

    // NOVO: A Lógica de soltar as tropas
    private void explodirOnibus() {
        System.out.println("O BUSÃO EXPLODIU! SOLTANDO TROPAS!");

        // Aumenta de 20 em 20. Onda 20 = 5 tropas | Onda 40 = 10 tropas | Onda 60 = 15 tropas...
        int quantidadeTropas = (this.ondaAtual / 20) * 5;
        if (quantidadeTropas < 5) quantidadeTropas = 5; // Garantia

        for (int i = 0; i < quantidadeTropas; i++) {
            // Sorteia um número de 0 a 3 (Ignora o ônibus para não criar um loop infinito)
            int sorteio = (int) (Math.random() * 4);
            Tipo tipoSorteado = Tipo.values()[sorteio];

            // Coloca um pequeno desvio (offset) na posição para as tropas não nascerem 100% grudadas
            float offsetX = (float) (Math.random() * 30 - 15);
            float offsetY = (float) (Math.random() * 30 - 15);

            Inimigo novaTropa = new Inimigo(this.x + offsetX, this.y + offsetY, tipoSorteado, this.caminho, this.jogoPrincipal, this.ondaAtual);

            // A Tropa precisa saber em qual ponto do mapa o Ônibus morreu, para continuar dali
            novaTropa.indiceAlvoAtual = this.indiceAlvoAtual;

            // Injeta a tropa direto na lista do jogo!
            jogoPrincipal.adicionarEntidadeAoJogo(novaTropa);
        }
    }

    public int getRecompensa() {
        return recompensa;
    }

    public void aplicarEnraizamento(float tempo) {
        this.tempoParalisado = tempo;
    }

    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }

    @Override
    public void atualizar(float deltaTime) {
        if (caminho == null || indiceAlvoAtual >= caminho.size()) {
            if (this.ativo) {
                // NOVO: Hit Kill se o ônibus chegar no núcleo
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
