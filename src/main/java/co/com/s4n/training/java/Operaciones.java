package co.com.s4n.training.java;


import io.vavr.collection.List;
import io.vavr.control.Option;

public class Operaciones {



    public static String concatenar(String a, String b){
        Option<String> x = Option.of(null);
        if(a.isEmpty() && b.isEmpty()){
            return x.get() ;
        }
        return a + b;

    }

    public static String reemplazar(String a, String b, String c){
        Option<String> x = Option.of(null);
        if(a.isEmpty() && b.isEmpty() && c.isEmpty()){
            return x.get() ;
        }
        return a.replaceFirst(b,c);

    }

    public static List<String> agregar(List<String> l, String a){
        return l.append(a);

    }
}
