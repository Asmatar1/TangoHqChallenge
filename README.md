# TangoHQ Challenge Solution

This repository contains a Kotlin-based solution to the TangoHQ Challenge, a series of tasks and challenges related to making HTTP requests and solving various problems. The main functions include sending GET and POST requests, guessing a number, playing Wordle, and working with ESC/POS commands for receipt printers.

## Usage

1. Uncomment the specific function you want to use in the `main` function.
2. Replace `finalToken` with your authentication token.
3. Run the program.

## Functions

- `sendGet(token: String)`: Sends a GET request with an authentication token.
- `guessTheNumber(token: String)`: Implements a binary search algorithm to guess a number.
- `getWordle(token: String)`: Fetches Wordle data and plays the game.
- `guessWord(token: String, guess: String)`: Guesses the correct word in Wordle.
- `getEscpos(token: String)`: Retrieves ESC/POS data from the server.
- `postMyGuess(json: String, token: String)`: Posts a JSON payload to the server.

## Contributing

Feel free to contribute to this repository by adding improvements or additional functionality. Make sure to upload your code to GitHub and follow the provided instructions for submitting your solution.
