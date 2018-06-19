package co.com.s4n.training.java.vavr;

import io.vavr.Function1;
import io.vavr.control.Either;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static io.vavr.API.Left;
import static io.vavr.API.None;
import static io.vavr.API.Right;
import java.util.function.Consumer;
import java.io.Serializable;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static io.vavr.API.*;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
;
@RunWith(JUnitPlatform.class)
public class EitherSuite {

    /**
     * Se valida la funcion swap con un Either right y left en la cual se aplica swap para el cambio de tipo Either
     */
    @Test
    public void swapToEither() {
        Either<Integer,String> myEitherR = Either.right("String");
        Either<Integer,String> myEitherL = Either.left(14);

        assertTrue(myEitherR.isRight());
        assertTrue( myEitherR.swap().isLeft());
        assertTrue(myEitherL.isLeft());
        assertTrue(myEitherL.swap().isRight());

        assertFalse(!myEitherR.isRight());
        assertFalse(!myEitherR.swap().isLeft());
        assertFalse(!myEitherL.isLeft());
        assertFalse(!myEitherL.swap().isRight());
    }

    /**
     * Los Either se definen porque tienen un manejo por convencion derecha el valor correcto e
     * izquierda si fue incorrecto, se usan las projecciones para saber con cual lado se operará.
     * Es decir si los Either son correctos es porque tienen una valor en la derecha y se sumara un valor x,
     * sino el lado izquierdo sera un mensaje que diga either incorrecto.
     */
    @Test
    public void testProjection(){
        Either<Integer,Integer> e1 = Either.right(5);
        Either<Integer,Integer> e2 = Either.left(5);

        //El Either por defecto cuando se usa el map opera con el lado derecho.
        assertEquals( Right(10), e1.map(it -> it + 5));

        //El Either para operar el lado izquierdo se debe usar un mapLeft.
        assertEquals(Left(10), e2.mapLeft(it -> it + 5));
    }

    @Test
    public void testMapLeft() {
        Either<Integer,Integer> e1 = Either.left(5);

        assertEquals(Left(10), e1.mapLeft(it -> it +5));
    }

    /**
     * El map por defecto operara el lado derecho, si el either tiene su informacion en el lado izquierdo
     * este no se modificara
     */
    @Test
    public void testEitherMap() {
        Either<String,Double> value = Either.right( 2.0 / 3);

        assertEquals(
                Right(4.0),
                value.map(aDouble -> aDouble * 6));

        Either<String,Double> value2 = Either.left("Left side");

        assertEquals(
                Left("Left side"),
                value2.map(aDouble -> aDouble * 6));

    }

    /**
     * El flatmap por defecto operara el lado derecho, si el either tiene su informacion en el lado izquierdo
     * este no se modificara
     */
    @Test
    public void testEitherFlatMap() {

        Either<String,Double> e1 = Either.right( 2.0 / 3);

        assertEquals(
                Right(4.0),
                e1.flatMap(aDouble -> Right(aDouble * 6)));

        Either<String,Double> e2 = Either.left("Left side");

        assertEquals(
                Left("Left side"),
                e2.flatMap(aDouble -> Right(aDouble * 6)));

    }


    private Either<String, Integer> sumar(Integer x, Integer y){
        Either<String,Integer> res = Either.right(x+y);
        return res;
    }

    private Either<String, Integer> restar(Integer x, Integer y){
        Either<String,Integer> res = x > y ?Either.right(x-y) : Either.left("Numero negativo");
        return res;
    }

    @Test
    public void testExercise(){
        Either<String,Integer> e1 = sumar(3 ,2);
        assertEquals(Right(5), e1.flatMap(e -> e1));

        Either<String, Integer> e2 = sumar(1, 2)//3
                .flatMap( a -> sumar(a,4)//7
                        .flatMap(b -> sumar(b,5)//12
                                .flatMap(c -> restar(c,3)))//9
        );

        assertEquals(Right(9),e2);
    }

    @Test
    public void testExerciseFail(){
        Either<String,Integer> e1 = sumar(3 ,2);
        assertEquals(Right(5), e1.flatMap(e -> e1));

        Either<String, Integer> e2 = sumar(1, 2)//3
                .flatMap( a -> sumar(a,4)//7
                        .flatMap(b -> sumar(b,5)//12
                                .flatMap(c -> restar(c,13)))//9
                );

        assertEquals(Left("Numero negativo"),e2);
    }





    /**
     * Un Either puede ser filtrado, y en el predicado se pone la condicion
     */
    @Test
    public void testEitherFilter() {

        Either<String,Integer> value = Either.right(7);

        assertEquals(
                None(),
                value.filter(it -> it % 2 == 0));
    }

    @Test
    public void testEitherFilterToSome() {

        Either<String,Integer> value = Either.right(8);

        assertEquals(
                Some(Right(8)),
                value.filter(it -> it % 2 == 0));
    }

    /**
     * Si el predicado del filter tiene un null, el void lanzara un Nullpointerexception
     */
    @Test
    public void testEitherFilter2() {
        assertThrows(NullPointerException.class, () -> {
            Either<String,Integer> value = Either.right(7);
            value.filter(null);
        });

    }

    /**
     * La funcion bimap me permite realizar map a los Either en su derecha o izquierda
     * esto dependera de en cual lado tiene informacion
     */
    @Test
    public void testEitherBiMap() {
        Function1<String,String> left = (Function1<String , String>) string -> "this the left";
        Function1<Integer,Integer> right = (Function1<Integer,Integer>) integer -> integer + 15;

        Function1<Either,Either> biMap = (Function1<Either, Either>) either ->
                either.bimap(left,right);

        Either<String,Integer> value = Either.right(5);
        Either<String,Integer> value2 = Either.left("this is some");

        assertEquals(Either.right(20),biMap.apply(value));
        assertEquals(Either.left("this the left"),biMap.apply(value2));
    }

    /**
     * Se valida la funcion orElseRun, la cual ejecuta una accion
     * si el either es either.left.
     */
    @Test
    public void testOrElseRun() {
        Either<Integer,String> myEitherR = Either.right("String");
        Either<Integer,String> myEitherL = Either.left(14);
        final String[] result = {"let's dance! "};
        Consumer<Object> addIfTrue = element -> {
            result[0] += element;
        };
        myEitherR.orElseRun(addIfTrue);
        assertEquals(
                "let's dance! ", result[0]);
        myEitherL.orElseRun(addIfTrue);
        assertEquals(
                "let's dance! 14", result[0]);
    }

    /**
     * Se valida la funcion peek y peekleft segun la proyeccion del Either ya sea right o left
     */
    @Test
    public void peekToEither() {

        final String[] valor = {"default"};
        Either<String,String> myEitherR = Either.right("123456");
        Either<String,String> myEitherL = Either.left("1234567");

        Consumer<String> myConsumer = element -> {
            if(element.length()>6){
                valor[0] = "foo";
            }
            else {
                valor[0] = "bar";
            }
        };

        myEitherL.peek(myConsumer);
        assertEquals("default", valor[0]);

        myEitherR.peek(myConsumer);
        assertEquals("bar", valor[0]);

        myEitherL.peekLeft(myConsumer);
        assertEquals("foo", valor[0]);
    }

    public class byPeek<L,R>{
         /*@param <L> The type of the Left consumer of an Either.
            @param <R> The type of the Right consumer of an Either.
         */
           public  Either<L,R> byPeek(Consumer<? super L> cl, Consumer<? super R> cr, Either<L,R> e){
               return e.isRight() ? e.peek(cr) : e.peekLeft(cl);
         }
    }

    @Test
    public void testByPeek(){
        Either<Integer,String> el = Either.right("1");
        byPeek bp = new byPeek();
        Consumer<Integer> cl = elem -> {
            System.out.println("Consumer por la izquierda");
        };
        Consumer<String> cr = elem -> {
            System.out.println("Consumer por la derecha");
        };
        Either ef = bp.byPeek(cl, cr, el);

    }








    /**
     * Uso de pattern matching para capturar un Either.Left
     */
    @Test
    public void testPatternMatchingLeftSide(){
        Option<String> none = None();

        //toEither method transform Option<String> to String
        Either<String, String> left = none.toEither(() -> "Left from None value");

        String result = Match(left).of(
                Case($Left($()), msg -> msg),
                Case($(), "Not found")
        );
        assertEquals(left.getLeft(), result);
    }

    /**
     * Uso de pattern matching para capturar un Either.Right
     */
    @Test
    public void testPatternMatchingRightSide(){
        Option<String> value = Option.of("Right value rules!");
        //toEither method transform Option<String> to String
        Either<String, String> right = value.toEither(() -> "Left from None value");
        String result = Match(right).of(
                Case($Right($()), msg -> msg),
                Case($(), "Not found")
        );
        assertEquals(right.getOrElse(""), result);
    }

    /**
     * Uso de narrow para duplicar objetos Either.
     */
    @Test
    public void testNarrow(){
        Either<Integer, String> either = Try.of(()-> "0").toEither(0);
        Either<Object, Object> copy = Either.narrow(either);
        assertEquals(either,copy);
        assertSame(
                either,
                copy);
    }

    /**
     * Las proyecciones de Either se pueden transformar con fold.
     * Retorna por defecto la proyección derecha si el Either tiene dicha proyección.
     */
    @Test
    public void testFoldRight(){
        String[] actual = transform("text_to_transform", true).fold(l -> l.split("_"), r -> r.split("_"));
        String[] expected = {"TEXT", "TO", "TRANSFORM"};
        assertArrayEquals(expected, actual);
    }

    /**
     * Las proyecciones de Either se pueden transformar con fold.
     * Retorna por defecto la proyección derecha si el Either tiene dicha proyección; de lo
     * contrario, retorna la proyección izquierda
     */
    @Test
    public void testFoldLeft(){
        String[] actual = transform("text_to_transform", false).fold(l -> l.split("_"), r -> r.split("_"));
        String[] expected = {"text", "to", "transform"};
        assertArrayEquals(expected, actual);
    }

    /**
     * Método utilitario para las pruebas de fold
     * @param s String a transformar
     * @param hasRightProjection si se le debe asignar valor en la derecha o no
     * @return Either<String, String>
     */
    private Either<String, String> transform(String s, boolean hasRightProjection){
        Either<String, String> either = Either.left(s.toLowerCase());
        if (hasRightProjection) either = Either.right(s.toUpperCase());
        return either;
    }



    /**
     * Aunque la documentación de fold either dice que el tipado de los mapper para cada proyección
     * debe ser igual, aún así, el compilador permite crear los mapper con tipos diferentes.
     */
    @Test
    public void testFoldWithTwoMappers(){
        Serializable rightProjection = transform("text_to_transform", true).fold(l -> l.split("_"), r -> r.length());
        assertEquals("17", rightProjection.toString());
        Serializable leftProjection = transform("text_to_transform", false).fold(l -> l.split("_"), r -> r.length());
        String[] expected = {"text", "to", "transform"};
        assertArrayEquals(expected, (String[])leftProjection);
    }
}
