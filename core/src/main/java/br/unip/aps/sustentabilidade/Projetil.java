package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Projetil extends EntidadeJogo {

    public enum Efeito { NORMAL, AREA, PARALISAR }

    private Inimigo alvo;
    private int dano;
    private float velocidade;
    private TelaJogo jogoPrincipal;

    // NOVOS ATRIBUTOS DE EFEITO
    private Efeito efeito;
    private List<EntidadeJogo> todasEntidades; // Precisamos da lista para calcular a explosão em área

    public Projetil(float x, float y, Inimigo alvo, int dano, Efeito efeito, List<EntidadeJogo> todasEntidades, TelaJogo jogoPrincipal) {
        super(x, y, null);
        this.alvo = alvo;
        this.dano = dano;
        this.efeito = efeito;
        this.todasEntidades = todasEntidades;
        this.jogoPrincipal = jogoPrincipal;
        this.velocidade = 600.0f;

        Color corPincel = Color.ORANGE; // Semente padrão
        if (efeito == Efeito.AREA) corPincel = Color.CYAN; // Balão de água
        if (efeito == Efeito.PARALISAR) corPincel = Color.GREEN; // Mordida/Raiz

        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(corPincel);
        pixmap.fillRectangle(0, 0, 10, 10);
        this.textura = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void atualizar(float deltaTime) {
        if (!alvo.isAtivo()) {
            this.ativo = false;
            return;
        }

        float distanciaX = alvo.x - this.x;
        float distanciaY = alvo.y - this.y;
        float distanciaTotal = (float) Math.sqrt((distanciaX * distanciaX) + (distanciaY * distanciaY));

        // BATEU NO INIMIGO!
        if (distanciaTotal < 15.0f) {

            // 1. APLICA O DANO PRINCIPAL
            boolean tiroFatal = alvo.receberDano(this.dano);
            if (tiroFatal) jogoPrincipal.adicionarMoedas(alvo.getRecompensa());

            // 2. APLICA OS EFEITOS ESPECIAIS
            if (efeito == Efeito.PARALISAR) {
                alvo.aplicarEnraizamento(2.0f); // Trava o inimigo por 2 segundos
            }
            else if (efeito == Efeito.AREA) {
                // Procura todo mundo que está perto da explosão (raio de 80 pixels)
                for (EntidadeJogo entidade : todasEntidades) {
                    if (entidade instanceof Inimigo && entidade.isAtivo() && entidade != alvo) {
                        Inimigo outroInimigo = (Inimigo) entidade;
                        float distExplosao = Vector2.dst(this.x, this.y, outroInimigo.x, outroInimigo.y);

                        if (distExplosao <= 80f) {
                            // Causa metade do dano em quem tomou o "respingo"
                            boolean respingoFatal = outroInimigo.receberDano(this.dano / 2);
                            if (respingoFatal) jogoPrincipal.adicionarMoedas(outroInimigo.getRecompensa());
                        }
                    }
                }
            }

            this.ativo = false; // Se destrói
        } else {
            this.x += (distanciaX / distanciaTotal) * velocidade * deltaTime;
            this.y += (distanciaY / distanciaTotal) * velocidade * deltaTime;
        }
    }
}
