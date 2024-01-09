package Traductor_Practica_Final;

public class Identificador extends ComponenteLexico{
    private String lexema;

    public Identificador(String lexema) {
        //super(etiqueta);
        super("id");
        this.lexema = lexema;
    }

    public String getLexema() {
        return this.lexema;
    }

    public String toString() {
        return super.toString() + ", " + this.lexema;
    }
}