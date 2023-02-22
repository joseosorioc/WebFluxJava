package com.osorio.springbootreactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

    private static final Logger Log = LoggerFactory.getLogger(SpringBootReactorApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(SpringBootReactorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Flux<String>  nombres = Flux.just("Jose", "Carlos", "Osorio")
                .doOnNext( e -> {
                    if(e.isEmpty()){
                        throw new RuntimeException("Lista vacia");
                    }else{
                        System.out.println(e);
                    }
                });

        nombres.subscribe( e -> Log.info(e), err -> {
            System.out.println("Ocurrio un error...");
        } );


    }
}
