package br.unip.aps.sustentabilidade;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Projetil extends EntidadeJogo {

    public enum Efeito { NORMAL, AREA, PARALISAR }

    private Inimigo alvo;
    private int dano;
    private float velocidade;
    private TelaJogo jogoPrincipal;

    private Efeito efeito;
    private List<EntidadeJogo> todasEntidades;

    // NOVAS VARIÁVEIS DE DIREÇÃO
    private float dirX;
    private float dirY;

    public Projetil(float x, float y, Inimigo alvo, int dano, Efeito efeito, List<EntidadeJogo> todasEntidades, TelaJogo jogoPrincipal) {
        super(x, y, null);
        this.alvo = alvo;
        this.dano = dano;
        this.efeito = efeito;
        this.todasEntidades = todasEntidades;
        this.jogoPrincipal = jogoPrincipal;
        this.velocidade = 600.0f;

        // Calcula a direção inicial logo no momento do disparo
        float distX = alvo.x - this.x;
        float distY = alvo.y - this.y;
        float distTotal = (float) Math.sqrt((distX * distX) + (distY * distY));
        if (distTotal > 0) {
            this.dirX = distX / distTotal;
            this.dirY = distY / distTotal;
        }

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
        
        // SE O ALVO ESTIVER VIVO: Atualiza a direção (Tiro Teleguiado) e verifica colisão
        if (alvo.isAtivo()) {
            float distanciaX = alvo.x - this.x;
            float distanciaY = alvo.y - this.y;
            float distanciaTotal = (float) Math.sqrt((distanciaX * distanciaX) + (distanciaY * distanciaY));

            if (distanciaTotal > 0) {
                this.dirX = distanciaX / distanciaTotal;
                this.dirY = distanciaY / distanciaTotal;
            }

            // bateu no inimigo
            if (distanciaTotal < 15.0f) {

                // Aplica o dano principal
                boolean tiroFatal = alvo.receberDano(this.dano);
                if (tiroFatal) jogoPrincipal.adicionarMoedas(alvo.getRecompensa());

                // Aplica os efeitos especiais
                if (efeito == Efeito.PARALISAR) {
                    alvo.aplicarEnraizamento(2.0f);
                }
                else if (efeito == Efeito.AREA) {
                    for (EntidadeJogo entidade : todasEntidades) {
                        if (entidade instanceof Inimigo && entidade.isAtivo() && entidade != alvo) {
                            Inimigo outroInimigo = (Inimigo) entidade;
                            float distExplosao = Vector2.dst(this.x, this.y, outroInimigo.x, outroInimigo.y);

                            if (distExplosao <= 80f) {
                                boolean respingoFatal = outroInimigo.receberDano(this.dano / 2);
                                if (respingoFatal) jogoPrincipal.adicionarMoedas(outroInimigo.getRecompensa());
                            }
                        }
                    }
                }

                this.ativo = false; // Tiro se destrói ao acertar
                return; // Sai do método para não andar mais
            }
        }

        // tiro some so dps q bate no fim da tela
        // (Seja teleguiado pro alvo vivo, ou indo reto na última direção pro alvo morto)
        this.x += dirX * velocidade * deltaTime;
        this.y += dirY * velocidade * deltaTime;

        if (this.x < -50 || this.x > 1330 || this.y < -50 || this.y > 770) {
            this.ativo = false;
        }
    }
}