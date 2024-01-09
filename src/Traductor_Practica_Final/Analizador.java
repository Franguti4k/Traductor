package Traductor_Practica_Final;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
public class Analizador {
    private String comparacionTipo=null;
    private ComponenteLexico componenteLexico;
    private Lexico lexico;
    private Hashtable<String, TipoDato> simbolos;
    private Vector<String> errores;

    public Analizador(Lexico lexico) {
        this.simbolos = new Hashtable<String, TipoDato>();
        this.lexico = lexico;
        this.componenteLexico = this.lexico.getComponenteLexico();
        this.errores = new Vector<String>();
    }

    public String tablaSimbolos() {
        String simbolos = "";

        Set<Map.Entry<String, TipoDato>> s = this.simbolos.entrySet();

        for(Map.Entry<String, TipoDato> m : s) {
            simbolos = simbolos + "\n| <'" + m.getKey() + "', " + m.getValue().toString() + "> |" ;
        }

        return simbolos;

    }


    //Programa
    public void programa() {
        compara("void");
        compara("main");
        compara("open_bracket");

        declaraciones();
        instrucciones();

        compara("closed_bracket");
        System.out.print(" halt " + "\n" + "\n");

    }

    //Declaraciones
    private void declaraciones() {
        String etiqueta = this.componenteLexico.getEtiqueta();

        if(etiqueta.equals("int") || etiqueta.equals("float") || etiqueta.equals("boolean")) {
            declaracion_variable();
            declaraciones();
        }

    }


    //Declaracion-variable
    private void declaracion_variable() {
        String tipo = tipo_primitivo();
        int tamaño = 1;

        if (this.componenteLexico.getEtiqueta().equals("open_square_bracket")) {
            compara("open_square_bracket");
            if (this.componenteLexico.getEtiqueta().equals("int")) {
                NumeroEntero numero = (NumeroEntero) this.componenteLexico;
                tamaño = numero.getValor();
            }
            compara("int");
            compara("closed_square_bracket");

            Identificador id = (Identificador) this.componenteLexico;
            this.simbolos.put(id.getLexema(), new TipoArray(tipo, tamaño));
            System.out.print(" lvalue " + id.getLexema() + "\n");
            compara("id");
            compara("semicolon");
        } else {
            this.comparacionTipo = tipo;
            lista_identificadores(tipo);
            compara("semicolon");
            this.comparacionTipo = null;
        }
    }



    //Tipo-primitivo
    private String tipo_primitivo() {
        String tipo = this.componenteLexico.getEtiqueta();

        switch (tipo) {
            case "int":
                compara("int");
                break;
            case "float":
                compara("float");
                break;
            case "boolean":
                compara("boolean");
                break;
            default:
                System.out.println("Error, se esperaba un tipo de dato");
        }
        return tipo;
    }



    //Lista-identificadores
    private void lista_identificadores(String tipo) {
        if(this.componenteLexico.getEtiqueta().equals("id")) {

            Identificador id = (Identificador) this.componenteLexico;
            if(this.simbolos.get(id.getLexema()) != null) {
                this.errores.add("Error en la linea "+this.lexico.getLineas()+", variable '"+
                        id.getLexema() +"' ya esta declarada de antes");
            }else {
                simbolos.put(id.getLexema(), new TipoPrimitivo(tipo));
            }

            System.out.print(" lvalue " + id.getLexema() + "\n");
            compara("id");
            asignacion();
            mas_identificadores(tipo);

        }
    }

    //Más-identificadores
    private void mas_identificadores(String tipo) {

        if(this.componenteLexico.getEtiqueta().equals("comma")) {
            compara("comma"); // ,
            Identificador id = (Identificador) this.componenteLexico;
            this.simbolos.put(id.getLexema(), new TipoPrimitivo(tipo));
            System.out.print(" lvalue " + id.getLexema() + "\n");
            compara("id");
            asignacion();
            mas_identificadores(tipo);


        }
    }


    //Asignacion
    private void asignacion() {
        if(this.componenteLexico.getEtiqueta().equals("assignment")) {
            compara("assignment");
            expresion_logica();
            System.out.println(" = " + "\n");
        }

    }



    //Instrucciones
    private void instrucciones() {
        String etiqueta = this.componenteLexico.getEtiqueta();
        if(etiqueta.equals("int")||etiqueta.equals("float")||etiqueta.equals("boolean") ||etiqueta.equals("id")||etiqueta.equals("if")||etiqueta.equals("while")||etiqueta.equals("do")||etiqueta.equals("print")||etiqueta.equals("open_bracket")) {
            instruccion();
            instrucciones();
        }


    }


    //Instruccion

    private void instruccion() {
        String etiqueta = this.componenteLexico.getEtiqueta();
        switch (etiqueta) {
            case "int":
            case "float":
            case "boolean":
                declaracion_variable();
                break;
            case "id":
                Identificador id = (Identificador) this.componenteLexico;
                variable();
                compara("assignment");

                if (this.simbolos.get(id.getLexema()) == null) {
                    this.comparacionTipo = null;
                } else {
                    this.comparacionTipo = this.simbolos.get(id.getLexema()).getTipo();
                }
                this.comparacionTipo = null;
                expresion_logica();
                compara("semicolon");
                System.out.print(" = " + "\n");
                break;
            case "if":
                compara("if");
                compara("open_parenthesis");
                expresion_logica();
                compara("closed_parenthesis");
                String out = this.componenteLexico.getEtiqueta();
                System.out.print(" gofalse " + out + "\n");
                instruccion();

                if (this.componenteLexico.getEtiqueta().equals("else")) {
                    System.out.print(" goto " + out + "\n");
                    compara("else");
                    String els = this.componenteLexico.getEtiqueta();
                    instruccion();
                    System.out.print(" label " + els + "\n");
                } else {
                    System.out.print(" label " + out + "\n");
                }
                break;
            case "while":
                compara("while");
                String test = this.componenteLexico.getEtiqueta();
                System.out.print(" label " + test + "\n");
                compara("open_parenthesis");
                expresion_logica();
                compara("closed_parenthesis");
                String out1 = this.componenteLexico.getEtiqueta();
                System.out.print(" gofalse " + out1 + "\n");
                if (this.componenteLexico.getEtiqueta().equals("semicolon")) {
                    compara("semicolon");
                } else {
                    instruccion();
                    System.out.print(" goto " + test + "\n");
                    System.out.print(" label " + out1 + "\n");
                }
                break;
            case "do":
                compara("do");
                String test1 = this.componenteLexico.getEtiqueta();
                System.out.print(" label " + test1 + "\n");
                instruccion();
                compara("while");
                String out2 = this.componenteLexico.getEtiqueta();
                System.out.print(" gofalsse " + out2 + "\n");
                System.out.print(" goto " + test1 + "\n");
                System.out.print(" label " + out2 + "\n");
                break;
            case "print":
                compara("print");
                compara("open_parenthesis");
                variable();
                compara("closed_parenthesis");
                compara("semicolon");
                break;
            case "open_bracket":
                compara("open_bracket");
                instrucciones();
                compara("closed_bracket");
                break;
            default:
                break;
        }
    }






    //Variable

    private void variable() {
        if (this.componenteLexico.getEtiqueta().equals("id")) {
            Identificador id = (Identificador) this.componenteLexico;
            TipoDato simbolo = this.simbolos.get(id.getLexema());

            if (simbolo == null) {
                this.errores.add("Error en la linea " + this.lexico.getLineas() + ", variable '" + id.getLexema() + "' no ha sido declarada");
            } else {
                System.out.print(" rvalue " + id.getLexema() + "\n");
                compara("id");
                if (this.componenteLexico.getEtiqueta().equals("open_square_bracket")) {
                    compara("open_square_bracket");
                    expresion();
                    compara("closed_square_bracket");
                }
                if (this.comparacionTipo != null && !simbolo.getTipo().equals(this.comparacionTipo.toString())) {
                    this.errores.add("Error en la linea " + this.lexico.getLineas() + ", incompatibilidad de tipos en la instrucción de asignación");
                }
            }
        }
    }





    //Expresion-logico
    private void expresion_logica(){
        termino_logico();
        masexpresion_logica();
    }
    private void masexpresion_logica(){
        if (this.componenteLexico.getEtiqueta().equals("or")) {
            compara("or");
            termino_logico();
            System.out.print(" || "  + "\n");
            masexpresion_logica();


        }else {
            termino_logico();
        }
    }


    //Termino-logico
    private void termino_logico() {
        factor_logico();
        mastermino_logico();
    }
    private void mastermino_logico() {
        if (this.componenteLexico.getEtiqueta().equals("and")) {
            compara("and");
            factor_logico();
            System.out.print(" && "  + "\n");
            mastermino_logico();
        }else {
            factor_logico();
        }
    }


    //Factor-logico

    private void factor_logico() { //factor_logico -> expresion comparacion expresion | not factor_logico | open_parenthesis expresion_logica closed_parenthesis
        String etiqueta = this.componenteLexico.getEtiqueta();
        switch (etiqueta) {
            case "not":
                compara("not");
                factor_logico();
                System.out.print(" ! " + "\n");
                break;
            case "true":
            case "false":
                compara(etiqueta);
                break;
            default:
                expresion_relacional();
                break;
        }
    }


    //Expresion-relacional
    private void expresion_relacional() { //expresion_relacional -> expresion comparacion expresion
        expresion();
        if(
                this.componenteLexico.getEtiqueta().equals("greater_than") ||
                        this.componenteLexico.getEtiqueta().equals("greater_equals") ||
                        this.componenteLexico.getEtiqueta().equals("less_than") ||
                        this.componenteLexico.getEtiqueta().equals("less_equals") ||
                        this.componenteLexico.getEtiqueta().equals("equals") ||
                        this.componenteLexico.getEtiqueta().equals("not_equals")
        ) {
            operador_relacional();
            expresion();
        }
    }

    //Operador-relacional
    private void operador_relacional() { //operador_relacional -> greater_than | greater_equals | less_than | less_equals | equals | not_equals
        String etiqueta = this.componenteLexico.getEtiqueta();
        switch (etiqueta) {
            case "less_than":
                compara("less_than");
                System.out.print(" < " + "\n");
                break;
            case "less_equals":
                compara("less_equals");
                System.out.print(" <= " + "\n");
                break;
            case "greater_than":
                compara("greater_than");
                System.out.print(" > " + "\n");
                break;
            case "greater_equals":
                compara("greater_equals");
                System.out.print(" >= " + "\n");
                break;
            case "equals":
                compara("equals");
                System.out.print(" == " + "\n");
                break;
            case "not_equals":
                compara("not_equals");
                System.out.print(" != " + "\n");
                break;
        }
    }



    //Expresion
    private void expresion() {
        termino();
        masexpresion();
    }
    private void masexpresion() {
        termino();
        if(this.componenteLexico.getEtiqueta().equals("add")) {

            compara("add");
            termino();
            System.out.print(" + " + "\n");
            masexpresion();
        }else if(this.componenteLexico.getEtiqueta().equals("subtract")) {
            compara("subtract");
            termino();
            System.out.print(" - " + "\n");
            masexpresion();
        }else {
            termino();
        }
    }



    //Termino
    private void termino() {
        factor();
        mastermino();
    }
    private void mastermino() { //mastermino -> factor masfactor | factor
        factor();
        while (true) {
            String etiqueta = this.componenteLexico.getEtiqueta();
            switch (etiqueta) {
                case "divide":
                    compara("divide");
                    factor();
                    System.out.print(" / " + "\n");
                    break;
                case "multiply":
                    compara("multiply");
                    factor();
                    System.out.print(" * " + "\n");
                    break;
                case "remainder":
                    compara("remainder");
                    factor();
                    System.out.print(" % " + "\n");
                    break;
                default:
                    return;
            }
        }
    }




    //Factor


    private void factor() { //factor -> id | id open_square_bracket expresion closed_square_bracket | constante | open_parenthesis expresion closed_parenthesis | llamada_funcion
        String etiqueta = this.componenteLexico.getEtiqueta();
        switch (etiqueta) {
            case "int":
                if (this.comparacionTipo != null && !this.comparacionTipo.equals("int")) {
                    this.errores.add("Error en la linea " + this.lexico.getLineas() + " se intenta asignar un " + this.comparacionTipo + " con un int");
                }
                NumeroEntero numero = (NumeroEntero) this.componenteLexico;
                System.out.print(" push " + numero.getValor() + "\n");
                compara("int");
                break;
            case "float":
                if (this.comparacionTipo != null && !this.comparacionTipo.equals("float")) {
                    this.errores.add("Error en la linea " + this.lexico.getLineas() + " se intenta asignar un " + this.comparacionTipo + " con un float");
                }
                NumeroReal numeroR = (NumeroReal) this.componenteLexico;
                System.out.print(" push " + numeroR.getValor() + "\n");
                compara("float");
                break;
            case "open_parenthesis":
                compara("open_parenthesis");
                expresion();
                compara("closed_parenthesis");
                break;
            case "id":
                variable();
                break;
        }
    }






    private void compara(String etiqueta) {

        if(this.componenteLexico.getEtiqueta().equals(etiqueta))
            this.componenteLexico = this.lexico.getComponenteLexico(); // AVANZA
        else
            System.out.println("Error, se esperaba " + etiqueta);
    }

    public String errores() {
        String s = "";
        if(this.errores.isEmpty()) {
            s = "Programa compilado correctamente";
        } else {
            for(String elem: this.errores) {
                s += elem + "\n";
            }
        }
        return s;
    }

}