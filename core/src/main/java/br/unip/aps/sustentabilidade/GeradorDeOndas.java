package br.unip.aps.sustentabilidade;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

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
    private boolean ondasIniciadas = false;

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

    // 2. ADICIONE ESTES DOIS MÉTODOS NOVOS (Pode ser embaixo do construtor):
    public void iniciarOndas() {
        this.ondasIniciadas = true;
    }

    public boolean isOndasIniciadas() {
        return ondasIniciadas;
    }

    // 3. ATUALIZE O INÍCIO DO MÉTODO atualizar():
    public void atualizar(float deltaTime) {
        // Se as ondas não foram iniciadas, ele simplesmente cancela o método e não conta o tempo
        if (!ondasIniciadas) {
            return;
        }

        tempoAcumulado += deltaTime;

        if (emTempoDeDescanso) {
            if (tempoAcumulado >= tempoDeDescansoEntreOndas) {
                prepararProximaOnda();
            }
            return;
        }

        if (inimigosJaGerados < totalInimigosNestaOnda) {
            if (tempoAcumulado >= intervaloDeSpawn) {

                Inimigo.Tipo tipoSorteado = Inimigo.Tipo.SACOLA;

                // SE FOR MÚLTIPLO DE 20, NASCE SÓ O ONIBUS!
                if (ondaAtual % 20 == 0) {
                    tipoSorteado = Inimigo.Tipo.ONIBUS;
                    tempoAcumulado = 0f; // Reseta normal
                }
                else {
                    // SORTEIO NORMAL PARA AS OUTRAS ONDAS
                    float sorteio = (float) Math.random();

                    if (ondaAtual >= 4 && sorteio > 0.85f) {
                        tipoSorteado = Inimigo.Tipo.BARRIL;
                    }
                    else if (ondaAtual >= 3 && sorteio > 0.60f) {
                        tipoSorteado = Inimigo.Tipo.CHUVA;
                    }
                    else if (ondaAtual >= 2 && sorteio > 0.30f) {
                        tipoSorteado = Inimigo.Tipo.FUMACA;
                    }

                    if (tipoSorteado == Inimigo.Tipo.CHUVA) {
                        tempoAcumulado = intervaloDeSpawn - 0.2f;
                    } else {
                        tempoAcumulado = 0f;
                    }
                }

                Inimigo novoInimigo = new Inimigo(200, 720, tipoSorteado, rotaDoMapa, telaJogo, ondaAtual);
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


    // MÉTODO NOVO: Força o gerador a pular para uma onda específica
    public void pularParaOnda(int novaOnda) {
        this.ondasIniciadas = true; // Garante que comece se pular a onda!
        this.ondaAtual = novaOnda;

        // Configura a quantidade de inimigos (1 se for Boss, normal se for outra)
        if (ondaAtual % 20 == 0) {
            totalInimigosNestaOnda = 1;
        } else {
            totalInimigosNestaOnda = 10 + ((ondaAtual - 1) * 5);
        }

        // Reseta os cronômetros para a onda começar na hora
        this.inimigosJaGerados = 0;
        this.tempoAcumulado = 0f;
        this.emTempoDeDescanso = false;
    }

    private void prepararProximaOnda() {
        ondaAtual++;

        // SE FOR ONDA DE BOSS (20, 40, 60...), GERA APENAS 1 INIMIGO!
        if (ondaAtual % 20 == 0) {
            totalInimigosNestaOnda = 1;
            System.out.println("ALERTA DE BOSS! PREPARE-SE!");
        } else {
            // Lógica normal de inimigos para as outras ondas
            totalInimigosNestaOnda = 10 + ((ondaAtual - 1) * 5);
        }

        intervaloDeSpawn = Math.max(0.4f, intervaloDeSpawn - 0.1f);
        inimigosJaGerados = 0;
        tempoAcumulado = 0f;
        emTempoDeDescanso = false;
    }

    public int getOndaAtual() {
        return ondaAtual;
    }
}
