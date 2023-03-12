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

import java.util.ArrayList;
import java.util.List;

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
        ejemploUsuarioComentarioFlatMap();
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
