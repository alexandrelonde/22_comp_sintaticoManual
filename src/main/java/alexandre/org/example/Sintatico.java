package alexandre.org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Sintatico {

    private TabelaSimbolos tabela;

    private int endereco;
    private int contRotulo = 1;


    private List<Registro> ultimasVariaveisDeclaradas = new ArrayList<>();


    private String nomeArquivoSaida;
    private String caminhoArquivoSaida;
    private BufferedWriter bw;
    private FileWriter fw;

    private String rotulo = "";

    private String rotElse;


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
        this.endereco = 0;

        nomeArquivoSaida = "codigofinal.c";
        caminhoArquivoSaida = Paths.get(nomeArquivoSaida).toAbsolutePath().toString();

        bw = null;
        fw = null;

        try {
            fw = new FileWriter(caminhoArquivoSaida, Charset.forName("UTF-8"));
            bw = new BufferedWriter(fw);
            programa();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" Tabela ");
        System.out.println(this.tabela);
    }

    private String criarRotulo(String texto) {
        String retorno = "rotulo" + texto + contRotulo;
        contRotulo++;
        return retorno;
    }

    private void gerarCodigo(String instrucoes) {
        try {
            if (rotulo.isEmpty()) {
                bw.write(instrucoes + "\n");
            } else {
                bw.write(rotulo + ": " +  instrucoes + "\n");
                rotulo = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                Acao1();
                corpo();
                if (token.getClasse() == Classe.cPonto) {
                    LerToken();
                } else {
                    mensagemErro(" - Faltou o encerramento com o ponto!");
                }
                Acao2();
            } else {
                mensagemErro(" - Faltou a identificação do nome do programa!");
            }
        } else {
            mensagemErro(" - Faltou começar com Programa!");
        }
    }

    public void Acao1()
    {
        tabela=new TabelaSimbolos();

        tabela.setTabelaPai(null);

        Registro registro=new Registro();
        registro.setNome(token.getValor().getValorIdentificador());
        registro.setCategoria(Categoria.PROGRAMAPRINCIPAL);

        registro.setNivel(0);
        registro.setOffset(0);
        registro.setTabelaSimbolos(tabela);
        registro.setRotulo("main");
        tabela.inserirRegistro(registro);
        String codigo = "#include <stdio.h>\n" +
                "\nint main(){\n";

        gerarCodigo(codigo);

    }

    public void Acao2()
    {
        Registro registro=new Registro();
        registro.setNome(null);
        registro.setCategoria(Categoria.PROGRAMAPRINCIPAL);
        registro.setNivel(0);
        registro.setOffset(0);
        registro.setTabelaSimbolos(tabela);
        registro.setRotulo("finalCode");
        tabela.inserirRegistro(registro);
        String codigo = "\n}\n";
        gerarCodigo(codigo);
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
            Acao3("int");
            LerToken();
        }else if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("real"))) {
            Acao3("float");
            LerToken();
        }else {
            mensagemErro(" - Faltou a declaração do inteiro - integer!");
        }
    }

    private void Acao3(String type) {
        String codigo= '\t'+type;
        for(int i=0;i<this.ultimasVariaveisDeclaradas.size();i++)
        {
            codigo=codigo+' '+ this.ultimasVariaveisDeclaradas.get(i).getNome();
            if(i == this.ultimasVariaveisDeclaradas.size()-1)
            {
                codigo=codigo + ';';
            }
            else{
                codigo=codigo + ',';
            }
        }
        gerarCodigo(codigo);
    }


    public void Acao4()
    {
        Registro registro=new Registro();
        registro.setNome(token.getValor().getValorIdentificador());
        registro.setCategoria(Categoria.VARIAVEL);
        registro.setNivel(0);
        registro.setOffset(0);
        registro.setTabelaSimbolos(tabela);
        this.endereco++;
        registro.setRotulo("variavel"+this.endereco);
        ultimasVariaveisDeclaradas.add(registro);
        this.tabela.inserirRegistro(registro);
    }

    public void variaveis() {
        if (token.getClasse() == Classe.cId) {
            Acao4();
            LerToken();
            mais_var();
        }else {
            mensagemErro(" - Faltou o identificador!");
        }
    }

    public void mais_var(){
        if (token.getClasse() == Classe.cVirgula) {
            LerToken();
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


    public List<Token> var_read(List<Token> arrayTokensLidos) {
        if (token.getClasse() == Classe.cId) {
            arrayTokensLidos.add(token);
            LerToken();
            //{A5}
            arrayTokensLidos = mais_var_read(arrayTokensLidos);
        }else {
            mensagemErro(" - Faltou o identificador!");
        }
        return arrayTokensLidos;
    }


    public List<Token> mais_var_read(List<Token> arrayTokensLidos) {
        if (token.getClasse() == Classe.cVirgula) {
            LerToken();
            arrayTokensLidos = var_read(arrayTokensLidos);
        }
        return arrayTokensLidos;
    }

    public String var_write(String codigo) {

        if (token.getClasse() == Classe.cId) {
            codigo=codigo+token.getValor().getValorIdentificador();
            LerToken();
            //{A6}
            codigo=mais_var_write(codigo);
        }else {
            mensagemErro(" - Faltou o identificador!");
        }

        return codigo;
    }


    public String mais_var_write(String codigo) {
        if (token.getClasse() == Classe.cVirgula) {
            codigo=codigo+ ',';
            LerToken();
            codigo=var_write(codigo);

        }
        return codigo;
    }



public void comando() {

    if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("read"))){
        String codigo="\tscanf";
        LerToken();
        if (token.getClasse() == Classe.cParEsq) {
            codigo=codigo+"(\"";
            LerToken();
            List<Token> arrayToken = new ArrayList<Token>();
            arrayToken=var_read(arrayToken);
            for(Token i: arrayToken){
                codigo=codigo+"%d ";
            }
            codigo=codigo+"\", ";
            for(Token i: arrayToken){
                if(i == arrayToken.get(arrayToken.size()-1)){
                    codigo=codigo+"&"+i.getValor().getValorIdentificador();
                }else{
                    codigo=codigo+"&"+i.getValor().getValorIdentificador()+", ";
                }
            }
            if (token.getClasse() == Classe.cParDir) {

                codigo=codigo+");";
                gerarCodigo(codigo);
                LerToken();
            }else {
                mensagemErro(" - Faltou o parenteses do lado direito )");
            }
        }else {
            mensagemErro(" - Faltou o parenteses do lado esquerdo (");
        }
    }else
        //write ( <var_write> ) |
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("write"))){
            String referencias="\tprintf";
            String codigo = "";
            LerToken();
            if (token.getClasse() == Classe.cParEsq) {
                referencias = referencias + "(\"";
                LerToken();

                codigo=codigo+var_write("");

                if (codigo.length() >  0) {
                    referencias = referencias + "%d ".repeat(codigo.split(",").length);
                    referencias = referencias + "\", ";
                } else {
                    referencias = referencias + "\"";
                }

                if (token.getClasse() == Classe.cParDir) {
                    codigo=codigo+");";
                    gerarCodigo(referencias + codigo);
                    LerToken();
                }else {
                    mensagemErro(" - Faltou o parenteses do lado direito )");
                }
            }else {
                mensagemErro(" - Faltou o parenteses do lado esquerdo (");
            }
        }else

        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("for"))){
            String codigo="\n\tfor(";
            LerToken();
            if (token.getClasse() == Classe.cId) {
                String identificador = token.getValor().getValorIdentificador();
                codigo=codigo+identificador;
                LerToken();

                if (token.getClasse() == Classe.cAtribuicao){
                    codigo=codigo+"=";
                    LerToken();

                    codigo=codigo+expressao();

                    if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("to"))){
                        codigo=codigo+";";
                        LerToken();
                        codigo=codigo+identificador;
                        codigo=codigo+"<="+expressao()+";";
                        codigo=codigo+identificador + "++)";
                        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("do"))){
                            LerToken();
                            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("begin"))){
                                codigo=codigo+"{";
                                gerarCodigo(codigo);
                                LerToken();
                                sentencas();
                                if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("end"))){
                                    String codigoFinal = "\t}";
                                    gerarCodigo(codigoFinal);
                                    LerToken();
                                    //{A29}
                                }else {
                                    mensagemErro(" - Faltou o end no for! ");
                                }
                            }else {
                                mensagemErro(" - Faltou o begin no for! ");
                            }
                        }else {
                            mensagemErro(" - Faltou o do no for! ");
                        }
                    }else {
                        mensagemErro(" - Faltou o to no for! ");
                    }
                }else {
                    mensagemErro(" - Faltou os dois pontos e o igual no for! ");
                }
            }else {
                mensagemErro(" - Faltou o identificados no for no início do for! ");
            }
        }else

        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("repeat"))){
            String codigo="\n\tdo {\n\t";

            LerToken();
            gerarCodigo(codigo);
            //{A23}
            sentencas();
            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("until"))){

                LerToken();
                if (token.getClasse() == Classe.cParEsq){
                    String codigoFinal="\n\t}while";
                    codigoFinal=codigoFinal+"(";
                    LerToken();

                    codigoFinal=codigoFinal+condicao();

                    if (token.getClasse() == Classe.cParDir){
                        codigoFinal=codigoFinal+");";
                        gerarCodigo(codigoFinal);
                        LerToken();

                        //{A24}
                    }else {
                        mensagemErro(" - Faltou fechar o parenteses no repeat! ");
                    }
                }else {
                    mensagemErro(" - Faltou abrir o parenteses no repeat! ");
                }
            }else {
                mensagemErro(" - Faltou until no repeat! ");
            }
        }

        else if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("while"))){
            String codigo="\n\twhile";
            LerToken();
            //{A20}
            if (token.getClasse() == Classe.cParEsq){
                codigo=codigo+"(";
                LerToken();
                codigo=codigo+condicao();
                if (token.getClasse() == Classe.cParDir){
                    codigo=codigo+")";
                    LerToken();
                    //{A21}
                    if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("do"))){
                        LerToken();
                        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("begin"))){
                            codigo=codigo+"{\n";
                            gerarCodigo(codigo);
                            LerToken();
                            sentencas();
                            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("end"))){
                                codigo="\t}\n";
                                gerarCodigo(codigo);
                                LerToken();
                                //{A22}
                            }else {
                                mensagemErro(" - Faltou end no while! ");
                            }
                        }else {
                            mensagemErro(" - Faltou begin no while! ");
                        }
                    }else {
                        mensagemErro(" - Faltou do no while! ");
                    }
                }else {
                    mensagemErro(" - Faltou o parenteses direito no while! ");
                }
            }else {
                mensagemErro(" - Faltou o parenteses esquerdo no while! ");
            }
        }
        else if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("if"))){
            String codigo="";
            LerToken();
            if (token.getClasse() == Classe.cParEsq){
                codigo=codigo+"\n\tif(";
                LerToken();
                codigo=codigo+condicao();
                if (token.getClasse() == Classe.cParDir){
                    codigo=codigo+")";
                    LerToken();
                    //{A17}
                    if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("then"))){
                        LerToken();
                        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("begin"))){
                            codigo=codigo +" {";
                            gerarCodigo(codigo);
                            LerToken();
                            sentencas();
                            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("end"))){
                                LerToken();

                                String codigoFinal = "";
                                codigoFinal = codigoFinal + "\t}";
                                gerarCodigo(codigoFinal);
                                //{A22}
                                pfalsa();
                                //{A19}
                            }else {
                                mensagemErro(" - Faltou end no while! ");
                            }
                        }else {
                            mensagemErro(" - Faltou begin no while! ");
                        }
                    }else {
                        mensagemErro(" - Faltou do no while! ");
                    }
                }else {
                    mensagemErro(" - Faltou o parenteses direito no while! ");
                }
            }else {
                mensagemErro(" - Faltou o parenteses esquerdo no while! ");
            }
        }
        else if (token.getClasse() == Classe.cId){
            String codigo="\n\t";
            codigo=codigo+token.getValor().getValorIdentificador();
            LerToken();
            //ação 13
            if (token.getClasse() == Classe.cAtribuicao){
                codigo=codigo+"=";
                LerToken();
                codigo=codigo+expressao()+";";
                gerarCodigo(codigo);
                //{A14}
            }
            else {
                mensagemErro("Faltou a atribuição! ");
            }
        }
}



    public String condicao() {
        String expressao1 = expressao();
        String relacao = relacao();
        //{A15}
        String expressao2 = expressao();
        //{A16}

        return expressao1 + relacao + expressao2;
    }


    public void pfalsa() {
        String codigo = "";
        if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("else"))){
            codigo = codigo + "\telse";
            LerToken();
            if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("begin"))){
                codigo = codigo + "{";
                gerarCodigo(codigo);
                LerToken();
                sentencas();
                if ((token.getClasse() == Classe.cPalRes) && (token.getValor().getValorIdentificador().equalsIgnoreCase("end"))){
                    String codigoFinal = "\n\t}";
                    gerarCodigo(codigoFinal);
                    LerToken();
                }else {
                    mensagemErro(" - Faltou finalizar colocando o end! ");
                }
            }else {
                mensagemErro(" - Faltou inicializar com o begin! ");
            }
        }
    }


    public String relacao() {
        String operador="";
        if (token.getClasse() == Classe.cIgual) {
            operador="=";
            LerToken();
        }else if (token.getClasse() == Classe.cMaior) {
            operador=">";
            LerToken();
        }else if (token.getClasse() == Classe.cMenor) {
            operador="<";
            LerToken();
        }else if (token.getClasse() == Classe.cMaiorIgual) {
            operador = ">=";
            LerToken();
        }else if (token.getClasse() == Classe.cMenorIgual) {
            operador = "<=";
            LerToken();
        }else if (token.getClasse() == Classe.cDiferente) {
            operador = "!=";
            LerToken();
        }else {
            mensagemErro(" - Faltou colocar o operador de relação! ");
        }

        return operador;
    }


    public String expressao() {
        String termo = termo();
        String outrosTermos = outros_termos();

        return termo + outrosTermos;
    }

    public String outros_termos() {
        String op = "";
        String termo= "";
        String outrosTermos = "";

        if (token.getClasse() == Classe.cMais || token.getClasse() == Classe.cMenos) {
            op = op_ad();
            termo = termo();
            outrosTermos = outros_termos();
        }

        return op + termo + outrosTermos;
    }

    public String op_ad() {
        String op = "";
        if (token.getClasse() == Classe.cMais) {
            op = "+";
            LerToken();
        } else if (token.getClasse() == Classe.cMenos) {
            op = "-";
            LerToken();
        }else {
            mensagemErro(" - Faltou colocar o operador de adição ou de subtração! ");
        }
        return op;
    }


    public String termo() {
        String fator = fator();
        String maisFatores = mais_fatores();

        return fator + maisFatores;
    }

    public String mais_fatores() {
        if (token.getClasse() == Classe.cMultiplicacao || token.getClasse() == Classe.cDivisao) {
            String op = op_mul();

            String fator = fator();

            String outrosFatores = mais_fatores();

            return op + fator + outrosFatores;
        }

        return "";
    }


    public String op_mul() {
        String op = "";
        if (token.getClasse() == Classe.cMultiplicacao) {
            op = "*";
            LerToken();
        } else if (token.getClasse() == Classe.cDivisao) {
            op = "/";
            LerToken();
        } else {
            mensagemErro(" - Faltou colocar o operador de multiplicação ou divisão! ");
        }
        return op;
    }

    public String fator() {
        String returnFator = "";
        if (token.getClasse() == Classe.cId) {
            returnFator = token.getValor().getValorIdentificador();

            LerToken();
            //{A7}
        } else if (token.getClasse() == Classe.cInt) {
            returnFator = String.valueOf(token.getValor().getValorInteiro());
            LerToken();
            //{A8}
        } else if (token.getClasse() == Classe.cReal) {
            returnFator = String.valueOf(token.getValor().getValorDecimal());
            LerToken();
        }else if (token.getClasse() == Classe.cParEsq){
            returnFator="(";
            LerToken();
            returnFator = returnFator + expressao();
            if (token.getClasse() == Classe.cParDir){
                returnFator=returnFator + ")";
                LerToken();
            }else {
                mensagemErro(" - Faltou colocar o parenteses do lado direito!");
            }
        }else {
            mensagemErro(" - Faltou fator in num exp! ");
        }

        return returnFator;
    }

}
