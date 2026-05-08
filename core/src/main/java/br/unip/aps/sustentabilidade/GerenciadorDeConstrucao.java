package br.unip.aps.sustentabilidade;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class GerenciadorDeConstrucao {

    private SustentabilidadeGame game;
    private TelaJogo telaJogo;
    private List<EntidadeJogo> entidades;
    private List<Vector2> rotaDoMapa;

    private Torre.Tipo torreSelecionada = Torre.Tipo.SEMENTEIRA;
    private float mouseX;
    private float mouseY;

    public GerenciadorDeConstrucao(SustentabilidadeGame game, TelaJogo telaJogo, List<EntidadeJogo> entidades, List<Vector2> rotaDoMapa) {
        this.game = game;
        this.telaJogo = telaJogo;
        this.entidades = entidades;
        this.rotaDoMapa = rotaDoMapa;
    }

    // Atualiza a escolha do jogador
    public void setTorreSelecionada(Torre.Tipo tipo) {
        this.torreSelecionada = tipo;
    }

    public Torre.Tipo getTorreSelecionada() {
        return torreSelecionada;
    }

    // Atualiza onde o mouse está apontando neste exato frame
    public void atualizarMouse(float x, float y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    // A lógica perfeita de colisão usando Retângulos Exatos
    public boolean podeConstruirAqui(float x, float y) {
        // 1. BLOQUEIO DA ÁREA DO MENU (Nada de construir em cima da interface!)
        if (x + 40 > 1050) {
            return false;
        }

        Rectangle rectNovaTorre = new Rectangle(x, y, 40, 40);

        // 2. Colisão com a Estrada (Garante que não pise 1 pixel na rua)
        for (int i = 0; i < rotaDoMapa.size() - 1; i++) {
            Vector2 p1 = rotaDoMapa.get(i);
            Vector2 p2 = rotaDoMapa.get(i + 1);

            float minX = Math.min(p1.x, p2.x) - 20f;
            float minY = Math.min(p1.y, p2.y) - 20f;
            float width = Math.abs(p1.x - p2.x) + 40f;
            float height = Math.abs(p1.y - p2.y) + 40f;

            Rectangle rectEstrada = new Rectangle(minX, minY, width, height);

            if (rectNovaTorre.overlaps(rectEstrada)) {
                return false;
            }
        }

        // 3. Colisão com outras Torres
        for (EntidadeJogo entidade : entidades) {
            if (entidade instanceof Torre) {
                Rectangle rectTorreExistente = new Rectangle(entidade.x, entidade.y, 40, 40);
                if (rectNovaTorre.overlaps(rectTorreExistente)) {
                    return false;
                }
            }
        }
        return true;
    }

    // O método que a TelaJogo vai chamar para desenhar os gráficos de preview
    public void renderizarPreview() {
        // Se o mouse estiver em cima do menu de compras, não desenha preview
        if (mouseX > 1050) return;

        // Subtraímos 20 para o X e Y representarem a quina da torre, 
        // mantendo o mouse exatamente no centro dela.
        float previewX = mouseX - 20f;
        float previewY = mouseY - 20f;

        boolean localValido = podeConstruirAqui(previewX, previewY);

        // Iniciamos o desenho em modo LINHA (apenas contornos)
        game.shape.begin(ShapeRenderer.ShapeType.Line);

        // 1. DESENHA A BORDA DA TORRE (Verde = Pode / Vermelho = Bloqueado)
        if (localValido) {
            game.shape.setColor(Color.GREEN); 
        } else {
            game.shape.setColor(Color.RED); 
        }
        game.shape.rect(previewX, previewY, 40, 40);

        // 2. DESENHA O CÍRCULO DE ALCANCE
        float alcance = Torre.getAlcanceParaPreview(torreSelecionada);
        
        // Se o alcance for 0 (como a Árvore de dinheiro), não desenha o círculo
        if (alcance > 0) {
            // Um cinza claro para diferenciar da borda de construção
            game.shape.setColor(Color.LIGHT_GRAY); 
            
            // O círculo é desenhado a partir do centro (mouseX, mouseY) com o raio do alcance
            game.shape.circle(mouseX, mouseY, alcance);
        }

        game.shape.end();
    }
}