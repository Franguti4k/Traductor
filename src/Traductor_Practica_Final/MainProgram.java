package Traductor_Practica_Final;

import java.nio.charset.StandardCharsets;

public class MainProgram {

    public static void main(String [] args) {
        String fileName="programa12.txt";
        Lexico Lexico= new Lexico(fileName,StandardCharsets.UTF_8);

        Analizador compiler= new Analizador(new Lexico(fileName,StandardCharsets.UTF_8));

        System.out.println("Programa 12: \n ");
        System.out.println(Lexico.getPrograma()+"\n");

        compiler.programa();

        System.out.println(compiler.errores());


        for(int i=0;i<19;i++) {System.out.print("_");}
        System.out.println("\n|Tabla de simbolos|");
        for(int i=0;i<26;i++) {System.out.print("-");}
        System.out.println(compiler.tablaSimbolos());
        for(int i=0;i<26;i++) {System.out.print("-");}



    }
}