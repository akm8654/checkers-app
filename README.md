# WebCheckers

An online Checkers game system built in Java 8 and Spark,
a web micro-framework.


## Team

- Mikayla Wishart
- Austin Miller
- Mario Castano
- Amena Baig
- Sean Bergen


## Prerequisites

- Java 8
- Maven


## How to run it

1. Clone the repository and go to the root directory.
2. Execute `mvn compile exec:java`
3. Open in your browser `http://localhost:4567/`
4. Start a game and begin playing.

## Known bugs and disclaimers
1. If you change themes during spectator mode and refresh the page, then
   a username was not produced in the params and produces a null pointer.

## How to run the demo test boards
If you challenge a player with one of these names, a game will start with a 
board with the condition on the right.

Sign in with one of these usernames challenge any other to yield the 
corresponding test result!

|             Username             |                        Test Board Scenario Generated                       |
|----------------------------------|----------------------------------------------------------------------------|
|       TEST MULTI KING JUMP       |                      A king needs to make a multi jump                     |
|         TEST REQUIRE JUMP        |                      A red piece needs to make a jump                      |
|       TEST NECESSARY WHITE       |                A white piece needs to make a jump (their turn)             |
|        TEST NO MORE MOVES	       |              There are no moves available on this board, red won           |
|      TEST MULTI JUMP REQUIRE     |                         Normal multi-jump required                         |
|         ABOUT TO JUMP ALL        | The board is set for the red player to win by jumping the last white piece |
|            TEST DEMO 1           |                        The first board for our demo                        |
|          ABOUT NO MOVES          |                  There are about to be no more moves left                  |
|         KING BACK AGAIN          |    The 4 jumps where the king doubles back on itself, but it's allowed     |


## How to test it

The Maven build script provides hooks for run unit tests and generate code coverage
reports in HTML.

To run tests on all tiers together do this:

1. Execute `mvn clean test jacoco:report`
2. Open in your browser the file at `PROJECT_HOME/target/site/jacoco/index.html`

To run tests on a single tier do this:

1. Execute `mvn clean test-compile surefire:test@tier jacoco:report@tier` where `tier` is one of `ui`, `appl`, `model`
2. Open in your browser the file at `PROJECT_HOME/target/site/jacoco/{ui, appl, model}/index.html`

To run tests on all the tiers in isolation do this:

1. Execute `mvn exec:exec@tests-and-coverage`
2. To view the Model tier tests open in your browser the file at `PROJECT_HOME/target/site/jacoco/model/index.html`
3. To view the Application tier tests open in your browser the file at `PROJECT_HOME/target/site/jacoco/appl/index.html`
4. To view the UI tier tests open in your browser the file at `PROJECT_HOME/target/site/jacoco/ui/index.html`


## How to generate the Design documentation PDF

1. Execute `mvn exec:exec@docs`
2. Note: this command will fail on a clean project without a `/target`
directory. Create the directory first if running after a `clean` operation
without any intervening commands that create the directory, such as compile.
3. The generated PDF will be in `PROJECT_HOME/target/` directory


## How to create a zipfile distribution of the source for the project

1. Execute `mvn exec:exec@zip`
2. The distribution zipfile will be in `PROJECT_HOME/target/WebCheckers.zip`

## License

MIT License

See LICENSE for details.
