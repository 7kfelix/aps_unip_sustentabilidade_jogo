package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Inimigo extends EntidadeJogo {

    private float velocidade;
    private int vida;

    // A rota que o inimigo deve seguir e qual ponto ele está buscando agora
    private List<Vector2> caminho;
    private int indiceAlvoAtual;

    public Inimigo(float x, float y, Texture textura, List<Vector2> caminho) {
        super(x, y, textura);
        this.velocidade = 150.0f;
        this.vida = 100;
        this.caminho = caminho;
        this.indiceAlvoAtual = 0; // Começa buscando o primeiro ponto da lista
    }

    @Override
    public void atualizar(float deltaTime) {
        // Se não tem caminho ou já chegou no final (no Núcleo da Natureza), ele para de andar
        if (caminho == null || indiceAlvoAtual >= caminho.size()) {
            return;
        }

        // Descobre qual é o X e Y do ponto que ele tem que chegar
        Vector2 destino = caminho.get(indiceAlvoAtual);

        // Calcula a distância geométrica entre o inimigo e o alvo
        float distanciaX = destino.x - this.x;
        float distanciaY = destino.y - this.y;

        // Teorema de Pitágoras para saber a distância total em linha reta
        float distanciaTotal = (float) Math.sqrt((distanciaX * distanciaX) + (distanciaY * distanciaY));

        // Se chegou muito perto do ponto alvo (margem de 5 pixels), muda para o próximo ponto
        if (distanciaTotal < 5.0f) {
            indiceAlvoAtual++;
        } else {
            // Se ainda não chegou, continua andando na direção do alvo
            // A matemática aqui "normaliza" a direção para ele andar na velocidade correta
            this.x += (distanciaX / distanciaTotal) * velocidade * deltaTime;
            this.y += (distanciaY / distanciaTotal) * velocidade * deltaTime;
        }
    }
}
