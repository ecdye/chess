# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Server Design Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIcDcuj3ZfF5vD6L9sgwr5iWw63O+nxPF+SwfgC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatJvqM2KgAQVB6jI+EsqGRLhhaHLcryBqCsKMCiuKrrSkml7wQqSoqhwXrgEgVGZDRDFuo6zrJjBJZoTy2a5pggEgnBJRXH0ozqMA5ILgAolA3hpmgEAwDmICpJxHR9AcxYaQAHl2ORgOUACsA4vrpqj6WOVbGaZCoWVZNk3PZsmJgStHarqmSxv604hhR4nUTx6j0UybpMVaNowIlDoZaynY4aUirKpoomUelsUaDhnbqeUSloCpCB5gpGn8VppY6Sy-lGSZ0DlD4wRBtASAAF4oMsDlXs5rk9jAAAsTgAIw+f1BnjlMQXDZxY1+hN02zVFuHyUCinIa1aiqeppXzdpXEBa2XzTrOzY7b+jmdtkS39oOvSviOowLpOfTvY2n0TpFy6rt4fiBF4KDoHuB6+Mwx7pJkmB-ReRTUNe0iGbuhn1IZzQtA+qhPt0kNzn+bJNb1kBzmDkX3Zp4IwEh9gY6h6O+hhGLYXKuExdJLIQhsEA0IlsKs+g5FiRJhjFVlZoRoUlqRMMsuGIV2gwMkGQ2YraAa4xfEE068owBVwnVWlknqw11vM2hGNtR1l1dTbQF9Xp22BUNaajdOx0zZFP1Ji5ePuSt62bUHL19HtYeHakkenW7F2ps1gtgN7amdQ9hNpj0c3l7954JwDQ5Lpw8ProEkK2ru0IwAA4qOrJY6euO12yjnlBUXdk5T9ijnTQYfRbMcAZ12nm+Mr3Qb7w9i4h0I97pqE773wtYWdGp1YRYDy+bys1S7dWWzlkbMTyMZBuxYTm-fvFc3bDtValqtSWynRXOi9fbNQPrpYunNNIB18gNL66cRqZ2ztHeaMo4613KKtDaQM4HB12qHJBEcoBTSjlXW24tAGa1KOSMAu81Cwmvs7NWd9iq5RYraYAypu6jiKnVGAZcKHlSEn-FWtVJaZRAWcMBe4IFqCgaXa2I9ep4NTog+2Fkp66VQdXWOi0MCeW8rgraajCEaJgFotQOiKGNSXrI2I9DVAKI3kox6vVLGqBbBUHonQ+iWIAJLSBbGtXsABmZa0xuh9BPJkA0FYvqPCiToBAoAGxxPfAkmYUTLEADlRyZL2I0chHZrbxz7EY-oHivE+L8aOQJwSwkRKiTE-UpFU6JPnMk1J6TQaZMifOXJ+SYaFMbiuTwCMNzYB8FAbA3B4DxUMPQlI2MzxuU3uXUetQGiT2npnOeL5Bm9OGYzPOwJl5Q3nDDaYtTRh5KOWvEuLjurc09HqehsI4ALPoUfLEJ88ISPdBYikl8LlMIAerNhj8uTPwKq-I2HEP6Qu-ghe2IiRL-3EUAyRYtbEyM+V6TI3zbrtUeamQRsCTGDWCgdYhpCc5oL0WUxOOD+iqKpftcO40SEnWsc6CWWL3SvMJaOWEhyUBgsxZrT+5ooXRh4aMPhAKBGuIEr-dFYjb4Ar+acks+K3mjmcWSlVPVWWUoQWYvQtpIRogxNYkpuQMFuSwRUwOfl8FpwtcAK1KAbWxF5YmHVaY9XCtGIa2CMCK43JQPUnaITwnFL9ky+uQMAlBNjY00ZzdEYBEsCgZUEBkgwAAFIQB5PKwwAQukgAbIPNZEbNmUjvC0SxM86wXJfLM4AuaoBwAgEhKA7NrmpvZgmpmdj+gryuc8FJ3be39sHVGmNxzObPLtgAK1LWgd5JaeREt9b8t2-LqFAovkGBWoLMAYs1QK6VWt2TQt5IbeQQp34XNvYI7maqnbgrvlI5mO6t0GuJT7I19aVFmpDtSzlR1uVkJOQ6-RCdsHJzdaYqDyDYP0vgqfAFpQYgADUuFIA4O81NErr1SshdrJ+vJ6FvwsXU6Q76P3ygAGZcIMKIm+LCtV-rsQBolOYSXQO6hSlO7K0yWpgNazCYA7ULSZatQGprxPmupVJmTtryF8qoeGUoG7d0itTbMTts6+3QFmM9cjPGb1UfvRQZgz0YCQG6KZygc7oAwDLXR7Q3RWOedTcx414JDLhHCDUcIXHmG6ZknxmRAmgNCZA+G0Tka2Vqf2lw3UHB37ADZqOxlmDE6hJQ-AyDmWEDZdy-l7VoD87Fs3YJu6iiwOV3g4UJNFTM3jJbgELwXauxelgMAbAszCDxESMsge8d1k9QqMTUm5NKbGBOXVs5pZ15GtXSikA3A8DSAAEKML+UevTMBdtDcO8dq9NnKP8KhdIPNFIDa7l3O+4LdtQvhci+q7jMXgE4vdnYxAl2Dthvtal7S6XytphcHD+T6DEOGOU66srBDqVw5cP686a3dV7agId8HftlFtZjqcTrynM1AA)

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
