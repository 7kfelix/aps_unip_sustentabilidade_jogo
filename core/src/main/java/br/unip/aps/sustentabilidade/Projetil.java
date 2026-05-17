package br.unip.aps.sustentabilidade;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils; // Importante para variação
import com.badlogic.gdx.math.Vector2;

public class Projetil extends EntidadeJogo {

    public enum Efeito { NORMAL, AREA, PARALISAR }

    private Inimigo alvo;
    private int dano;
    private float velocidade;
    private TelaJogo jogoPrincipal;

    private Efeito efeito;
    private List<EntidadeJogo> todasEntidades;

    private float dirX;
    private float dirY;

    public Projetil(float x, float y, Inimigo alvo, int dano, Efeito efeito, List<EntidadeJogo> todasEntidades, TelaJogo jogoPrincipal) {
        super(x, y, null);

        // --- AJUSTE DE POSIÇÃO INICIAL ---
        this.x = x + 5;
        this.y = y + 35; // Sai da altura do peito/boca da torre

        this.alvo = alvo;
        this.dano = dano;
        this.efeito = efeito;
        this.todasEntidades = todasEntidades;
        this.jogoPrincipal = jogoPrincipal;

        // Velocidade equilibrada para ser visível
        this.velocidade = 450.0f;

        float distX = alvo.x - this.x;
        float distY = alvo.y - this.y;
        float distTotal = (float) Math.sqrt((distX * distX) + (distY * distY));
        if (distTotal > 0) {
            this.dirX = distX / distTotal;
            this.dirY = distY / distTotal;
        }

        // --- ARTE DO PROJÉTIL MELHORADA ---
        int tamPixmap = 20; // Um pouco maior para dar detalhes
        Pixmap pixmap = new Pixmap(tamPixmap, tamPixmap, Pixmap.Format.RGBA8888);

        if (efeito == Efeito.AREA) {
            // --- BALÃO DE ÁGUA DO MACACO (Forma de Gota Irregular) ---
            pixmap.setColor(new Color(0.2f, 0.6f, 1.0f, 0.8f)); // Azul água transparente
            // Corpo principal
            pixmap.fillCircle(tamPixmap/2, tamPixmap/2 + 2, 6);
            // Ponta da gota (atrás na direção do tiro)
            pixmap.fillTriangle(tamPixmap/2 - 5, tamPixmap/2, tamPixmap/2 + 5, tamPixmap/2, tamPixmap/2, tamPixmap/2 - 8);
        } else if (efeito == Efeito.PARALISAR) {
            // --- ESPINHO DE PLANTA (Verde Escuro pontudo) ---
            pixmap.setColor(new Color(0.1f, 0.5f, 0.1f, 1.0f));
            pixmap.fillTriangle(0, tamPixmap/2, tamPixmap, tamPixmap/2 - 3, tamPixmap, tamPixmap/2 + 3);
        } else {
            // --- SEMENTE NORMAL (Laranja Oval) ---
            pixmap.setColor(Color.ORANGE);
            pixmap.fillCircle(tamPixmap/2, tamPixmap/2, 5); // Base redonda
        }

        this.textura = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        if (this.textura != null && this.ativo) {
            // Desenha centralizado na colisão (assumindo tamanho visual 20x20)
            batch.draw(this.textura, this.x - 10, this.y - 10, 20, 20);
        }
    }

    @Override
    public void atualizar(float deltaTime) {

        if (alvo.isAtivo()) {
            float distanciaX = alvo.x - this.x;
            float distanciaY = alvo.y - this.y;
            float distanciaTotal = (float) Math.sqrt((distanciaX * distanciaX) + (distanciaY * distanciaY));

            if (distanciaTotal > 0) {
                this.dirX = distanciaX / distanciaTotal;
                this.dirY = distanciaY / distanciaTotal;
            }

            if (distanciaTotal < 15.0f) {

                // 1. Aplica o Dano Principal
                boolean tiroFatal = alvo.receberDano(this.dano);

                // --- VISUAL: IMPACTO VERMELHO (Tiro Vermelho) ---
                // Cria uma variação rápida de cor vermelha no inimigo
                alvo.setCorTemporaria(Color.RED, 0.1f);

                // --- VISUAL: SONS DE IMPACTO ---
                if (efeito == Efeito.AREA) {
                    GerenciadorAudio.tocarSom(GerenciadorAudio.somBalao);
                } else if (efeito == Efeito.PARALISAR) {
                    GerenciadorAudio.tocarSom(GerenciadorAudio.somEnraizar);
                }

                // 2. Texto do dano flutuante (mudei macaco para azul e planta para verde)
                Color corDano = Color.WHITE;
                if (efeito == Efeito.AREA) corDano = Color.CYAN;
                if (efeito == Efeito.PARALISAR) corDano = Color.GREEN;

                jogoPrincipal.adicionarDanoFlutuante(alvo.x, alvo.y, this.dano, corDano);

                if (tiroFatal) {
                    GerenciadorAudio.tocarSom(GerenciadorAudio.somMorte);
                    jogoPrincipal.adicionarMoedas(alvo.getRecompensa());
                }

                // 3. Aplica os efeitos secundários E VISUAIS nos inimigos
                if (efeito == Efeito.PARALISAR) {
                    // Prende o inimigo por 2 segundos e ativa as RAÍZES
                    alvo.aplicarEnraizamento(2.0f);
                }
                else if (efeito == Efeito.AREA) {
                    // --- VISUAL DO MACACO: IMPACTO VERMELHO EXCLUSIVO NO ALVO PRINCIPAL ---
                    // O alvo principal já ficou vermelho lá em cima.

                    // Lógica do Splash de Dano em Área
                    for (EntidadeJogo entidade : todasEntidades) {
                        if (entidade instanceof Inimigo && entidade.isAtivo() && entidade != alvo) {
                            Inimigo outroInimigo = (Inimigo) entidade;
                            float distExplosao = Vector2.dst(this.x, this.y, outroInimigo.x, outroInimigo.y);

                            if (distExplosao <= 80f) {
                                int danoSplash = this.dano / 2;
                                boolean respingoFatal = outroInimigo.receberDano(danoSplash);

                                // Inimigos do splash ficam só "MOLHADOS" (Azul), não vermelhos.
                                outroInimigo.setMolhado(1.5f);

                                jogoPrincipal.adicionarDanoFlutuante(outroInimigo.x, outroInimigo.y, danoSplash, Color.CYAN);

                                if (respingoFatal) {
                                    GerenciadorAudio.tocarSom(GerenciadorAudio.somMorte);
                                    jogoPrincipal.adicionarMoedas(outroInimigo.getRecompensa());
                                }
                            }
                        }
                    }
                }

                this.ativo = false;
                return;
            }
        }

        this.x += dirX * velocidade * deltaTime;
        this.y += dirY * velocidade * deltaTime;

        if (this.x < -50 || this.x > 1330 || this.y < -50 || this.y > 770) {
            this.ativo = false;
        }
    }
}
