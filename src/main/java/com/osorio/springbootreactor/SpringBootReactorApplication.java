package com.osorio.springbootreactor;

import com.osorio.springbootreactor.models.Comentario;
import com.osorio.springbootreactor.models.Usuario;
import com.osorio.springbootreactor.models.UsuarioComentario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/*
*Los Observables son inmutables.
* */
@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

    private static final Logger Log = LoggerFactory.getLogger(SpringBootReactorApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(SpringBootReactorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //ejemploIterable();
      // ejemploFlatMap();
        // ejemploToString();
       // ejemploCollectList();
        // ejemploUsuarioComentarioFlatMap();
       // ejemploUsuarioComentarioZipWith();
       // ejemploUsuarioComentarioZipWithForma2();
       // zipWithConRangos();
        // ejemploInterval();
       // ejemploDalayElements();
        // ejemploIntervalInfinito();
        ejemploContrapresion();
    }

    // ejemplo manejando la contrapresión
    public void ejemploContrapresion(){
        Flux.range(1,10)
                .log()
                .limitRate(2) // limitRate: permite dividir en lotes los elementos del flujo
                                        // en este caso es en lotes de 2. Pero hay que tener en cuenta algo...
                                        // por dentro maneja un algoritmo interno que de acuerdo a la cantidad de elementos, a
                                        // la calidad de la conexion es cuanto elementos va a traer por request, por eso puede
                                        // que después baje a mucha menos cantidad. Esto permite configurar el
                                        // número de elementos a traer a la vez. Una característica interesante
                                        // es que el límite se aplica incluso cuando el suscriptor pide más
                                        // eventos para procesar. El emisor divide los eventos en fragmentos
                                        // evitando consumir más del límite en cada petición o request.

                .subscribe();
    }

    /***
     * interval: permite definir un intervalo de tiempo para el flujo
     * , es decir, lo que también se le conoce como un delay.
     */
    public void ejemploInterval(){
        Flux<Integer> rango = Flux.range(1,12);
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        rango.zipWith(interval, (rangeStream, IntervalStream) -> {
            return rangeStream;
        }).doOnNext( p -> {
            Log.info(p.toString());
        }).blockLast(); // Estamos utilizando blocking, para bloquear el flujo, Los flujos en RxJava por defecto son NonBlocking,
                        // y se recomienda trabajar con NonBlocking (objetivo de la programacion reactiva)
                        // para evitar cuellos de botella.
                        // OJO: No se recomienda utilizar blocking, solo para casos especificos.
    }


    // el mismo ejemplo con interval.
    public void ejemploDalayElements(){
        Flux<Integer> rango = Flux.range(1,12)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(p -> {
                    Log.info(p.toString());
                });
        rango.blockLast(); // el blocklast lo que hace es suscribirnos y terminar con el flujo, hasta el ultimo elemento.
    }

    // interval infinito.
    public void ejemploIntervalInfinito() throws InterruptedException {

        /**
         * permite sincronizar entre Threads, lo que nos permite esperar
         * por uno o más hilos. CountDownLatch (desde Java 5) permite
         * hacer lo mismo que hacemos con el wait y notify de una forma
         * más sencilla (y con mucho menos código)
         */
        CountDownLatch latch = new CountDownLatch(1);

        Flux.interval(Duration.ofSeconds(1))
                .doOnTerminate(latch :: countDown )
                .flatMap(i -> {
                    if(i>5){
                        return Flux.error(new InterruptedException("El número es mayor a 5"));
                    }else{
                        return Flux.just(i);
                    }
                })
                .map(i -> "Hola " + i )
                .retry(3)   // permite manejar los reintentos,
                                     // luego de que ocurra algún fallo.
                .doOnNext(Log::info)
                .subscribe();

        latch.await();

    }

    /**
     * Range: permite crear un flujo de un rango determinado, en la documentacion:
     * create an Observable that emits a particular range of sequential integers,
     * The Range operator emits a range of sequential integers, in order,
     * where you select the start of the range and its length.
     * range(20 <start> ,45 <count>) : donde start es el parámetro (numero) donde va a iniciar
     * y count la cantidad a contar.
     */

    public void zipWithConRangos(){
        Flux.just(5,10,15,20).map(t -> (t*2)).zipWith(Flux.range(40,2) , (rangoTransformado, numerosdelRango ) -> {
            return  "flux1: " + rangoTransformado + " , " +  "flux2: " + numerosdelRango ;
        } ).subscribe(System.out::println);

        System.out.println("***********************************");

        Flux.range(20,45).subscribe(p-> System.out.println(p));

    }


    /**
     * FlatMap operator: basicamente es lo mismo que map, pero con una caracteristica
     * peculiar; que por cada emisión que realice va a retornar un flujo (Flux o Mono),
     * y luego el flujo principal lo termina unificando.
     * @throws Exception
     */
    public void ejemploFlatMap() throws Exception {

        List<String> usuariosList = new ArrayList<>();


        usuariosList.add("Jose");
        usuariosList.add("Carlos");
        usuariosList.add("MrX");
        usuariosList.add("Jose osorio");
        usuariosList.add("Manuel Gomez");
        usuariosList.add("Juan Ortega");



        Flux.fromIterable(usuariosList)
                .map(nombre -> new Usuario(nombre.toLowerCase(), "Apellido") )
                .flatMap( e -> {
                 if(e.getNombre().toLowerCase().contains("jose") ){
                    return Mono.just(e);
                 }else{
                     return Mono.empty();
                 }
                })
                .subscribe(e -> {
                    Log.info("Se imprimiran los nombres de los usuarios.");
                    Log.info(e.getNombre());
                    }, err -> {
                        System.out.println("Ocurrio un error...");
                    }, new Runnable() {
                        @Override
                        public void run() {
                            Log.info("Se completó el metodo");
                        }
                    });

    }

    /**
     * ZipWith:combine the emissions of multiple Observables together via
     * a specified function and emit single items for each combination
     * based on the results of this function.
     * consiste en tomar dos flujos y luego los combina.
     *
     * @throws Exception
     */
    public void ejemploUsuarioComentarioZipWith(){
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jhon", "Doe"));

        Mono<Comentario> comentariosUsuarioMono = Mono.fromCallable(() -> {
            Comentario comentario = new Comentario();

            comentario.addComentario("Prueba Jose 1");
            comentario.addComentario("Prueba Jose 2");
            comentario.addComentario("Prueba Jose 3");

            return comentario;
        });


        usuarioMono // partimos del flujo inicial,
                    // aunque tambien podriamos partir del flujo ComentariosUsuarioMono.
                .zipWith(comentariosUsuarioMono, (usuario, comentariosUsuario) -> // el primer argumento del zipWith es "other",
                                                                                // que corresponde con el otro flujo que queremos tomar.
                                                                                // Luego, le pasamos los dos nombres de referencia
                                                                                // para consumir esa lambda
                        new UsuarioComentario(usuario,comentariosUsuario))
                .subscribe(element -> {
                    Log.info(element.toString());
                });


    }

    public void ejemploUsuarioComentarioZipWithForma2(){
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jhon", "Doe"));

        Mono<Comentario> comentariosUsuarioMono = Mono.fromCallable(() -> {
            Comentario comentario = new Comentario();

            comentario.addComentario("Prueba Jose 1");
            comentario.addComentario("Prueba Jose 2");
            comentario.addComentario("Prueba Jose 3");

            return comentario;
        });


        usuarioMono
                .zipWith(comentariosUsuarioMono)
                .map(tuple -> {
                    return new UsuarioComentario(tuple.getT1(), tuple.getT2());
                })
                .subscribe(element -> {
                    Log.info(element.toString());
                });


    }

    public void ejemploIterable() throws Exception {

        List<String> usuariosList = new ArrayList<>();


        usuariosList.add("Jose");
        usuariosList.add("Carlos");
        usuariosList.add("MrX");
        usuariosList.add("Jose osorio");
        usuariosList.add("Manuel Gomez");
        usuariosList.add("Juan Ortega");





        Flux<Usuario>  nombres = Flux.fromIterable(usuariosList)
                .map(nombre -> new Usuario(nombre.toLowerCase(), "Apellido") )

                .doOnNext( e -> {
                    if(e.getNombre().isEmpty()){
                        throw new RuntimeException("Hay elementos vacios.");
                    }else{
                        System.out.println(e.getNombre());
                    }
                }).map( e -> {
                    e.setNombre(e.getNombre().toUpperCase());
                    return  e;
                })
                .filter(user -> user.getNombre().contains("OSORIO") );

        nombres.subscribe(e -> {
            Log.info("Se imprimiran los nombres de los usuarios.");
            Log.info(e.getNombre());
        }, err -> {
            System.out.println("Ocurrio un error...");
        }, new Runnable() {
            @Override
            public void run() {
                Log.info("Se completó el metodo");
            }
        });


    }

    public void ejemploToString(){
        List<Usuario> usuariosList = new ArrayList<>();


        usuariosList.add(new Usuario("Jose", "Osorio"));
        usuariosList.add(new Usuario("Carlos", "Torres"));
        usuariosList.add(new Usuario("MrX", "Havertz"));
        usuariosList.add(new Usuario("Pedro", "Perez"));
        usuariosList.add(new Usuario("Paulina", "Torres"));
        usuariosList.add(new Usuario("Juan", "Ortega"));


        Flux.fromIterable(usuariosList)
                .map(usuario ->  usuario.getNombre().toLowerCase())
                .flatMap( nombre -> {
                    if(nombre.equalsIgnoreCase("jose") ){
                        return Mono.just(nombre);
                    }else{
                        return Mono.empty();
                    }
                })
                .subscribe(e -> {
                    Log.info("Se imprimiran los nombres de los usuarios.");
                    Log.info(e.toUpperCase());
                }, err -> {
                    System.out.println("Ocurrió un error...");
                }, new Runnable() {
                    @Override
                    public void run() {
                        Log.info("Se completó el metodo");
                    }
                });
    }

    public void ejemploCollectList(){
        List<Usuario> usuariosList = new ArrayList<>();


        usuariosList.add(new Usuario("Jose", "Osorio"));
        usuariosList.add(new Usuario("Carlos", "Torres"));
        usuariosList.add(new Usuario("MrX", "Havertz"));
        usuariosList.add(new Usuario("Pedro", "Perez"));
        usuariosList.add(new Usuario("Paulina", "Torres"));
        usuariosList.add(new Usuario("Juan", "Ortega"));


        Flux.fromIterable(usuariosList)
                .collectList()   // retorna <Mono<T>> : Normalmente emite cada elementos de la lista
                                 // pero con collectList() se evita eso, y se emite la lista completa,
                                // en este caso es Mono<List<usuariosList>>.
                .subscribe(list -> {
                    list.forEach( element -> {
                        System.out.println(element.toString());
                    });
                });
    }

    public void ejemploUsuarioComentarioFlatMap(){
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jhon", "Doe"));

        Mono<Comentario> comentariosUsuarioMono = Mono.fromCallable(() -> {
            Comentario comentario = new Comentario();

            comentario.addComentario("Prueba Jose 1");
            comentario.addComentario("Prueba Jose 2");
            comentario.addComentario("Prueba Jose 3");

            return comentario;
        });

        usuarioMono
                .flatMap(usuario ->
                        comentariosUsuarioMono
                                .map(comentario -> new UsuarioComentario(usuario, comentario)))
                .subscribe(element -> {
                    Log.info(element.toString());
                });


    }



}
