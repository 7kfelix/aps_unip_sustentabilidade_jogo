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
    private boolean menuAberto = true;

    public GerenciadorDeConstrucao(SustentabilidadeGame game, TelaJogo telaJogo, List<EntidadeJogo> entidades, List<Vector2> rotaDoMapa) {
        this.game = game;
        this.telaJogo = telaJogo;
        this.entidades = entidades;
        this.rotaDoMapa = rotaDoMapa;
    }

    public void setTorreSelecionada(Torre.Tipo tipo) { this.torreSelecionada = tipo; }
    public Torre.Tipo getTorreSelecionada() { return torreSelecionada; }
    public void atualizarMouse(float x, float y) { this.mouseX = x; this.mouseY = y; }
    public void setMenuAberto(boolean aberto) { this.menuAberto = aberto; }

    public boolean podeConstruirAqui(float x, float y) {
        int tamanho = Torre.getTamanhoColisao(torreSelecionada); // Pega se é 40 ou 55!

        if (menuAberto && (x + tamanho > 1050)) return false;

        Rectangle rectNovaTorre = new Rectangle(x, y, tamanho, tamanho);

        for (int i = 0; i < rotaDoMapa.size() - 1; i++) {
            Vector2 p1 = rotaDoMapa.get(i);
            Vector2 p2 = rotaDoMapa.get(i + 1);

            float minX = Math.min(p1.x, p2.x) - 20f;
            float minY = Math.min(p1.y, p2.y) - 20f;
            float width = Math.abs(p1.x - p2.x) + 40f;
            float height = Math.abs(p1.y - p2.y) + 40f;

            Rectangle rectEstrada = new Rectangle(minX, minY, width, height);
            if (rectNovaTorre.overlaps(rectEstrada)) return false;
        }

        for (EntidadeJogo entidade : entidades) {
            if (entidade instanceof Torre) {
                Torre t = (Torre) entidade;
                int tamTorre = Torre.getTamanhoColisao(t.getTipo()); // Checa a torre que já tá no chão
                Rectangle rectTorreExistente = new Rectangle(t.x, t.y, tamTorre, tamTorre);
                if (rectNovaTorre.overlaps(rectTorreExistente)) return false;
            }
        }
        return true;
    }

    public void renderizarPreview() {
        if (menuAberto && mouseX > 1050) return;

        int tamanho = Torre.getTamanhoColisao(torreSelecionada);
        // Centraliza o mouse baseado no tamanho exato da torre
        float previewX = mouseX - (tamanho / 2f);
        float previewY = mouseY - (tamanho / 2f);

        boolean localValido = podeConstruirAqui(previewX, previewY);

        game.shape.begin(ShapeRenderer.ShapeType.Line);

        if (localValido) game.shape.setColor(Color.GREEN);
        else game.shape.setColor(Color.RED);

        game.shape.rect(previewX, previewY, tamanho, tamanho);

        float alcance = Torre.getAlcanceParaPreview(torreSelecionada);
        if (alcance > 0) {
            game.shape.setColor(Color.LIGHT_GRAY);
            game.shape.circle(mouseX, mouseY, alcance);
        }

        game.shape.end();
    }
}
