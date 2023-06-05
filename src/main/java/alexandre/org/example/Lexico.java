package alexandre.org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Lexico {
    private String caminhoArquivo;
    private String nomeArquivo;
    private int c;
    PushbackReader br;
    BufferedReader initialBr;
    private ArrayList<String> reservedWords = new ArrayList<String>(Arrays.asList(
            "and", "array", "begin", "case", "const", "div",
            "do", "downto", "else", "end", "file", "for",
            "function", "goto", "if", "in", "label", "mod",
            "nil", "not", "of", "or", "packed", "procedure",
            "program", "record", "repeat", "set", "then",
            "to", "type", "until", "var", "while", "with",
             "read", "write", "real", "integer"
    ));

    public Lexico(String nomeArquivo) {
        this.caminhoArquivo = Paths.get(nomeArquivo).toAbsolutePath().toString();
        this.nomeArquivo = nomeArquivo;

        try {
            this.initialBr = new BufferedReader(new FileReader(caminhoArquivo, StandardCharsets.UTF_8));
            this.br = new PushbackReader(initialBr);
            this.c = this.br.read();
        } catch (IOException err) {
            System.err.println("Não foi possível abrir o arquivo ou ler do arquivo: " + this.nomeArquivo);
            err.printStackTrace();
        }
    }

    public Token getToken(int linha, int coluna) {
        Token token = new Token();
        int qtdEspacos = 0;
        int tamanhoToken = 0;
        char caractere;

        try {
            while (c != -1) {
                caractere = (char) c;

                if (!(c == 13 || c == 10)) {
                    if (caractere == ' ') {
                        qtdEspacos = contarEspacos(caractere);
                    } else if (Character.isLetter(caractere)) {
                        token = tratarLetras(caractere, linha, coluna, qtdEspacos, tamanhoToken);
                        return token;
                    } else if (Character.isDigit(caractere)) {
                        token = tratarNumeros(caractere, linha, coluna, qtdEspacos, tamanhoToken);
                        return token;
                    } else {
                        token = tratarSimbolos(caractere, linha, coluna, qtdEspacos, tamanhoToken);
                        return token;
                    }
                } else {
                    int[] posicoesAtualizadas = atualizarPosicao(linha, qtdEspacos, tamanhoToken, coluna);
                    linha = posicoesAtualizadas[0];
                    qtdEspacos = posicoesAtualizadas[1];
                    tamanhoToken = posicoesAtualizadas[2];
                    coluna = posicoesAtualizadas[3];
                }
            }

            token.setClasse(Classe.cEOF);
            return token;
        } catch (IOException err) {
            System.err.println("Não foi possível abrir o arquivo ou ler do arquivo: " + this.nomeArquivo);
            err.printStackTrace();
        }
        return null;
    }
    private int[] atualizarPosicao(int linha, int qtdEspacos, int tamanhoToken, int coluna) throws IOException {
        c = this.br.read();
        linha++;
        qtdEspacos = 0;
        tamanhoToken = 0;
        coluna = 1;

        return new int[]{linha, qtdEspacos, tamanhoToken, coluna};
    }
    private int contarEspacos(char caractere) throws IOException {
        int qtdEspacos = 0;
        while (caractere == ' ') {
            c = this.br.read();
            qtdEspacos++;
            caractere = (char) c;
        }
        return qtdEspacos;
    }

    private Token tratarLetras(char caractere, int linha, int coluna, int qtdEspacos, int tamanhoToken) throws IOException {
        StringBuilder lexema = new StringBuilder("");
        Token token = new Token();

        while (Character.isLetter(caractere) || Character.isDigit(caractere)) {
            lexema.append(caractere);
            c = this.br.read();
            tamanhoToken++;
            caractere = (char) c;
        }

        if (returnIfIsReservedWord(lexema.toString())) {
            token.setClasse(Classe.cPalRes);
        } else {
            token.setClasse(Classe.cId);
        }
        token.setTamanhoToken(tamanhoToken);
        token.setColuna(coluna + qtdEspacos);
        token.setLinha(linha);
        Valor valor = new Valor(lexema.toString());
        token.setValor(valor);

        return token;
    }

    private Token tratarSimbolos(char caractere, int linha, int coluna, int qtdEspacos, int tamanhoToken) throws IOException {
        Token token = new Token();
        tamanhoToken++;

        switch (caractere) {
            case ':':
                int proximo = this.br.read();
                caractere = (char) proximo;
                if (caractere == '=') {
                    tamanhoToken++;
                    token.setClasse(Classe.cAtribuicao);
                } else {
                    this.br.unread(proximo);
                    token.setClasse(Classe.cDoisPontos);
                }
                break;
            case '+':
                token.setClasse(Classe.cMais);
                break;
            case '-':
                token.setClasse(Classe.cMenos);
                break;
            case '/':
                token.setClasse(Classe.cDivisao);
                break;
            case '*':
                token.setClasse(Classe.cMultiplicacao);
                break;
            case '>':
                proximo = this.br.read();
                caractere = (char) proximo;
                if (caractere == '=') {
                    tamanhoToken++;
                    token.setClasse(Classe.cMaiorIgual);
                } else {
                    this.br.unread(proximo);
                    token.setClasse(Classe.cMaior);
                }
                break;
            case '<':
                proximo = this.br.read();
                caractere = (char) proximo;
                if (caractere == '=') {
                    tamanhoToken++;
                    token.setClasse(Classe.cMenorIgual);
                } else if (caractere == '>') {
                    tamanhoToken++;
                    token.setClasse(Classe.cDiferente);
                } else {
                    this.br.unread(proximo);
                    token.setClasse(Classe.cMenor);
                }
                break;
            case '=':
                token.setClasse(Classe.cIgual);
                break;
            case ',':
                token.setClasse(Classe.cVirgula);
                break;
            case ';':
                token.setClasse(Classe.cPontoVirgula);
                break;
            case '.':
                token.setClasse(Classe.cPonto);
                break;
            case '(':
                token.setClasse(Classe.cParEsq);
                break;
            case ')':
                token.setClasse(Classe.cParDir);
                break;
            default:
                token.setClasse(Classe.cEOF);
                break;
        }

        token.setTamanhoToken(tamanhoToken);
        token.setColuna(coluna + qtdEspacos);
        token.setLinha(linha);
        token.setValor(null);
        c = this.br.read();
        tamanhoToken++;

        return token;
    }

    private Token tratarNumeros(char caractere, int linha, int coluna, int qtdEspacos, int tamanhoToken) throws IOException {
        StringBuilder lexema = new StringBuilder("");
        Token token = new Token();
        int numberOfPoints = 0;

        while (Character.isDigit(caractere) || caractere == '.') {
            if (caractere == '.') {
                numberOfPoints++;
            }
            lexema.append(caractere);
            c = this.br.read();
            tamanhoToken++;
            caractere = (char) c;
        }

        if (numberOfPoints <= 1) {
            if (numberOfPoints == 0) {
                token.setClasse(Classe.cInt);
                Valor valor = new Valor(Integer.parseInt(lexema.toString()));
                token.setValor(valor);
            } else {
                token.setClasse(Classe.cReal);
                Valor valor = new Valor(Float.parseFloat(lexema.toString()));
                token.setValor(valor);
            }

            token.setTamanhoToken(tamanhoToken);
            token.setColuna(coluna + qtdEspacos);
            token.setLinha(linha);
            return token;
        }
        return null;
    }

    boolean returnIfIsReservedWord(String word){
        return this.reservedWords.contains(word.toLowerCase());
    }
}