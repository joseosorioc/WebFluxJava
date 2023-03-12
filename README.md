# WebFluxJava
Proyect for learning webflux java 

<h3>Observer Pattern</h3>

<p> <strong>Observer</strong> is a behavioral design pattern that lets you define a subscription mechanism to notify
multiple objects about any events that happen to the object they’re observing. </p>

<img src="https://refactoring.guru/images/patterns/content/observer/observer-2x.png?id=d5a83e115528e9fd633f04ad2650f1db" 
        alt="Observer Pattern" width="400" height="250">

<h5>Terms</h5> 

Contrapresion o BackPressure: 
Hace referencia a la capacidad con la que podemos procesar las emisiones que realizan los flujos.
El concepto de Rx Back Pressure hace referencia a cuando tenemos un flujo de datos y nos damos cuenta
de que no todo ese flujo de datos nos es interesante sino que podemos prescindir de una parte de él . 
Por ejemplo en nuestro caso sería interesante que cada 2 segundos nos muestre un mensaje en la consola
con lo que en este se encuentre escrito. Se puede usar en cualquier circunstancia, incluso cuando en el 
servidor no tenemos el hardware con toda la capacidad.