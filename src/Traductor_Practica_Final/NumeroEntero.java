package Traductor_Practica_Final;

public class NumeroEntero extends ComponenteLexico{
    private int valor;

    public NumeroEntero(int valor) {
        super("int");
        this.valor = valor;
    }

    public int getValor() {
        return this.valor;
    }

    public String toString() {
        //return Integer.toString(this.valor);
        //return this.toString();
        return super.toString() + ", " + this.getValor();
    }
}