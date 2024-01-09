package Traductor_Practica_Final;

public abstract class TipoDato {
    private String tipo;

    public TipoDato(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return this.tipo;
    }

    public String toString() {
        return this.tipo;
    }
}


