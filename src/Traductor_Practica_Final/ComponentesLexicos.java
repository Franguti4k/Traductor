package Traductor_Practica_Final;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ComponentesLexicos {

    private Hashtable<String, String> componentesLexicos;

    public ComponentesLexicos(String ficheroComponentesLexicos){
        this.componentesLexicos = new Hashtable<String, String>();
        leeComponentesLexicos(this.componentesLexicos, ficheroComponentesLexicos);
    }

    public String getEtiqueta(String lexema) {
        return this.componentesLexicos.get(lexema);
    }

    public String getLexema(String etiquetaLexica) {
        String lexema = null;
        Set<Map.Entry<String, String>> s = this.componentesLexicos.entrySet();
        for(Map.Entry<String, String> m : s)
            if (m.getValue().equals(etiquetaLexica)) {
                lexema = m.getKey();
                break;
            }

        return lexema;
    }

    private static boolean existeFichero(String fichero) {
        File ficheroEntrada = new File (fichero);

        return ficheroEntrada.exists();
    }

    private static String etiqueta(String s) {
        return s.substring(0, s.indexOf("\t")).trim();
    }

    private static String lexema(String s) {
        return s.substring(s.lastIndexOf("\t") + 1).trim();
    }

    private static void leeComponentesLexicos(Hashtable<String, String> componentesLexicos, String ficheroComponentesLexicos) {
        if (existeFichero(ficheroComponentesLexicos)) {
            try {
                Scanner fichero = new Scanner(new File(ficheroComponentesLexicos), "UTF-8");
                String componenteLexico, lexema, etiquetaLexica;

                while (fichero.hasNext()) {
                    componenteLexico = fichero.nextLine();

                    if (componenteLexico.length() > 0 && componenteLexico.charAt(0) != '/') {
                        lexema = lexema(componenteLexico);
                        etiquetaLexica = etiqueta(componenteLexico);

                        componentesLexicos.put(lexema, etiquetaLexica);
                    }
                }

                fichero.close();

            } catch (IOException e) {}
        }
    }
}