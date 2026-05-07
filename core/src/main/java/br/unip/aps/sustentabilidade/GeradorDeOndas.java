package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class GeradorDeOndas {

    private List<EntidadeJogo> todasEntidades;
    private List<Vector2> rotaDoMapa;

    // ATUALIZADO: Agora ele se comunica com a TelaJogo
    private TelaJogo telaJogo;

    private int ondaAtual;
    private int totalInimigosNestaOnda;
    private int inimigosJaGerados;
    private float intervaloDeSpawn;

    private float tempoAcumulado;
    private boolean emTempoDeDescanso;
    private float tempoDeDescansoEntreOndas;

    // ATUALIZADO: Construtor limpo, recebendo apenas 3 parâmetros
    public GeradorDeOndas(List<EntidadeJogo> todasEntidades, List<Vector2> rotaDoMapa, TelaJogo telaJogo) {
        this.todasEntidades = todasEntidades;
        this.rotaDoMapa = rotaDoMapa;
        this.telaJogo = telaJogo;

        this.ondaAtual = 1;
        this.totalInimigosNestaOnda = 10;
        this.intervaloDeSpawn = 1.5f;
        this.inimigosJaGerados = 0;
        this.tempoAcumulado = 0f;
        this.emTempoDeDescanso = false;
        this.tempoDeDescansoEntreOndas = 5.0f;
    }

    public void atualizar(float deltaTime) {
        tempoAcumulado += deltaTime;

        if (emTempoDeDescanso) {
            if (tempoAcumulado >= tempoDeDescansoEntreOndas) {
                prepararProximaOnda();
            }
            return;
        }

        if (inimigosJaGerados < totalInimigosNestaOnda) {
            if (tempoAcumulado >= intervaloDeSpawn) {

                // SISTEMA DE SORTEIO BASEADO NA ONDA
                Inimigo.Tipo tipoSorteado = Inimigo.Tipo.SACOLA; // Padrão

                // Um número aleatório de 0.0 a 1.0 para definir a chance
                float sorteio = (float) Math.random();

                if (ondaAtual >= 4 && sorteio > 0.85f) {
                    tipoSorteado = Inimigo.Tipo.BARRIL;  // 15% de chance a partir da onda 4
                }
                else if (ondaAtual >= 3 && sorteio > 0.60f) {
                    tipoSorteado = Inimigo.Tipo.CHUVA;   // 25% de chance a partir da onda 3
                }
                else if (ondaAtual >= 2 && sorteio > 0.30f) {
                    tipoSorteado = Inimigo.Tipo.FUMACA;  // 30% de chance a partir da onda 2
                }

                // Se o inimigo sorteado for CHUVA, fazemos o próximo inimigo nascer
                // BEM mais rápido para criar o efeito de "Enxame"
                if (tipoSorteado == Inimigo.Tipo.CHUVA) {
                    tempoAcumulado = intervaloDeSpawn - 0.2f; // Corta o cooldown do gerador quase a zero
                } else {
                    tempoAcumulado = 0f; // Reseta normal
                }

                Inimigo novoInimigo = new Inimigo(200, 720, tipoSorteado, rotaDoMapa, telaJogo);
                todasEntidades.add(novoInimigo);

                inimigosJaGerados++;
            }
        }
        else {
            boolean temInimigoVivo = false;
            for (EntidadeJogo entidade : todasEntidades) {
                if (entidade instanceof Inimigo) {
                    temInimigoVivo = true;
                    break;
                }
            }
            if (!temInimigoVivo) {
                emTempoDeDescanso = true;
                tempoAcumulado = 0f;
            }
        }
    }

    private void prepararProximaOnda() {
        ondaAtual++;
        totalInimigosNestaOnda = 10 + ((ondaAtual - 1) * 5);
        intervaloDeSpawn = Math.max(0.4f, intervaloDeSpawn - 0.1f);
        inimigosJaGerados = 0;
        tempoAcumulado = 0f;
        emTempoDeDescanso = false;
    }

    public int getOndaAtual() {
        return ondaAtual;
    }
}
