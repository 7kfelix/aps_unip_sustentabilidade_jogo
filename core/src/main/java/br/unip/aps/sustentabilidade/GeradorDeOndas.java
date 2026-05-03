package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class GeradorDeOndas {

    private List<EntidadeJogo> todasEntidades;
    private List<Vector2> rotaDoMapa;
    private Texture texturaInimigo;

    // Lógica de Progressão das Ondas
    private int ondaAtual;
    private int totalInimigosNestaOnda;
    private int inimigosJaGerados;
    private float intervaloDeSpawn;

    // Controle de Tempo e Estado
    private float tempoAcumulado;
    private boolean emTempoDeDescanso;
    private float tempoDeDescansoEntreOndas;

    public GeradorDeOndas(List<EntidadeJogo> todasEntidades, List<Vector2> rotaDoMapa, Texture texturaInimigo) {
        this.todasEntidades = todasEntidades;
        this.rotaDoMapa = rotaDoMapa;
        this.texturaInimigo = texturaInimigo;

        // Configurações Iniciais (Onda 1)
        this.ondaAtual = 1;
        this.totalInimigosNestaOnda = 10;
        this.intervaloDeSpawn = 1.5f;
        this.inimigosJaGerados = 0;

        this.tempoAcumulado = 0f;
        this.emTempoDeDescanso = false;
        this.tempoDeDescansoEntreOndas = 5.0f; // 5 segundos de paz entre as ondas

        System.out.println("--- JOGO INICIADO: ONDA 1 ---");
    }

    public void atualizar(float deltaTime) {
        tempoAcumulado += deltaTime;

        // ESTADO 1: O jogo está no intervalo de descanso entre uma onda e outra
        if (emTempoDeDescanso) {
            if (tempoAcumulado >= tempoDeDescansoEntreOndas) {
                prepararProximaOnda();
            }
            return; // Interrompe o metodo aqui para não gerar inimigos no descanso
        }

        // ESTADO 2: Ainda precisamos gerar inimigos para a onda atual
        if (inimigosJaGerados < totalInimigosNestaOnda) {
            if (tempoAcumulado >= intervaloDeSpawn) {
                Inimigo novoInimigo = new Inimigo(0, 200, texturaInimigo, rotaDoMapa);
                todasEntidades.add(novoInimigo);

                inimigosJaGerados++;
                tempoAcumulado = 0f;
            }
        }
        // ESTADO 3: Já gerou todos. Precisamos verificar se o jogador já matou todos
        else {
            boolean temInimigoVivo = false;

            // Varre a lista de entidades procurando se sobrou algum inimigo
            for (EntidadeJogo entidade : todasEntidades) {
                if (entidade instanceof Inimigo) {
                    temInimigoVivo = true;
                    break; // Achou um vivo, já pode parar de procurar
                }
            }

            // Se não tem mais inimigos vivos na tela, a onda acabou!
            if (!temInimigoVivo) {
                System.out.println("Onda " + ondaAtual + " concluída! Você tem " + tempoDeDescansoEntreOndas + " segundos para se preparar.");
                emTempoDeDescanso = true;
                tempoAcumulado = 0f;
            }
        }
    }

    // Metodo que aplica a matemática para aumentar a dificuldade
    private void prepararProximaOnda() {
        ondaAtual++;

        // Matemática da progressão: Adiciona 5 inimigos a cada onda
        totalInimigosNestaOnda = 10 + ((ondaAtual - 1) * 5);

        // Matemática da velocidade: Reduz o tempo de spawn em 10%, mas não deixa ser menor que 0.4 segundos
        intervaloDeSpawn = Math.max(0.4f, intervaloDeSpawn - 0.1f);

        inimigosJaGerados = 0;
        tempoAcumulado = 0f;
        emTempoDeDescanso = false;

        System.out.println("--- INICIANDO ONDA " + ondaAtual + " | Inimigos: " + totalInimigosNestaOnda + " ---");
    }
}
