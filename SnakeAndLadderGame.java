import java.util.*;

public class SnakeAndLadderGame {
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;

    private static final int MIN_DIFFICULTY = 1;
    private static final int MAX_DIFFICULTY = 3;

    private static final int BOARD_SIZE = 100;

    private static Map<Integer, String[]> snakes;
    private static Map<Integer, String[]> ladders;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Snake and Ladder Game!");

        int numPlayers = getNumberInput("Enter the number of players (2-4): ", MIN_PLAYERS, MAX_PLAYERS);

        int difficultyLevel = getNumberInput("Choose difficulty level (1-3): ", MIN_DIFFICULTY, MAX_DIFFICULTY);
        initializeGame(difficultyLevel);

        int[] playerPositions = new int[numPlayers];

        while (true) {
            for (int player = 0; player < numPlayers; player++) {
                System.out.println("\nPlayer " + (player + 1) + "'s turn. Press Enter to roll the dice.");
                scanner.nextLine();

                System.out.print("Rolling the dice...");

                // Simulate rolling dice animation
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.print("\rRolling the dice... " + getDiceAnimation(i + 1));
                }

                int diceResult = rollDice();
                playerPositions[player] = updatePosition(playerPositions[player], diceResult);
                playerPositions[player] = checkForSnakeAndLadder(playerPositions[player]);

                printBoard(playerPositions, numPlayers);
                System.out.println("\nPlayer " + (player + 1) + " rolled a " + diceResult +
                        ". Current position: " + getPlayerLabel(player + 1, playerPositions[player]));

                if (playerPositions[player] == BOARD_SIZE) {
                    System.out.println("Congratulations! Player " + (player + 1) + " won!");
                    return;
                }
            }
        }
    }

    private static String getDiceAnimation(int count) {
        switch (count % 6) {
            case 0:
                return "\u2680"; // ⚀
            case 1:
                return "\u2681"; // ⚁
            case 2:
                return "\u2682"; // ⚂
            case 3:
                return "\u2683"; // ⚃
            case 4:
                return "\u2684"; // ⚄
            case 5:
                return "\u2685"; // ⚅
            default:
                return "-";
        }
    }

    private static int rollDice() {
        return new Random().nextInt(6) + 1;
    }

    private static int updatePosition(int currentPosition, int diceResult) {
        int newPosition = currentPosition + diceResult;
        return newPosition <= BOARD_SIZE ? newPosition : currentPosition;
    }

    private static int checkForSnakeAndLadder(int position) {
        if (snakes.containsKey(position)) {
            String[] snakeLabels = snakes.get(position);
            System.out.println("Oops! You landed on a snake. Going down to " + snakeLabels[1] + ".");
            return getPositionByLabel(snakeLabels[1]);
        } else if (ladders.containsKey(position)) {
            String[] ladderLabels = ladders.get(position);
            System.out.println("Yay! You found a ladder. Climbing up to " + ladderLabels[1] + ".");
            return getPositionByLabel(ladderLabels[1]);
        }
        return position;
    }

    private static int getPositionByLabel(String label) {
        for (Map.Entry<Integer, String[]> entry : snakes.entrySet()) {
            if (entry.getValue()[1].equals(label)) {
                return entry.getKey();
            }
        }
        for (Map.Entry<Integer, String[]> entry : ladders.entrySet()) {
            if (entry.getValue()[1].equals(label)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private static void printBoard(int[] playerPositions, int numPlayers) {
        System.out.println("\nBoard:");
        for (int i = 10; i >= 1; i--) {
            for (int j = 1; j <= 10; j++) {
                int position = (i - 1) * 10 + (i % 2 == 0 ? 10 - j + 1 : j);
                printColoredCell(position, playerPositions, numPlayers);
            }
            System.out.println();
        }
    }


    // display labels on the board
    private static void printColoredCell(int position, int[] playerPositions, int numPlayers) {
        String label = getLabel(position);

        for (int player = 0; player < numPlayers; player++) {
            if (position == playerPositions[player]) {
                System.out.print(colorText(getPlayerLabelOnBoard(player + 1, position), getPlayerColor(player + 1)));
                return;
            }
        }

        if (snakes.containsKey(position)) {
            System.out.print(colorText(label, "\u001B[31m")); // Red for snakes
        } else if (ladders.containsKey(position)) {
            System.out.print(colorText(label, "\u001B[32m")); // Green for ladders
        } else {
            System.out.print(colorText(label, "\u001B[0m")); // Reset color
        }
    }

    // Add a new method for displaying player labels on the board
    private static String getPlayerLabelOnBoard(int player, int position) {
        return "P" + player;
    }

    // Update the existing 
    private static String getPlayerLabel(int player, int position) {
        return String.valueOf(position);
    }

    private static String getPlayerColor(int player) {
        switch (player) {
            case 1:
                return "\u001B[33m"; // Yellow for Player 1
            case 2:
                return "\u001B[34m"; // Blue for Player 2
            case 3:
                return "\u001B[35m"; // Magenta for Player 3
            case 4:
                return "\u001B[36m"; // Cyan for Player 4
            default:
                return "\u001B[0m"; // Reset color
        }
    }

    private static String colorText(String text, String colorCode) {
        return colorCode + String.format("%4s", text) + " \u001B[0m";
    }

    private static String getLabel(int position) {
        String label = "";

        if (position == 0) {
            label = "St";
        } else if (position == BOARD_SIZE) {
            label = "Fs";
        } else if (snakes.containsKey(position)) {
            label = snakes.get(position)[0];
        } else if (ladders.containsKey(position)) {
            label = ladders.get(position)[0];
        } else {
            label = String.valueOf(position);
        }

        return label;
    }

    private static void initializeGame(int difficultyLevel) {
        int numSnakesLadders = difficultyLevel; // Adjust the number of snakes/ladders based on difficulty

        int maxPosition = BOARD_SIZE - 1;
        Set<Integer> snakeLadderPositions = new HashSet<>();

        while (snakeLadderPositions.size() < numSnakesLadders * 4) {
            int position = new Random().nextInt(maxPosition) + 1;
            snakeLadderPositions.add(position);
        }

        List<Integer> positionsList = new ArrayList<>(snakeLadderPositions);
        Collections.shuffle(positionsList);

        snakes = new HashMap<>();
        ladders = new HashMap<>();

        for (int i = 0; i < numSnakesLadders; i++) {
            int snakeTailPosition = positionsList.get(i * 4);
            int snakeHeadPosition = positionsList.get(i * 4 + 1);
            int ladderBottomPosition = positionsList.get(numSnakesLadders * 2 + i * 2);
            int ladderTopPosition = positionsList.get(numSnakesLadders * 2 + i * 2 + 1);

            snakes.put(snakeTailPosition, new String[] { "S" + (i + 1), "S" + (i + 1) });
            snakes.put(snakeHeadPosition, new String[] { "S" + (i + 1), "S" + (i + 1) });

            ladders.put(ladderBottomPosition, new String[] { "L" + (i + 1), "L" + (i + 1) });
            ladders.put(ladderTopPosition, new String[] { "L" + (i + 1), "L" + (i + 1) });
        }
    }

    private static int getNumberInput(String prompt, int min, int max) {
        Scanner scanner = new Scanner(System.in);
        int input;

        while (true) {
            try {
                System.out.print(prompt);
                input = Integer.parseInt(scanner.nextLine());

                if (input >= min && input <= max) {
                    break;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        return input;
    }
}
