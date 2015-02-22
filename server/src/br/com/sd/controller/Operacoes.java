package br.com.sd.controller;

/**
 * Created by diego on 08/09/2014.
 */
public class Operacoes {

    public static int Soma(int x, int y) {
        return x + y;
    }

    public static int Subtracao(int x, int y) {
        return x-y;
    }

    public static int Multiplic(int x, int y) {
        return x*y;
    }

    public static int Divisao(int x, int y) {
        if (y!=0) {
            return x / y;
        } else {
            return 0;
        }
    }
}