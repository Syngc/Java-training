package co.com.s4n.training.java.vavr;


import io.vavr.Lazy;
import io.vavr.concurrent.Future;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static java.lang.Thread.sleep;
import java.util.function.Supplier;
import static junit.framework.TestCase.assertEquals;

@RunWith(JUnitPlatform.class)
public class LazySuite {

    @Test
    public void exerciseLazy(){

        Lazy<Future<Integer>> f1 = Lazy.of(()->Future.of(() -> {
            sleep(500);
            return 1;
        }));
        Lazy<Future<Integer>> f2 = Lazy.of(()->Future.of(() -> {
            sleep(800);
            return 1;
        }));
        Lazy<Future<Integer>> f3 = Lazy.of(()->Future.of(() -> {
            sleep(300);
            return 1;
        }));

        long i = System.nanoTime();
        Future<Integer> fut =
                f1.get().flatMap(x ->
                        f2.get().flatMap(y ->
                                f3.get().flatMap(z -> Future.of(() -> x+y+z))));
        fut.await();
        long f = System.nanoTime();

        double elapsed = (f - i) * Math.pow(10,-6) ;
        System.out.println(elapsed);

        assertEquals(1600, elapsed,100);
    }

    @Test
    public void exerciseLazy2(){

        Lazy<Future<Integer>> f1 = Lazy.of(()->Future.of(() -> {
            sleep(500);
            return 1;
        }));
        Lazy<Future<Integer>> f2 = Lazy.of(()->Future.of(() -> {
            sleep(800);
            return 1;
        }));
        Lazy<Future<Integer>> f3 = Lazy.of(()->Future.of(() -> {
            sleep(300);
            return 1;
        }));

        long i = System.nanoTime();
        Future<Integer> fut =
                f1.get().flatMap(x ->
                        f2.get().flatMap(y ->
                                f3.get().flatMap(z ->
                                    f2.get().flatMap(w -> Future.of(()-> x+y+z+w)))));
        fut.await();
        long f = System.nanoTime();

        long im = System.nanoTime();
        Future<Integer> futm =
                f1.get().flatMap(x ->
                        f2.get().flatMap(y ->
                                f3.get().flatMap(z ->
                                        f2.get().flatMap(w -> Future.of(()-> x+y+z+w)))));
        fut.await();
        long fm = System.nanoTime();

        double elapsed = (f - i) * Math.pow(10,-6) ;
        double elapsedm = (fm - im) * Math.pow(10,-6) ;
        System.out.println("elapsed "+elapsed+" elapsedm "+elapsedm);

        assertEquals(1600, elapsed,100);
        assertEquals(new Integer(4), fut.get());
    }

    @Test
    public void compareteSupplierLazy(){

        Supplier<Future<Integer>> s = ()-> {
            Future<Integer> f = Future.of(() -> {
                Thread.sleep(500);
                return 0;
            });
            return f;
        };

        Lazy<Future<Integer>> l = Lazy.of(() ->{
            Future<Integer> f = Future.of(() -> {
                Thread.sleep(500);
                return 0;
            });
            return f;
        });

        //Supplier
        long is = System.nanoTime();
        s.get();
        long fs = System.nanoTime();
        double elapseds = (fs - is) * Math.pow(10,-6) ;
        System.out.println("Supplier " + elapseds);

        //Supplier 2
        long is2 = System.nanoTime();
        s.get();
        long fs2 = System.nanoTime();
        double elapseds2 = (fs - is) * Math.pow(10,-6) ;
        System.out.println("Supplier 2 " + elapseds2);

        //Lazy 1
        long il = System.nanoTime();
        l.get();
        long fl = System.nanoTime();
        double elapsedl = (fl - il) * Math.pow(10,-6) ;
        System.out.println("Lazy 1 " + elapsedl);

        //Lazy 2
        long il2 = System.nanoTime();
        l.get();
        long fl2 = System.nanoTime();
        double elapsedl2 = (fl2 - il2) * Math.pow(10,-6) ;
        System.out.println("lazy 2 " + elapsedl2);

        assertTrue(elapsedl > elapsedl2);
        assertEquals(elapseds, elapseds2,10);
    }


}
