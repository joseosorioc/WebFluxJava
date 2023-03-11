package com.osorio.springbootreactor;

import com.osorio.springbootreactor.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

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
}
