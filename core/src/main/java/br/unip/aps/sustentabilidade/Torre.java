package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Torre extends EntidadeJogo {

    private float alcance;
    private int dano;
    private float taxaDeTiro; // Tempo de recarga (cooldown) em segundos
    private float tempoDesdeUltimoTiro;

    // A torre precisa olhar para a lista geral para achar os inimigos
    private List<EntidadeJogo> todasEntidades;

    public Torre(float x, float y, Texture textura, List<EntidadeJogo> todasEntidades) {
        super(x, y, textura);
        this.todasEntidades = todasEntidades;

        // Configurações da nossa primeira Torre (Filtro de Ar / Cata-vento)
        this.alcance = 150.0f; // Raio de alcance em pixels
        this.dano = 50;        // Tira 50 de vida por tiro
        this.taxaDeTiro = 1.0f; // Atira 1 vez por segundo
        this.tempoDesdeUltimoTiro = 0f;
    }

    @Override
    public void atualizar(float deltaTime) {
        // O tempo não para! Vamos somando o tempo que passou.
        tempoDesdeUltimoTiro += deltaTime;

        // Só tenta procurar alvo se o "cooldown" do tiro já carregou
        if (tempoDesdeUltimoTiro >= taxaDeTiro) {

            // Procura por inimigos na lista
            for (EntidadeJogo entidade : todasEntidades) {
                // Verifica se é um Inimigo E se ele ainda está vivo
                if (entidade instanceof Inimigo && entidade.isAtivo()) {
                    Inimigo inimigo = (Inimigo) entidade;

                    float distancia = Vector2.dst(this.x, this.y, inimigo.x, inimigo.y);

                    if (distancia <= alcance) {
                        // AGORA É PRA VALER: CHAMA O METODO DE DANO DO INIMIGO
                        inimigo.receberDano(this.dano);

                        tempoDesdeUltimoTiro = 0f;
                        break;
                    }
                }
            }
        }
    }
}
