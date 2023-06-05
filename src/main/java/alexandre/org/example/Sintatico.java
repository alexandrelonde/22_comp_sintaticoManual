package alexandre.org.example;

public class Sintatico {

    private Lexico lexico;

    private Token token;

    private int linha;

    private int coluna;


    public void LerToken(){
        token = lexico.getToken(linha, coluna);
        coluna = token.getColuna()+token.getTamanhoToken();
        linha = token.getLinha();
        System.out.println(token);
    }

    public Sintatico(String nomedoArquivo){
        linha=1;
        coluna=1;
        this.lexico=new Lexico(nomedoArquivo);
    }

    public void Analisar(){
        LerToken();
        programa();
    }


    public void mensagemErro(String msg) {
        System.err.println("Linha: " + token.getLinha() +
                ", Coluna: " + token.getColuna() +
                msg);
    }


    public void programa() {
        if ((token.getClasse() == Classe.cPalRes)
                && (token.getValor().getValorIdentificador().equalsIgnoreCase("program"))) {
            LerToken();
            if (token.getClasse() == Classe.cId) {
                LerToken();
                corpo();
                if (token.getClasse() == Classe.cPonto) {
                    LerToken();
                } else {
                    mensagemErro(" - Faltou o encerramento com o ponto!");
                }
            } else {
                mensagemErro(" - Faltou a identificação do nome do programa!");
            }
        } else {
            mensagemErro(" - Faltou começar com Programa!");
        }
    }

    public void corpo() {
        declara();
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("begin"))) {
            LerToken();
            sentencas();
            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("end"))) {
                LerToken();
            }else {
                mensagemErro(" - Faltou a finalização com end!");
            }
        }else {
            mensagemErro(" - Faltou o begin no corpo do programa!");
        }
    }

    public void declara() {
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("var"))) {
            LerToken();
            dvar();
            mais_dc();
        }
    }

    public void mais_dc() {
        if (token.getClasse() == Classe.cPontoVirgula) {
            LerToken();
            cont_dc();
        } else {
            mensagemErro(" - Faltou colocar o ponto e vírgula!");
        }
    }


    public void cont_dc() {
        if (token.getClasse() == Classe.cId) {
            dvar();
            mais_dc();
        }
    }

    public void dvar() {
        variaveis();
        if (token.getClasse() == Classe.cDoisPontos) {
            LerToken();
            tipo_var();
        }else {
            mensagemErro(" - Faltou os dois pontos!");
        }
    }

    public void tipo_var() {
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("integer"))) {
            LerToken();
        }else if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("real"))) {
            LerToken();
        }else {
            mensagemErro(" - Faltou a declaração do inteiro - integer!");
        }
    }

    public void variaveis() {
        if (token.getClasse() == Classe.cId) {
            LerToken();
            mais_var();
        }else {
            mensagemErro(" - Faltou o identificador!");
        }
    }

    public void mais_var(){
        if (token.getClasse() == Classe.cVirgula) {
            LerToken();
            //{A2}
            variaveis();
        }
    }


    public void sentencas() {
        comando();
        mais_sentencas();
    }


    public void mais_sentencas() {
        if (token.getClasse() == Classe.cPontoVirgula) {
            LerToken();
            cont_sentencas();
        }else {
            mensagemErro(" - Faltou o ponto e vírgula!");
        }
    }



    public void cont_sentencas() {
        if (((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("read"))) ||
                ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("write"))) ||
                ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("for"))) ||
                ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("repeat"))) ||
                ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("while"))) ||
                ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("if"))) ||
                ((token.getClasse() == Classe.cId))
        ) {
            sentencas();
        }
    }


    public void var_read() {
        if (token.getClasse() == Classe.cId) {
            LerToken();
            //{A5}
            mais_var_read();
        }else {
            mensagemErro(" - Faltou o identificador!");
        }
    }


    public void mais_var_read() {
        if (token.getClasse() == Classe.cVirgula) {
            LerToken();
            var_read();
        }
    }



    public void var_write() {
        if (token.getClasse() == Classe.cId) {
            LerToken();
            //{A6}
            mais_var_write();
        }else {
            mensagemErro(" - Faltou o identificador!");
        }
    }


    public void mais_var_write() {
        if (token.getClasse() == Classe.cVirgula) {
            LerToken();
            var_write();
        }
    }


    public void comando() {

        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("read"))){
            LerToken();
            if (token.getClasse() == Classe.cParEsq) {
                LerToken();
                var_read();
                if (token.getClasse() == Classe.cParDir) {
                    LerToken();
                }else {
                    mensagemErro(" - Faltou o parenteses do lado direito )");
                }
            }else {
                mensagemErro(" - Faltou o parenteses do lado esquerdo ( ");
            }
        }else
            //write ( <var_write> ) |
            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("write"))){
                LerToken();
                if (token.getClasse() == Classe.cParEsq) {
                    LerToken();
                    var_write();
                    if (token.getClasse() == Classe.cParDir) {
                        LerToken();
                    }else {
                        mensagemErro(" - Faltou o parenteses do lado direito ) ");
                    }
                }else {
                    mensagemErro(" - Faltou o parenteses do lado esquerdo ( ");
                }
            }else

            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("for"))){
                LerToken();
                if (token.getClasse() == Classe.cId) {
                    LerToken();

                    if (token.getClasse() == Classe.cAtribuicao){
                        LerToken();
                        expressao();
                        //{A26}
                        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("to"))){
                            LerToken();
                            //{A27}
                            expressao();
                            //{A28}
                            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("do"))){
                                LerToken();
                                if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("begin"))){
                                    LerToken();
                                    sentencas();
                                    if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("end"))){
                                        LerToken();
                                        //{A29}
                                    }else {
                                        mensagemErro(" - Faltou o end no for!");
                                    }
                                }else {
                                    mensagemErro(" - Faltou o begin no for!");
                                }
                            }else {
                                mensagemErro(" - Faltou colocar do no for!");
                            }
                        }else {
                            mensagemErro(" - Faltou colocar to no for!");
                        }
                    }else {
                        mensagemErro(" - Faltou colocar os dois pontos e igual no for!");
                    }
                }else {
                    mensagemErro(" - Faltou o identificador no for no inicio do for!");
                }
            }else

            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("repeat"))){
                LerToken();
                //{A23}
                sentencas();
                if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("until"))){
                    LerToken();
                    if (token.getClasse() == Classe.cParEsq){
                        LerToken();
                        condicao();
                        if (token.getClasse() == Classe.cParDir){
                            LerToken();
                            //{A24}
                        }else {
                            mensagemErro(" - Faltou fechar parenteses no repeat!");
                        }
                    }else {
                        mensagemErro(" - Faltou abrir parenteses no repeat!");
                    }
                }else {
                    mensagemErro(" - Faltou colocar until no repeat!");
                }
            }

            else if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("while"))){
                LerToken();
                //{A20}
                if (token.getClasse() == Classe.cParEsq){
                    LerToken();
                    condicao();
                    if (token.getClasse() == Classe.cParDir){
                        LerToken();
                        //{A21}
                        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("do"))){
                            LerToken();
                            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("begin"))){
                                LerToken();
                                sentencas();
                                if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("end"))){
                                    LerToken();
                                    //{A22}
                                }else {
                                    mensagemErro(" - Faltou colocar end no while!");
                                }
                            }else {
                                mensagemErro(" - Faltou colocar begin no while!");
                            }
                        }else {
                            mensagemErro(" - Faltou colocar do no while!");
                        }
                    }else {
                        mensagemErro(" - Fatou o parenteses direito no while!");
                    }
                }else {
                    mensagemErro(" - Faltou o parenteses esquerdo do while!");
                }
            }
            else if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("if"))){
                LerToken();
                if (token.getClasse() == Classe.cParEsq){
                    LerToken();
                    condicao();
                    if (token.getClasse() == Classe.cParDir){
                        LerToken();
                        //{A17}
                        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("then"))){
                            LerToken();
                            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("begin"))){
                                LerToken();
                                sentencas();
                                if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("end"))){
                                    LerToken();
                                    //{A22}
                                    pfalsa();
                                    //{A19}
                                }else {
                                    mensagemErro(" - Faltou colocar end no while!");
                                }
                            }else {
                                mensagemErro(" - Falou colocar begin no while!");
                            }
                        }else {
                            mensagemErro(" - Faltou colocar do no while!");
                        }
                    }else {
                        mensagemErro(" - Faltou colocar parenses direito no while!");
                    }
                }else {
                    mensagemErro(" - Faltou colocar parenses esquerdo no while!");
                }
            }
            else if (token.getClasse() == Classe.cId){
                LerToken();
                //ação 13
                if (token.getClasse() == Classe.cAtribuicao){
                    LerToken();
                    expressao();
                    //{A14}
                }
                else {
                    mensagemErro(" - Faltou a atribuição!");
                }
            }
    }

    public void condicao() {
        expressao();
        relacao();
        //{A15}
        expressao();
        //{A16}
    }


    public void pfalsa() {
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("else"))){
            LerToken();
            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("begin"))){
                LerToken();
                sentencas();
                if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("end"))){
                    LerToken();
                }else {
                    mensagemErro(" - Faltou finalizar colocando o end!");
                }
            }else {
                mensagemErro(" - Faltou inicializar com o begin!");
            }
        }
    }

    public void relacao() {
        if (token.getClasse() == Classe.cIgual) {
            LerToken();
        }else if (token.getClasse() == Classe.cMaior) {
            LerToken();
        }else if (token.getClasse() == Classe.cMenor) {
            LerToken();
        }else if (token.getClasse() == Classe.cMaiorIgual) {
            LerToken();
        }else if (token.getClasse() == Classe.cMenorIgual) {
            LerToken();
        }else if (token.getClasse() == Classe.cDiferente) {
            LerToken();
        }else {
            mensagemErro(" - Faltou colocar o operador de relação!");
        }
    }

    public void expressao() {
        termo();
        outros_termos();
    }

    public void outros_termos() {
        if (token.getClasse() == Classe.cMais || token.getClasse() == Classe.cMenos) {
            op_ad();
            termo();
            outros_termos();
        }
    }

    public void op_ad() {
        if (token.getClasse() == Classe.cMais || token.getClasse() == Classe.cMenos) {
            LerToken();
        }else {
            mensagemErro(" - Faltou colocar o operador de adição ou de subtração!");
        }
    }

    public void termo() {
        fator();
        mais_fatores();
    }


    public void mais_fatores() {
        if (token.getClasse() == Classe.cMultiplicacao || token.getClasse() == Classe.cDivisao) {
            op_mul();
            //{A11}
            fator();
            //{A12}
            mais_fatores();
        }
    }

    public void op_mul() {
        if (token.getClasse() == Classe.cMultiplicacao || token.getClasse() == Classe.cDivisao) {
            LerToken();
        }else {
            mensagemErro(" - Faltou colocar o operador de multiplicação ou divisão!");
        }
    }


    public void fator() {
        if (token.getClasse() == Classe.cId) {
            LerToken();
            //{A7}
        }else if (token.getClasse() == Classe.cInt || token.getClasse() == Classe.cReal) {
            LerToken();
            //{A8}
        }else if (token.getClasse() == Classe.cParEsq){
            LerToken();
            expressao();
            if (token.getClasse() == Classe.cParDir){
                LerToken();
            }else {
                mensagemErro(" - Faltou colocar o parenteses do lado direito!");
            }
        }else {
            mensagemErro(" - Faltou fator in num exp!");
        }
    }





}
