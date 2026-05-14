package br.unip.aps.sustentabilidade;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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

    public Torre(float x, float y, Tipo tipo, List<EntidadeJogo> todasEntidades, TelaJogo jogoPrincipal) {
        super(x, y, null);
        this.tipo = tipo;
        this.todasEntidades = todasEntidades;
        this.jogoPrincipal = jogoPrincipal;
        this.tempoDesdeUltimoTiro = 0f;

        Color cor;
        switch (tipo) {
            case MACACO: // Splash AoE
                this.alcance = 150.0f; this.dano = 80; this.taxaDeTiro = 1.2f; cor = Color.CYAN;
                break;
            case PLANTA: // CC / Root
                this.alcance = 120.0f; this.dano = 10; this.taxaDeTiro = 0.5f; cor = Color.FOREST;
                break;
            case BAMBU: // Sniper (Moggador)
                this.alcance = 1500.0f; this.dano = 450; this.taxaDeTiro = 0.2f; cor = Color.WHITE;
                break;
            case FILTRO: // Dano Contínuo (Aura)
                this.alcance = 100.0f; this.dano = 35; this.taxaDeTiro = 0.2f; cor = Color.ROYAL;
                break;
            case ARVORE: // Economia (Gera 20 moedas a cada 10 segundos)
                this.alcance = 0f; this.dano = 0; this.taxaDeTiro = 10.0f; cor = Color.LIME;
                break;
            case SEMENTEIRA: // O Dart Monkey
            default:
                this.alcance = 150.0f; this.dano = 50; this.taxaDeTiro = 0.8f; cor = Color.BLUE;
                break;
        }
        this.custo = getCusto(tipo);
        this.textura = criarTexturaQuadrada(40, cor);
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
        switch (tipo) {
            case MACACO: return 150.0f;
            case PLANTA: return 120.0f;
            case BAMBU: return 1500.0f;
            case FILTRO: return 100.0f;
            case ARVORE: return 0f;
            case SEMENTEIRA: default: return 150.0f;
        }
    }

    // MÉTODOS NOVOS PARA RECEBER OS UPGRADES DA LOJA
    public void buffAlcance(float multiplicador) {
        this.alcance *= multiplicador;
    }

    public void buffRecarga(float multiplicador) {
        this.taxaDeTiro *= multiplicador;
    }

    public void buffDano(float multiplicador) {
        this.dano = (int)(this.dano * multiplicador);
    }

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
        tempoDesdeUltimoTiro += deltaTime;

        if (tempoDesdeUltimoTiro >= taxaDeTiro) {

            // LÓGICA ESPECIAL DA ÁRVORE DE DINHEIRO
            if (this.tipo == Tipo.ARVORE) {
                jogoPrincipal.adicionarMoedas(20);
                System.out.println("Árvore gerou recursos!");
                tempoDesdeUltimoTiro = 0f;
                return; // Encerra aqui, a árvore não atira.
            }

            // LÓGICA DE TIRO PARA AS OUTRAS TORRES
            for (EntidadeJogo entidade : todasEntidades) {
                if (entidade instanceof Inimigo && entidade.isAtivo()) {
                    Inimigo inimigo = (Inimigo) entidade;
                    float distancia = Vector2.dst(this.x, this.y, inimigo.x, inimigo.y);

                    if (distancia <= alcance) {

                        // O FILTRO DÁ DANO DIRETO (Sem criar projétil)
                        if (this.tipo == Tipo.FILTRO) {
                            boolean tiroFatal = inimigo.receberDano(this.dano);
                            if (tiroFatal) jogoPrincipal.adicionarMoedas(inimigo.getRecompensa());
                        }
                        // AS OUTRAS CRIAM PROJÉTEIS COM SEUS EFEITOS ESPECÍFICOS
                        else {
                            Projetil.Efeito efeito = Projetil.Efeito.NORMAL;
                            if (this.tipo == Tipo.MACACO) efeito = Projetil.Efeito.AREA;
                            if (this.tipo == Tipo.PLANTA) efeito = Projetil.Efeito.PARALISAR;

                            Projetil novoTiro = new Projetil(this.x + 15, this.y + 15, inimigo, this.dano, efeito, todasEntidades, jogoPrincipal);
                            todasEntidades.add(novoTiro);
                        }

                        tempoDesdeUltimoTiro = 0f;
                        break; // Atirou em um, para de procurar.

                    }
                }
            }
        }
    }
}
