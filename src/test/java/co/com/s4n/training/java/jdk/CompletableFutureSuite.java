package co.com.s4n.training.java.jdk;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.util.concurrent.*;

public class CompletableFutureSuite {

    private void sleep(int milliseconds){
        try{
            Thread.sleep(milliseconds);
        }catch(Exception e){
            System.out.println("Problemas durmiendo hilo");
        }
    }
    //H:M:MS
    //date;SimpleDateFormat

    private void impMensaje(String mensaje){
        Date date = new Date();
        SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm:ss.SSS");
        System.out.println(mensaje+" a las "+ formatDate.format(date));
    }


    @Test
    public void t1() {

        CompletableFuture<String> completableFuture
                = new CompletableFuture<>();


        ExecutorService executorService = Executors.newCachedThreadPool();


        executorService.submit(() -> {
            Thread.sleep(300);

            completableFuture.complete("Hello");
            return null;
        });
            System.out.println(Thread.currentThread().getName());

        try {
            String s = completableFuture.get(500, TimeUnit.MILLISECONDS);
            assertEquals(s, "Hello");
        }catch(Exception e){
            assertTrue(false);
        }finally{
            executorService.shutdown();

        }

    }

    @Test
    public void t2(){
        CompletableFuture<String> completableFuture
                = new CompletableFuture<>();

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(() -> {
            Thread.sleep(300);

            completableFuture.complete("Hello");
            return null;
        });

        try {
            String s = completableFuture.get(500, TimeUnit.MILLISECONDS);
            assertEquals(s, "Hello");
        }catch(Exception e){
            assertTrue(false);
        }finally{
            executorService.shutdown();
        }
    }

    @Test
    public void t3(){
        // Se puede construir un CompletableFuture a partir de una lambda Supplier (que no recibe parámetros pero sí tiene retorno)
        // con supplyAsync
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "Hello";
        });

        try {
            String s = future.get(500, TimeUnit.MILLISECONDS);
            assertEquals(s, "Hello");
        }catch(Exception e){

            assertTrue(false);
        }
    }

    @Test
    public void t4(){

        int i = 0;
        // Se puede construir un CompletableFuture a partir de una lambda (Supplier)
        // con runAsync
        Runnable r = () -> {
            sleep(300);
            System.out.println("Soy impuro y no merezco existir");
        };

        // Note el tipo de retorno de runAsync. Siempre es un CompletableFuture<Void> asi que
        // no tenemos manera de determinar el retorno al completar el computo
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(r);

        try {
            voidCompletableFuture.get(500, TimeUnit.MILLISECONDS);
        }catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void t5(){

        String testName = "t5";

        System.out.println(testName + " - El test (hilo ppal) esta corriendo en: "+Thread.currentThread().getName());

        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> {
            impMensaje(testName + " - completbleFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });

        //thenApply acepta lambdas de aridad 1 con retorno
        CompletableFuture<String> future = completableFuture
                .thenApply(s -> {
                    impMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());

                    return s + " World";
                })
                .thenApply(s -> {
                    impMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());

                    return s + "!";
                });

        try {
            assertEquals("Hello World!", future.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }


    @Test
    public void t6(){

        String testName = "t6";

        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> {
            impMensaje(testName + " - completableFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });


        // thenAccept solo acepta Consumer (lambdas de aridad 1 que no tienen retorno)
        // analice el segundo thenAccept ¿Tiene sentido?
        CompletableFuture<Void> future = completableFuture
                .thenAccept(s -> {

                    impMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                })
                .thenAccept(s -> {
                    impMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                });


    }

    @Test
    public void t7(){

        String testName = "t7";

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            impMensaje(testName + " - completableFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });

        //thenAccept solo acepta Consumer (lambdas de aridad 1 que no tienen retorno)
        CompletableFuture<Void> future = completableFuture
                .thenRun(() -> {
                    impMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    sleep(500);
                })
                .thenRun(() -> {
                   impMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                });

    }

    @Test
    public void t8(){

        String testName = "t8";

        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    return "Hello";
                })
                .thenCompose(s -> {
                    System.out.println(testName + " - compose corriendo en el thread: " + Thread.currentThread().getName());
                    return CompletableFuture.supplyAsync(() ->{
                        System.out.println(testName + " - CompletableFuture interno corriendo en el thread: " + Thread.currentThread().getName());
                        return s + " World"  ;
                    } );
                });

        try {
            assertEquals("Hello World", completableFuture.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }

    class Persona{
        String nombre;
        int edad;
        Persona(String nombre , int edad){
            this.nombre = nombre;
            this.edad = edad;
        }
    }

    @Test
    public void ejemploPersona(){
        CompletableFuture<Persona> completableFuturePersona = CompletableFuture
                .supplyAsync(() -> {
                    return "cynthia.22";
                })
                .thenCompose(s -> {
                    int index = s.indexOf(".");
                    int tam = s.length();
                    String nombre = s.substring(0,index);
                    String edadString = s.substring(index+1,s.length());
                    int edad = Integer.parseInt(edadString);
                    return CompletableFuture.supplyAsync(() -> {
                        Persona persona = new Persona(nombre, edad);
                        return persona;
                    });
                });
        try{
            Persona persona = completableFuturePersona.get();
            assertEquals("cynthia", persona.nombre);
            assertEquals(22,persona.edad);
        }catch (Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void t9(){

        String testName = "t9";


        // El segundo parametro de thenCombina es un BiFunction la cual sí tiene que tener retorno.
        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(testName +" - s1 en el thread "+ Thread.currentThread().getName());
                    return "Hello";
                })
                .thenCombine(
                        CompletableFuture.supplyAsync(() -> {
                            System.out.println(testName +" - s2 en el thread "+ Thread.currentThread().getName());
                            return " World";
                        }),
                        (s1, s2) -> {
                            System.out.println(testName +" - s1 + s2 en el thread " + Thread.currentThread().getName());
                            return s1+s2;
                        }
                );

        try {
            assertEquals("Hello World", completableFuture.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void t10(){

        String testName = "t10";

        // El segundo parametro de thenAcceptBoth debe ser un BiConsumer. No puede tener retorno.
        CompletableFuture future = CompletableFuture.supplyAsync(() -> "Hello")
                .thenAcceptBoth(
                        CompletableFuture.supplyAsync(() -> " World"),
                        (s1, s2) -> System.out.println(testName + " corriendo en thread: "+Thread.currentThread().getName()+ " : " +s1 + s2));

        try{
            Object o = future.get();
        }catch(Exception e){
            assertTrue(false);

        }
    }

 

    @Test
    public void testEnlaceConSupplyAsync(){
        ExecutorService es = Executors.newFixedThreadPool(1);
        CompletableFuture f = CompletableFuture.supplyAsync(()-> "Hi", es);


        CompletableFuture f2 = f.supplyAsync(() -> {
            impMensaje("t11 ejecutando a");
            sleep(500);
            return "a";
        }, es).supplyAsync(()->{
            impMensaje("t11 Ejecutando b ");
            return "b";

        }, es);

        try{

        }catch (Exception e){
            assertFalse(false);
        }
    }

    @Test
    public void t11(){

        String testName = "t11";

        ExecutorService es = Executors.newFixedThreadPool(1);
        CompletableFuture f = CompletableFuture.supplyAsync(()->"Hello",es);

        f.supplyAsync(() -> "Hello")
                .thenCombineAsync(
                    CompletableFuture.supplyAsync(() -> {
                        System.out.println(testName + " thenCombineAsync en Thread (1): " + Thread.currentThread().getName());
                        return " World";
                    }),
                    (s1, s2) -> {
                        System.out.println(testName + " thenCombineAsync en Thread (2): " + Thread.currentThread().getName());
                        return s1 + s2;
                    },
                    es
                );

    }

   /* @Test
    public void TestSupplyAsync(){

        String testName = "TestSupply - ";
        ExecutorService es = Executors.newFixedThreadPool(2);
        impMensaje(testName + "hilo principal ejecutado en " + Thread.currentThread().getName());
        CompletableFuture f = CompletableFuture
                .supplyAsync(()->{
                    impMensaje(testName + " supply 1 en " + Thread.currentThread().getName());
                    return "1";
                },es)
                .supplyAsync(()->{
                    sleep(500);
                    impMensaje(testName + " supply 2 en " + Thread.currentThread().getName());
                    return "2";
                }, es)
                .supplyAsync(()->{
                    impMensaje(testName + " supply 3 en " + Thread.currentThread().getName());
                    return "3";
                }, es);

    }*/

    @Test
    public void TestThenApplyAsync(){

        String testName = "TestApply - ";
        ExecutorService es = Executors.newFixedThreadPool(2);
        impMensaje(testName + "hilo principal ejecutado en " + Thread.currentThread().getName());


        CompletableFuture f = CompletableFuture
                .supplyAsync(()->{
                    impMensaje(testName + " supply 0 en " + Thread.currentThread().getName());
                    return "0";
                },es )
                .thenApplyAsync(s ->{
                    sleep(1000);
                    impMensaje(testName + " apply 1 en " + Thread.currentThread().getName());
                    return s+"1";
                },es)
                .thenApplyAsync(s ->{
                    sleep(500);
                    impMensaje(testName + " apply 2 en " + Thread.currentThread().getName());
                    return s+"2";
                }, es)
                .thenApplyAsync(s ->{
                    impMensaje(testName + " apply 3 en " + Thread.currentThread().getName());
                    return s+"3";
                }, es);
        try{
            String x = (String) f.get();
            assertEquals("0123", x);
        }catch(Exception e){
            assertTrue(false);
        }

    }

}
