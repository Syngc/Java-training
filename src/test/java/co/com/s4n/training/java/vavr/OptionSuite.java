package co.com.s4n.training.java.vavr;

import co.com.s4n.training.java.Operaciones;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;



import io.vavr.PartialFunction;
import io.vavr.control.Option;

import static io.vavr.API.None;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.ArrayList;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;
import io.vavr.collection.List;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

//import java.util.List;
import java.util.Optional;

import static io.vavr.API.Some;


@RunWith(JUnitPlatform.class)
public class OptionSuite {


    /**
     * Un option se puede filtar, y mostrar un some() o un none si no encuentra el resultado
     */
    @Test
    public void testOptionWithFilter() {
        Option<Integer> o = Option(3);

        assertEquals(
                Some(3),
                o.filter(it -> it >= 3));

        assertEquals(
                None(),
                o.filter(it -> it > 3));
    }

    /**
     * Se puede hacer pattern matching a un option y comparar entre Some y None.
     */
    private String patternMatchSimple(Option<Integer> number) {
        String result = Match(number).of(
                Case($Some($()),"Existe"),
                Case($None(),"Imaginario")
        );
        return result;
    }

    @Test
    public void testOptionWithPatternMatching() {
        Option<Integer> o1 = Option(1);
        Option<Integer> o2 = None();

        //Comparacion de Some o None()
        assertEquals("Existe", patternMatchSimple(o1));
        assertEquals("Imaginario", patternMatchSimple(o2));
    }
    /**
     *
     * el metodo peek aplica una funcion lambda o un metodo con el valor de Option cuando esta definido
     * este metodo se usa para efectos colaterales y retorna el mismo Option que lo llamó
     */
    
    /*@Test
    Se debe utilizar la clase List de Java
    public void testPeekMethod(){
        Option<String> defined_option = Option.of("Hello!");
        *//* Se debe utilizar una variable mutable para reflejar los efectos colaterales*//*
        final List<String> list = new ArrayList<>();
        Option<String> peek = defined_option.peek(list::add);// the same as defined_option.peek(s -> list.add(s))

        System.out.println("peek: "+ peek);

        assertEquals("failed - peek did not return the same Option value",
                Option.of("Hello!"),
                defined_option);

        assertEquals("failed - peek did not apply the side effect",
                "Hello!",
                list.get(0));
    }*/

    /**
     * Un option se puede transformar dada una función
     */
    @Test
    public void testOptionTransform() {
        String textToCount = "Count this text";
        Option<String> text = Option.of(textToCount);
        Option<Integer> count = text.transform(s -> Option.of(s.getOrElse("DEFAULT").length()));

        assertEquals(
                Option.of(textToCount.length()),
                count);

        Option<String> hello = Option.of("Hello");
        Tuple2<String, String> result = hello.transform(s -> Tuple.of("OK", s.getOrElse("DEFAULT")));

        assertEquals(
                Tuple.of("OK", "Hello"),
                result);

    }

    /**
     * el metodo getOrElse permite obtener el valor de un Option o un sustituto en caso de ser None
     */
    @Test
    public void testGetOrElse(){
        Option<String> defined_option = Option.of("Hello!");
        Option<String> none = None();
        assertEquals("Hello!", defined_option.getOrElse("Goodbye!"));
        assertEquals("Goodbye!", none.getOrElse("Goodbye!"));
    }

    /**
     * el metodo 'when' permite crear un Some(valor) o None utilizando condicionales booleanos
     */
    @Test
    public void testWhenMethod(){
        Option<String> valid = Option.when(true, "Good!");
        Option<String> invalid = Option.when(false, "Bad!");
        assertEquals( Some("Good!"), valid);
        assertEquals( None(), invalid);
    }

    @Test
    public void testOptionCollect() {
        final PartialFunction<Integer, String> pf = new PartialFunction<Integer, String>() {
            @Override
            public String apply(Integer i) {
                return String.valueOf(i);
            }

            @Override
            public boolean isDefinedAt(Integer i) {
                return i % 2 == 1;
            }
        };
        assertEquals( None(),Option.of(2).collect(pf));
        assertEquals( None(),Option.<Integer>none().collect(pf));
    }
    /**
     * En este test se prueba la funcionalidad para el manejo de Null en Option con FlatMap
     */
    @Test
    public void testMananagementNull(){
        Option<String> valor = Option.of("pepe");
        Option<String> someN = valor.map(v -> null);

        /* Se valida que devuelve un Some null lo cual podria ocasionar en una Excepcion de JavanullPointerExcepcion*/
        assertEquals(
                someN.get(),
                null);

        Option<String> buenUso = someN
                .flatMap(v -> {
                    System.out.println("testManagementNull - Esto se imprime? (flatMap)");
                    return Option.of(v);
                })
                .map(x -> {
                    System.out.println("testManagementNull - Esto se imprime? (map)");
                    return x.toUpperCase() +"Validacion";
                });

        assertEquals(
                None(),
                buenUso);
    }

    /**
     * En este test se prueba la funcionalidad para transformar un Option por medio de Map y flatMap
     */
    @Test
    public void testMapAndFlatMapToOption() {
        Option<String> myMap = Option.of("mi mapa");

        Option<String> myResultMapOne = myMap.map(s -> s + " es bonito");

        assertEquals(
                Option.of("mi mapa es bonito"),
                myResultMapOne);

        Option<String> myResultMapTwo = myMap
                .flatMap(s -> Option.of(s + " es bonito"))
                .map(v -> v + " con flat map");


        assertEquals(
                Option.of("mi mapa es bonito con flat map"),
                myResultMapTwo);
    }

    @Test
    public void optionFromNull(){
        Option<Object> of = Option.of(null);
        assertEquals(of, None());
    }

    @Test
    public void optionFromOptional(){
        Optional optional = Optional.of(1);
        Option option = Option.ofOptional(optional);
    }

    private Option<Integer> esPar(int i){
        return (i%2==0)?Some(i):None();
    }

    @Test
    public void forCompEnOption1(){
        Option<Integer> integers = For(esPar(2), d -> Option(d)).toOption();
        assertEquals(integers,Some(2));
    }

    @Test
    public void forCompEnOption2(){
        Option<Integer> integers = For(esPar(2), d ->
                                   For(esPar(4), c -> Option(d+c))).toOption();
        assertEquals(integers,Some(6));
    }

    //Ejercicio con Optional

    //Concatenar
    @Test
    public void testConcatenar(){
        Option<String> c = Operaciones.concatenar("Hola", "Mundo");
        assertEquals("HolaMundo",c.getOrElse("No concatena"));
    }

    @Test
    public void testConcatenarVacio(){
        Option<String> c = Operaciones.concatenar("Hola", "");
        assertEquals("Uno de los strings es vacio",c.getOrElse("Uno de los strings es vacio"));
    }

    @Test
    public void testConcatenarFlatmap(){
        Option<String> res =
                Operaciones.concatenar("A","l")
                        .flatMap(a -> Operaciones.concatenar(a,"o"))
                        .flatMap(b -> Operaciones.concatenar(b,"h"))
                        .flatMap(c -> Operaciones.concatenar(c,"a"));
        assertEquals("Aloha", res.getOrElse("Adiós"));
    }

    @Test
    public void testConcatenarFlatmapNone(){
        Option<String> res =
                Operaciones.concatenar("A","l")
                        .flatMap(a -> Operaciones.concatenar(a,""))
                        .flatMap(b -> Operaciones.concatenar(b,"h"))
                        .flatMap(c -> Operaciones.concatenar(c,"a"));
        assertEquals("Adiós", res.getOrElse("Adiós"));
    }

    @Test
    public void testConcatenarFor(){
        Option<String> res =
                For(Operaciones.concatenar("A","l"), a ->
                For(Operaciones.concatenar(a,"o"),b ->
                For(Operaciones.concatenar(b,"h"),c -> (Operaciones.concatenar(c,"a"))))).toOption();
        assertEquals("Aloha",res.getOrElse("Adiós"));

    }

    @Test
    public void testConcatenarForNone(){
        Option<String> res =
                For(Operaciones.concatenar("A","l"), a ->
                For(Operaciones.concatenar(a,""),b ->
                For(Operaciones.concatenar(b,"h"),c -> (Operaciones.concatenar(c,"a"))))).toOption();
        assertEquals("Adiós",res.getOrElse("Adiós"));
    }

    //Reemplazar
    @Test
    public void testReemplazar(){
        Option<String> c = Operaciones.reemplazar("Hola", "H","P");
        assertEquals("Pola",c.getOrElse("No concatena"));
    }

    @Test
    public void testReemplazarVacio(){
        Option<String> c = Operaciones.reemplazar("Hola", "a"," ");
        assertEquals("Uno de los strings es vacio",c.getOrElse("Uno de los strings es vacio"));
    }

    @Test
    public void testReemplazarFlatmap() {
        Option<String> res =
                Operaciones.reemplazar("Sol", "S", "L")
                        .flatMap(a -> Operaciones.reemplazar(a, "o", "u"))
                        .flatMap(b -> Operaciones.reemplazar(b, "l", "n"))
                        .flatMap(c -> Operaciones.concatenar(c, "a"));
        assertEquals("Luna", res.getOrElse("Brilla brilla"));
    }

    @Test
    public void testReemplazarFlatmapNone(){
        Option<String> res =
                Operaciones.reemplazar("Sol","S","L")
                        .flatMap(a -> Operaciones.reemplazar(a,"o"," "))
                        .flatMap(b -> Operaciones.reemplazar(b,"l"," "))
                        .flatMap(c -> Operaciones.concatenar(c,"a"));
        assertEquals("Brilla brilla", res.getOrElse("Brilla brilla"));
    }

    @Test
    public void testReemplazarFor(){
        Option<String> res =
                For(Operaciones.reemplazar("Sol","S","L"), a ->
                For(Operaciones.reemplazar(a,"o","u"),b ->
                For(Operaciones.reemplazar(b,"l","n"),c -> (Operaciones.concatenar(c,"a"))))).toOption();
        assertEquals("Luna",res.getOrElse("Brilla brilla"));

    }

    @Test
    public void testReemplazarForNone(){
        Option<String> res =
                For(Operaciones.reemplazar("Sol","S","L"), a ->
                For(Operaciones.reemplazar(a,"o"," "),b ->
                For(Operaciones.reemplazar(b,"l","n"),c -> (Operaciones.concatenar(c,"a"))))).toOption();
        assertEquals("Brilla brilla",res.getOrElse("Brilla brilla"));
    }

    //Agregar
    @Test
    public void testAgregar(){
        List<String> lista = List.of();
        Option<List<String>> c = Operaciones.agregar(lista, "Hola");
        assertEquals(List.of("Hola"),c.getOrElse(List.of("Adiós")));
    }

    @Test
    public void testAgregarVacio(){
        List<String> lista = List.of();
        Option<List<String>> c = Operaciones.agregar(lista, "");
        assertEquals(List.of("Adiós"),c.getOrElse(List.of("Adiós")));
    }

    @Test
    public void testAgregarWhiteSpace(){
        List<String> lista = List.of();
        Option<List<String>> c = Operaciones.agregar(lista, " ");
        assertEquals(List.of("Adiós"),c.getOrElse(List.of("Adiós")));
    }

    @Test
    public void testAgregarFlatmap(){
        List<String> lista = List.of();
        Option<List<String>> res =
                Operaciones.agregar(lista,"Buenos")
                        .flatMap(a -> Operaciones.agregar(a," días"))
                        .flatMap(b -> Operaciones.agregar(b," todos"));
        assertEquals(List.of("Buenos"," días"," todos"), res.getOrElse(List.of()));
    }

    @Test
    public void testAgregarFlatmapNone(){
        List<String> lista = List.of();
        Option<List<String>> res =
                Operaciones.agregar(lista,"Buenos")
                        .flatMap(a -> Operaciones.agregar(a," "))
                        .flatMap(b -> Operaciones.agregar(b," todos"));
        assertEquals(List.of(), res.getOrElse(List.of()));
        }

    @Test
    public void testAgregarFor(){
        List<String> lista = List.of();
        Option<List<String>> res =
                For(Operaciones.agregar(lista,"Buenos"), a ->
                For(Operaciones.agregar(a," días"),b -> Operaciones.agregar(b," todos"))).toOption();
        assertEquals(List.of("Buenos"," días"," todos"),res.getOrElse(List.of("Bye"," bye")));
    }

    @Test
    public void testAgregarForNone(){
        List<String> lista = List.of();
        Option<List<String>> res =
                For(Operaciones.agregar(lista,"Buenos"), a ->
                For(Operaciones.agregar(a," "),b -> Operaciones.agregar(b," todos"))).toOption();
        assertEquals(List.of("Bye"," bye"),res.getOrElse(List.of("Bye"," bye")));
    }

    //Todos
    @Test
    public void testFlagMap(){
        String palabra = "Hi";
        Option<List<String>> res =
                Operaciones.reemplazar(palabra,"H","A")
                    .flatMap(a -> Operaciones.reemplazar(a,"i","l"))
                    .flatMap(b -> Operaciones.concatenar(b,"o"))
                    .flatMap(c -> Operaciones.concatenar(c,"h"))
                    .flatMap(d -> Operaciones.concatenar(d,"a"))
                    .flatMap(e -> Operaciones.agregar(List.of(e)," mundo"));

        assertEquals(List.of("Aloha"," mundo"), res.getOrElse(List.of()));
    }

    @Test
    public void testFor(){
        String palabra = "Hi";
        Option<List<String>> res =
                        For(Operaciones.reemplazar(palabra,"H","A"), a ->
                        For(Operaciones.reemplazar(a,"i","l"), b ->
                        For(Operaciones.concatenar(b,"o"), c ->
                        For(Operaciones.concatenar(c,"h"), d ->
                        For(Operaciones.concatenar(d,"a"), e -> (Operaciones.agregar(List.of(e)," mundo"))))))).toOption();

        assertEquals(List.of("Aloha"," mundo"), res.getOrElse(List.of()));
    }

}
