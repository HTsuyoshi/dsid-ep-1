# Desenvolvimento de Sistemas Distribuídos EP 1 - RMI

Exercício Programa 1 de DSID feito com Java Swing e tem o objetivo de usar o `Remote Method Invocation` do Java para simular uma versão enxuta de um sistema de informaões sobre peças ou componentes (Parts).

#### Requisitos

O programa foi feito usando o Java 8

#### Como rodar

Para compilar o projeto rode:

```sh
make build
```

Para rodar o cliente:

```sh
java -jar ./bin/client-1.0.0.jar
```

Para rodar o server:

```sh
java -jar ./bin/server-1.0.0.jar
```

#### Se tudo der errado

Para rodar um Cliente:

```sh
./gradlew client:run
```

Para rodar um Servidor:

```sh
./gradlew server:run
```
