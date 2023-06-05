package alexandre.org.example;

public class Token {
    private Classe classe;
    private Valor valor;
    private int linha;
    private int coluna;
    private int tamanhoToken;

    public Token(Classe classe) {
        this.classe = classe;
    }

    public Token(Valor valor) {
        this.valor = valor;
    }

    public Token(int linha) {
        this.linha = linha;
    }

    public Token() {
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public Valor getValor() {
        return valor;
    }

    public void setValor(Valor valor) {
        this.valor = valor;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

    public int getTamanhoToken() {
        return tamanhoToken;
    }

    public void setTamanhoToken(int tamanhoToken) {
        this.tamanhoToken = tamanhoToken;
    }

    @Override
    public String toString() {
        return "Token{" +
                "classe=" + classe +
                ", valor=" + valor +
                ", linha=" + linha +
                ", coluna=" + coluna +
                ", tamanhoToken=" + tamanhoToken +
                '}';
    }
}
