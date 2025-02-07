# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Server Design Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h2u6W5WVqlDlNiqVIAWWA2BaCigUGAlkdztOgIuZQiUKRUEiqiVWHLwJlhSOlxg1w6d0m5VWT0DfvqEAA1uge1N9o7KC34MhzOUAExOJzdTtDMVjGC9x5TAepIejtDj1YHdAcUxeXz+ALQdjkmAAGQg0SSATSGSyc7yreK7eqdSaVoDHUBI0FXAYuw3XYvheN4PhPKcQUKJt2zXW4oM+J5YKWaCAXOZtkydeUYAQZ8eVhJ8X1RdFYmxMF1RdMM3TJCkDVpSDRmxUACCoPUZCYllQyJcMLQ5bleQNQVhRgUVxQTc0kx-FMFSVFUOC9cAkF4zJ+OEt0SwYst8PbSieTrBtMBQkFCNQvpRnUYByWPABRAtoAVCAYHrEBUhkjo+gONtrIAD1nHIwHKABWZdwPs1RHM3XtXO8Ss0E87zfJuAKhKZN0Z3o1M7QFbQDMMQjwRKJAADNLCqfRXiWWFJO0FwXga95Zmw94uM07TDAK0qYHyuVUyq4AEAMQbFLOIETLItBzIQRtjOspSSiuOyWQSly3MrHxgiDaAkAALxQZZAt-EKwvnGAABYnAARliranK3KZkvcmSDr9I7TvOwbhuUix0U0DSeL4gaCo1ATtV1TJY39PcQ3kiNCijG0YAR+MYdZQGiNTdMsxzPMCyLKajNmytTIWtQLKsvHbNkxKJy+PcDzHN6EKCmdshupcV16CD11GY8dz6NmRw57dsrPC9vD8QIvBQdBH2fXxmDfdJMkwXnvyKag-2kZyH2c+pnOaFpgNUUDuglw9ELZKyNrtqWWbwym2QK8pSPsdWKLV31qIxOiRuhvSWQhDYIBoBGAyDdm0B68GdJRnKzVR9lSkiYZo8MLHtBgZIMl8l20DTkTpq9mAxomsR1O4rS+NTqHpqd1XfcD2mlsslaGcrfo4u2zmPr277Ul+s7su5pNQt1iK7se56HNepLdvKfa9wn-6Ubx8EYGBsQW4pityko9XFuWj3K8uq4LoN1browRcYt6U9ODlq9AkhW0H2hGAAHENysk1h+HWX5mA2UrBUf+psLb2A3LbeOksy7T2QitZ2SDDzjDdj3K+a097IFiIA+yFFoTELUEHWiU0CQ4xYmAWOpck6NxTjjcueVIxiR5DGIMUkwilzYYmcqxFFTKlBg3PqulcqCSPmgymp8yFAIvrgisfcNqDxXu9NeX1N5QBOpPO+xwZ6P3nvdJ6gt1HMz6CPdeY8t7ZR3tfZSmMeElRbjQ8OpJyRgHIaoWETCJGpwcWjThvJxoIAARubGHjcaOPxqUERakwbMMMM3UOrd0GPgUfZJR9NYm2QsTtFKHkYDwPslPS6MpZ7gKii-AeL1LHWJgGlEpQD7E413sRfMhZiwyJmifTJRDFFd0viovJ-c+ilLUOOCoPROgTI3AASWkOOB6C4ADMt1pjdD6O+TIBpuyc0eNsnQCBQDDn2VBQ5MxtmTIAHIbiuXsRoBjpzTTns-AW-RJmqGmbM+ZowlkrPWZs7Zuz9QcUsUco8JyzkXJFlcrZR47kPOlk8t+55PDy2vNgHwUBsDcHgHDQw5CUha0-OFT2N9Ki1AaHAhBY8E7gWRfC1FDtj7AgwYOZB2C+z-JQPcllODcn4OIp6PU5DYRwCJeQyhWJqGSPTnQhhmD0D+KbqwoJmdxLcLjAXaS-CHFCMKgksRvV1XRPJrI-pUqvSZBlcM5RBFIFqPqYUz6G9Dq6L+uU++lTjHlFMUveKGirFaI9T9L1+iBEKSNeUfO8h5Uow9NKjcsJmUoDVSwi1mr0a2nIVEqR5oOmphNfXM1WbC0aF6W3G14qNw5N7mMl1y8GlaL0LaSEaIMQ+sMbkKp4UA21M2i2t1lZ20wE7TRMAbTonFvKDyMAabFnSBDimGcNaU2jAbVfZ1HY+WAreqsjZLyH7vJgPzJly6gXHrMO-TFn8AiWBQMqCAyQYAACkIA8giaMQIMKQDDjARS3d1RKSARaJMxBXLDzgXxUWSgcAICkSgKLGY+7lmPLZVajle7S48seM8U5T6oCIeQ6h6Y6HUPu1GSKwqAArL9aAJWfp5PartVC3EKvDEqoMcdoOqswOI81lbo0Z0tNq5xur5BCj4SqsuhraMqVEWW5OKTWHVoySxpj9aHW5N3XUkdw8w22Mjf9aefb-ULzMQZ4NrainaM9Xo7e7TYl73jcARNtCYgADVxpIA4BKyZSzM1qezS54JXIuE-pQLwlpALpCiaGq5+UNdJoqeSVx-SGm5EfsY-a+s3c9NrXya6oz9nx2Tu7Sev1Z77qfOHbZ0d5QKsoHY9Og4O850H089E0oDHWOpqC9ITqRGENIegLMdzLgmYhcy4JHNHIKDMCZvvCA3Q4PEdI9AGA3783aG6FVbbQ3EtztS3XJJAT1NpPZe2LT+W6aNv0w1oeq97PjV1BwMI8gsHVaMbVpwayg0vc0W9hAH2vvAB+yd5LqZuvZf6XdnTBWRlOuK-3X7pwz0XtfrejFl4FYBC8EWWcXpYA5nxYQeIiRSWgLnpS++5QKhGxNmbC2xgsN9Jwz0ajBFFMwBANwPA0gABCfieuVvKAL0nIuxdCYrenRLHDSjSGfRSPOD4HyK9jdXcaaWLvCYV-DnDiBpfC+3aMp7BSyufRcLbntryLNnuivVq3r2bd246y57XcPrvYfbCboXZvdOPbR7fNlmPqnntqeizAQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
