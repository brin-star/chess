# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

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
## Phase 2 Endpoint Diagram
URL: https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmyyKp8izfL8-yAmMSxXKBgpAf6IzKUR8xqSCGk7HsOmYAZ8K+s6XYwAgQnihiznCQSRJgKSb6GLuNL7gyTJTipXI3gFd5LsKYoSm6MpymW7xKpgKrBhqbpGBAagwGgEDMFaaIwNAMAWQCmVqGgADkzDHGAIDxOFvKReJVBIjlvb9r5IHVP6zLTJe0BIAAXigHBRjGcaFHpSaVKJaZOAAjAROaqHmpmFsW0D1D4-V6oNI27HRTbDg6M0dq1Lobu6W5df5TX8vUh5yCgz7xOel7Xidi6VMu5CQCixqXlR6CNQuApnb59Sva+DntrZ9SCR5-4IIBjzARDPVgYR5bEaR3wUcDyEkahDbTcmyCpjAuH4aM2NJbjJOwUDiH1sTtGNgxnjeH4-heCg6AxHEiR8wLiO+FgomCqB9QNNIEb8RG7QRt0PRyaoCnDAAkBR+15AU9QADwEyzaDlOhdkPLC-rG0hNloxhLVte54vvfBJs+bDd1g0FYCva7lHu6D+73tF4pPh92iyvKNsg6l6owITOV5TAVAmkgDFfeDjuXT2fbbn5LW9bt8T7aN40oLGCnm5hFPYTA6ZLbTK1rQWYxFiW23F6Xh0c0HzX2Z2UMR-I+eCpn9QcCg3DHpe-ufbep0-cKMhT0yhgx4UmfdYP7V57dH726WYunhkqgAXbVvwoXYG6ZjNclGAOF4VmR2c0xPMouu-jYOKGr8YVABxJUGhJbXxlgAxWKt7BKmGDrKAw09ZoENhvM201Kjw0Bm7W2tkpYOXqMgHIQCcz+0Jh7TsY8F70hgIyX2s8N5zgiovIUoow4BmHsAKOmCA62zjhqROuVmCp2QBnShWcB4XUcrnTqnswGSi7vAg65dK7xjJrNWuj966LWWvyFu0F25bQVPI4ao0Gz0T7qdbOjloY3U9lSUR+C0RELUBiBh91vrMJgAApklYECeKVB6LeGMd7-xyAbJxPJUEyPQYfBGjjgGn3PjgoJNQXjLGgTmAsDRxjpJQAASWkAWBa4RgiBBBJseIuoUBuk5FZEEyRQBqmqZBMyaSlQADklRqQuDATot8JJXzUQ-J+NMjJjByaoTJ2SlT5MKcU0pyxymVKaapbSdSEANOWaZVZrS5gdLmF0npr9GLcwCBwAA7G4JwKAnAxAjMEOAXEABs8AJyGCcTAIo6jcH9Jlq0DoUCYFGKGog5BzMkLlCUmM9pzTtIwD6YmaJl96gb0hTkvZWzrJJPEW1J66InEYhyZ5NQ3lR5e0CtQpkft6HmPccuGK4cXyRwShvFKqo+EmyToItOIjGGLksd2DqpLZF9TgcYsa0YK6TWrmdOamjG7Zh0fmPRm1Sw7VFQdUxx1RHbwkUPRlI9bp2N5aOGAuKUD4rRUqVxYMQ5QzXH4uYATtXJLauEmx5CzoYLgK8pxCSUYXy-DqwyOy8kFPqEUkpcLpXkyGVTZ+owQ0zPDXMqNZiubMX8JYKezlNiCyQAkMAWa+wQFzQAKQgOKB1hh-D1JAGqT5D9vkpMaE0ZkMkeg5NgQNBRIKYBGzBegCFCafjrKzVAOAEBnJQD2AAdRYLkpWPQABC-EFBwAANLfByUmmAEbAhRrQZbL8yKB2KWHdgUdlAJ1TtnfOxdK612bpBNusNu6U3QkPk2tqAArCtaB8W-vFESkkpKjVuJNTQqlp7rXByivUelbD9UcOZae1laUE4coESnblNKxHnTalIoV4ii7qrLhK5RU074yvUfNeVYxm5Ko2h3QxpGe5mMCfyxDV53USIocan2FrpnSBg5FJe8HWE5M4S+3DOrXX+K3Lwqt5VVBVXytaHIMmXU50FfvYjpYl0hmA2AJRUrVHwBo5okZ9HFXrTbiq+oeh1wokJMSzVDFFPvJNGaGs4ZN7Os426g1UTzr+icWGJCJmq5mdlemTMTcbOt30aWLzwZzQwFrPWI5hqZD2IVNgLQeKlTTkoOitYF7gBjuvdANY26OAiaYb9Zk+XnrUIrKFKT0yeXgdk5dQLwAiNHrhPUct4pfXI1RpfINN9o2DMptTF+vd008y8BVvNBaVvykQMGWAwBsAXsIIgj5oC9MyzlgrJWKtjDSsRcemAIwP2Tc4yAbgeAXGgZy-x01L2oBvcCWJ8z230vxEMAV3Q+gxC6cG-6DEZ99ykiSbI+7M3zOxvm6MI5QA

PNG format:
![Endpoint Sequence Diagram](Phase2Diagram.png)