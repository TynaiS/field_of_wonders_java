import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Field {

    private static final Scanner SCANNER = new Scanner(System.in);

    private  static  final int ONE_LETTER_LENGTH = 1;

    private static final Map<Integer, String> WORDS_AND_DESCRIPTIONS = Map.of(
            0,
            "��������@�������, � ��� ����������� ������ ����������� ����������� � ��������� ��� ������� ���� ���� ����������� � �������� ��� ��� ���������� �������.",
            1,
            "���������@����������� ��� � ��������� ������������ ����������� ����, �� ������� ����������� ������� �������� ���������� �������������� � ��������� ����������� ������� � ������������ ������� �������������� ������ ����������� ���.",
            2,
            "��������@��� ��������������� ����������� ���� � ����� ���������������� ���������, ����� ������� ����������� �� ����� ���������� ���.",
            3,
            "��������������@������������� ������������������ (������������, ������������, ������������) ���� � ������ ������� ������������, ��������� � ���������� ������������ ����������� ��������� � ����� ��������������� �����, �������������� ������ �����, �������� ����� ����� ����������� � ����� ��������� ������������� ������������, ��������� ������������� ������, �������������� �����������",
            4, "��������@���, ��� ����������� �� ��������� �����-�. ������, ������ ��� �������.",
            5,
            "��������@�������������� ��������� � ����������, ������� ��� �������� ��������, ���� �� ���������� ����, ������, ������������ ������ ��� �������������.",
            6,
            "������������@���� �� ������� �������� ���� ������� ������������� �������, � ����� ���� ��� ����. ����������� � ������������� ���������� �� ��������� ������� �������, ���������� ������������, � ������� ���������� ��� ���� � ����� ��������� ������� ������",
            7, "��������@����������� ������, ����������.",
            8,
            "�������������@������, ����� �������� ����-�. [����.: ����� ������ - ����, �������������� � ������������� ��������� ������� ������ ������].",
            9,
            "��������@����������� ����������, ��������������� ��� �������� �������� �� ������� �����. ����� ���������� �������� ��������, ������������� ��� ����������� ������� �����.");

    public static void main(String[] args) {
        startGame();
    }

    private static void startGame() {

        System.out.println("""

                ����� ���������� � ���� �����!
                (������� 'q' ��� ������ �� ���� � 'r' ����� ������ ������� � ����� ������)
                        """);

        Map<Integer, ArrayList> playersList = getPlayersFromUsers();

        String[] selectedWordAndDefinition = WORDS_AND_DESCRIPTIONS.get(getRandomNumInRange(9)).split("@", 2);

        int[] shuffledPlayersIndexes = shufflePlayers(playersList);

        goThroughPlayersAndPlay(playersList, shuffledPlayersIndexes, selectedWordAndDefinition);
    }

    private static void goThroughPlayersAndPlay(Map<Integer, ArrayList> playersList, int[] shuffledPlayersIndexes, String[] selectedWordAndDefinition) {
        boolean keepAskingPlayers = true;
        int halfOfMaxWordScore = 0;
        boolean isOnlyGuessByWord = false;
        String contenderForWin = "";

        String[] selectedWordHidden = new String[selectedWordAndDefinition[0].length()];
        String selectedWordUnique = "";

        for (int i = 0; i < selectedWordHidden.length; i++) {
            selectedWordHidden[i] = "#";
            if (!selectedWordUnique.contains(Character.toString(selectedWordAndDefinition[0].charAt(i)))) {
                selectedWordUnique += selectedWordAndDefinition[0].charAt(i);
            }
        }

        halfOfMaxWordScore = selectedWordUnique.length() * 100 / 2;

        while (keepAskingPlayers) {
            for (int i = 0; i < shuffledPlayersIndexes.length; i++) {

                String currentPlayerName = playersList.get(shuffledPlayersIndexes[i]).get(0).toString();

                if(shuffledPlayersIndexes.length == 1){
                    keepAskingPlayers = false;
                    if(currentPlayerName.equals(contenderForWin)){
                        System.out.println("\n�� ��������, " + contenderForWin + ", ����� �� ������ �����!");
                    } else {
                        System.out.println("\n�����������, " + currentPlayerName + ", �� ��������!");
                    }
                    askIfStartNewGameOrQuit(currentPlayerName);
                }

                int currentPlayerScore = (int) playersList.get(shuffledPlayersIndexes[i]).get(1);

                System.out.println("\n" + selectedWordAndDefinition[1]);
                System.out.println("\n" + currentPlayerName + ", ��� ���:");

                String playerQuess = "";

                while (playerQuess.length() == 0) {
                    String uncheckedPlayerQuess = SCANNER.nextLine().trim().toLowerCase();
                    if (uncheckedPlayerQuess.length() == 0) {
                        System.out.println("������� ����� ��� �����:");
                    } else if(String.join("", selectedWordHidden).contains(uncheckedPlayerQuess) && !isOnlyGuessByWord) {
                        System.out.println("��� ����� ��� ��������, ���������� ������ ����� ��� �����:");
                    } else {
                        playerQuess = uncheckedPlayerQuess;
                    }
                }

                handleUserQuitOrRestart(playerQuess);

                if (playerQuess.length() == ONE_LETTER_LENGTH && isOnlyGuessByWord == false) {
                    if (selectedWordAndDefinition[0].contains(playerQuess)) {
                        System.out.println("\n�����!");

                        String lastLetter = "";

                        for (int j = 0; j < selectedWordAndDefinition[0].length(); j++) {
                            if (String.valueOf(selectedWordAndDefinition[0].charAt(j)).equals(playerQuess)) {
                                selectedWordHidden[j] = playerQuess;
                                if(!lastLetter.equals(playerQuess)){
                                    currentPlayerScore+=100;
                                }
                                lastLetter = playerQuess;
                                ArrayList currentPlayer = new ArrayList<>(Arrays.asList(currentPlayerName, currentPlayerScore));
                                playersList.put(shuffledPlayersIndexes[i], currentPlayer);
                            }
                        }

                        System.out.println("\n� ��� " + currentPlayerScore + "/" + (halfOfMaxWordScore * 2) + " �����");
                        displayWordToUser(selectedWordHidden);

                        if(!String.join("", selectedWordHidden).contains("#")){
                            keepAskingPlayers = false;
                            System.out.println("\n�����������, " + currentPlayerName + ", �� ��������!");
                            askIfStartNewGameOrQuit(currentPlayerName);
                        }

                        if(currentPlayerScore > halfOfMaxWordScore) {
                            isOnlyGuessByWord = true;
                            System.out.println("\n� ��� ������� ����� �����, " + currentPlayerName + ", ����� ������ ������� �����!");
                            contenderForWin = currentPlayerName;
                            continue;
                        }

                    } else {
                        System.out.println("\n�� �����...");
                    }
                }
                else if(playerQuess.length() > 1 || isOnlyGuessByWord == true) {
                    if(currentPlayerName.equals(contenderForWin)){
                        keepAskingPlayers = false;
                        System.out.println("\n�� ��������, " + contenderForWin + ", ����� �� ������ �����!");
                        askIfStartNewGameOrQuit(contenderForWin);
                    }
                    if(playerQuess.equals(selectedWordAndDefinition[0])){
                        keepAskingPlayers = false;
                        System.out.println("\n�����������, " + currentPlayerName + ", �� ��������!");
                        askIfStartNewGameOrQuit(currentPlayerName);
                    } else {
                        System.out.println("\n�� ���������, "  + currentPlayerName + "...");
                        shuffledPlayersIndexes = removePlayerIndexFromList(shuffledPlayersIndexes, i);
                    }
                }
            }
        }
    }

    public static int[] removePlayerIndexFromList(int[] arr, int index)
    {
        if (arr == null || index < 0 || index >= arr.length) {
            return arr;
        }

        int[] anotherArray = new int[arr.length - 1];

        for (int i = 0, k = 0; i < arr.length; i++) {
            if (i == index) {
                continue;
            }
            anotherArray[k++] = arr[i];
        }
        return anotherArray;
    }

    private static void askIfStartNewGameOrQuit(String winnerName) {
        System.out.println("\n������� 'q' ��� ������ �� ���� � 'r' ����� ������ �������:");
        String playerEnter = "";
        while (playerEnter.length() == 0) {
            String uncheckedPlayerEnter = SCANNER.nextLine().trim();
            if (uncheckedPlayerEnter.length() == 0) {
                System.out.println("������� 'q' ��� ������ �� ���� � 'r' ����� ������ �������:");
            } else if(uncheckedPlayerEnter.equals("q") || uncheckedPlayerEnter.equals("r")){
                playerEnter = uncheckedPlayerEnter;
                handleUserQuitOrRestart(playerEnter);
            } else {
                System.out.println("������� 'q' ��� ������ �� ���� � 'r' ����� ������ �������:");
            }
        }
    }

    private static void displayWordToUser(String[] word) {
        System.out.println("============================");
        for (String i : word) {
            System.out.print(" " + i);
        }
        System.out.println();
        System.out.println("============================");
    }

    private static int[] shufflePlayers(Map<Integer, ArrayList> playersList) {
        int[] shuffledPlayersIndexes = new int[playersList.size()];

        for (int i = 0; i < playersList.size(); i++) {
            shuffledPlayersIndexes[i] = i;
        }

        Random rand = new Random();

        for (int i = 0; i < shuffledPlayersIndexes.length; i++) {
            int randomIndexToSwap = rand.nextInt(shuffledPlayersIndexes.length);
            int temp = shuffledPlayersIndexes[randomIndexToSwap];
            shuffledPlayersIndexes[randomIndexToSwap] = shuffledPlayersIndexes[i];
            shuffledPlayersIndexes[i] = temp;
        }

        return shuffledPlayersIndexes;
    }

    private static Map<Integer, ArrayList> getPlayersFromUsers() {
        Map<Integer, ArrayList> playersList = new HashMap<Integer, ArrayList>();
        ArrayList<String> playersNames = new ArrayList<String>();
        boolean stop = false;
        int counter = 0;

        while (!stop) {
            System.out.println("����� " + (counter + 1) + ", ������� ���� ���, ��� 's' ��� ������ ����:");
            String nameFromUser = SCANNER.nextLine().trim();
            handleUserQuitOrRestart(nameFromUser);
            if (nameFromUser.length() > 0) {
                if (nameFromUser.equals("s")) {
                    if (counter >= 2) {
                        stop = true;
                    } else {
                        System.out.println("������� ������ ���� ���� ��� ������!");
                        continue;
                    }
                } else if (playersNames.contains(nameFromUser)) {
                    System.out.println("��� ��� ��� ������");
                    continue;
                } else {
                    ArrayList currentPlayer = new ArrayList<>(Arrays.asList(nameFromUser, 0));
                    playersList.put(counter, currentPlayer);
                    playersNames.add(nameFromUser);
                    counter++;
                }
            } else {
                System.out.println("��� ������ �� ����� ���� ������!");
                continue;
            }
        }
        return playersList;
    }

    private static void handleUserQuitOrRestart(String userEnter) {
        if (userEnter.equals("q")) {
            System.out.println("\n�����...");
            System.exit(0);
        } else if (userEnter.equals("r")) {
            System.out.println("\n�������� �������...");
            startGame();
        }
    }

    private static int getRandomNumInRange(int max) {
        return (int) (Math.random() * (max + 1));
    }
}