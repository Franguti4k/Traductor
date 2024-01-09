package Traductor_Practica_Final;

public class NumeroReal extends ComponenteLexico{
    private float valor;

    public NumeroReal(float valor) {
        super("float");
        this.valor = valor;
    }

    public float getValor() {
        return this.valor;
    }

    public String toString() {
        //return Float.toString(this.valor);
        //return this.toString();
        return super.toString() + ", " + this.getValor();
    }
}