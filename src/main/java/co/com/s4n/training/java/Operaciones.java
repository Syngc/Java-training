package co.com.s4n.training.java;


import io.vavr.PartialFunction;
import io.vavr.control.Option;

import static io.vavr.API.None;
import io.vavr.collection.List;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.ArrayList;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;


import java.util.Optional;

import static io.vavr.API.Some;

public class Operaciones {

    public static Option<String> concatenar(String a, String b){
        Option<String> c = a != "" && b!= "" ? Option.of(a + b) : None();
        return c;
    }

    public static Option<String> reemplazar(String a, String b, String c){
        Boolean aux = a !=" " && b != "" && c != " ";
        Option<String> res = aux ? Option.of(a.replaceFirst(b,c)) : None();
        return res;
    }

    public static Option<List<String>> agregar(List<String> lista,String a ){
        boolean bol =  a!= " " && a!="";
        Option<List<String>> res = bol ? Option(lista.append(a)) : None();
        return res;
    }
}
