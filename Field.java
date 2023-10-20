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
            "консьерж@Человек, в чьи обязанности входят обеспечение постояльцев в гостинице или жильцов дома всем необходимым и создание для них комфортных условий.",
            1,
            "омбудсмен@Гражданское или в некоторых государствах должностное лицо, на которое возлагаются функции контроля соблюдения справедливости и интересов определённых граждан в деятельности органов исполнительной власти должностных лиц.",
            2,
            "анфилада@Ряд последовательно примыкающих друг к другу пространственных элементов, проёмы которых расположены на одной продольной оси.",
            3,
            "диверсификация@Проникновение специализированных (промышленных, транспортных, строительных) фирм в другие отрасли производства, изменение и расширение ассортимента выпускаемой продукции и видов предоставляемых услуг, переориентация рынков сбыта, освоение новых видов производств с целью повышения эффективности производства, получения экономической выгоды, предотвращения банкротства",
            4, "номинант@Тот, кто выдвигается на соискание какой-л. премии, титула или награды.",
            5,
            "манифест@Опубликованное заявление о намерениях, мотивах или взглядах эмитента, будь то физическое лицо, группа, политическая партия или правительство.",
            6,
            "триангуляция@Один из методов создания сети опорных геодезических пунктов, а также сама эта сеть. Заключается в геодезическом построении на местности системы пунктов, образующих треугольники, у которых измеряются все углы и длины некоторых базовых сторон",
            7, "адюльтер@Супружеская измена, неверность.",
            8,
            "квинтэссенция@Основа, самая сущность чего-н. [букв.: пятая стихия - эфир, признававшийся в средневековой философии основой прочих стихий].",
            9,
            "скафандр@Специальное снаряжение, предназначенное для изоляции человека от внешней среды. Части снаряжения образуют оболочку, непроницаемую для компонентов внешней среды.");

    public static void main(String[] args) {
        startGame();
    }

    private static void startGame() {

        System.out.println("""

                Добро пожаловать в Поле Чудес!
                (введите 'q' для выхода из игры и 'r' чтобы начать сначала в любой момент)
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
                        System.out.println("\nВы победили, " + contenderForWin + ", никто не угадал слово!");
                    } else {
                        System.out.println("\nПоздравляем, " + currentPlayerName + ", вы победили!");
                    }
                    askIfStartNewGameOrQuit(currentPlayerName);
                }

                int currentPlayerScore = (int) playersList.get(shuffledPlayersIndexes[i]).get(1);

                System.out.println("\n" + selectedWordAndDefinition[1]);
                System.out.println("\n" + currentPlayerName + ", ваш ход:");

                String playerQuess = "";

                while (playerQuess.length() == 0) {
                    String uncheckedPlayerQuess = SCANNER.nextLine().trim().toLowerCase();
                    if (uncheckedPlayerQuess.length() == 0) {
                        System.out.println("Введите букву или слово:");
                    } else if(String.join("", selectedWordHidden).contains(uncheckedPlayerQuess) && !isOnlyGuessByWord) {
                        System.out.println("Эта буква уже отгадана, попробуйте другую букву или слово:");
                    } else {
                        playerQuess = uncheckedPlayerQuess;
                    }
                }

                handleUserQuitOrRestart(playerQuess);

                if (playerQuess.length() == ONE_LETTER_LENGTH && isOnlyGuessByWord == false) {
                    if (selectedWordAndDefinition[0].contains(playerQuess)) {
                        System.out.println("\nБраво!");

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

                        System.out.println("\nУ вас " + currentPlayerScore + "/" + (halfOfMaxWordScore * 2) + " очков");
                        displayWordToUser(selectedWordHidden);

                        if(!String.join("", selectedWordHidden).contains("#")){
                            keepAskingPlayers = false;
                            System.out.println("\nПоздравляем, " + currentPlayerName + ", вы победили!");
                            askIfStartNewGameOrQuit(currentPlayerName);
                        }

                        if(currentPlayerScore > halfOfMaxWordScore) {
                            isOnlyGuessByWord = true;
                            System.out.println("\nУ вас слишком много очков, " + currentPlayerName + ", дайте другим угадать слово!");
                            contenderForWin = currentPlayerName;
                            continue;
                        }

                    } else {
                        System.out.println("\nДа ладно...");
                    }
                }
                else if(playerQuess.length() > 1 || isOnlyGuessByWord == true) {
                    if(currentPlayerName.equals(contenderForWin)){
                        keepAskingPlayers = false;
                        System.out.println("\nВы победили, " + contenderForWin + ", никто не угадал слово!");
                        askIfStartNewGameOrQuit(contenderForWin);
                    }
                    if(playerQuess.equals(selectedWordAndDefinition[0])){
                        keepAskingPlayers = false;
                        System.out.println("\nПоздравляем, " + currentPlayerName + ", вы победили!");
                        askIfStartNewGameOrQuit(currentPlayerName);
                    } else {
                        System.out.println("\nВы проиграли, "  + currentPlayerName + "...");
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
        System.out.println("\nВведите 'q' для выхода из игры и 'r' чтобы начать сначала:");
        String playerEnter = "";
        while (playerEnter.length() == 0) {
            String uncheckedPlayerEnter = SCANNER.nextLine().trim();
            if (uncheckedPlayerEnter.length() == 0) {
                System.out.println("Введите 'q' для выхода из игры и 'r' чтобы начать сначала:");
            } else if(uncheckedPlayerEnter.equals("q") || uncheckedPlayerEnter.equals("r")){
                playerEnter = uncheckedPlayerEnter;
                handleUserQuitOrRestart(playerEnter);
            } else {
                System.out.println("Введите 'q' для выхода из игры и 'r' чтобы начать сначала:");
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
            System.out.println("Игрок " + (counter + 1) + ", введите свое имя, или 's' для начала игры:");
            String nameFromUser = SCANNER.nextLine().trim();
            handleUserQuitOrRestart(nameFromUser);
            if (nameFromUser.length() > 0) {
                if (nameFromUser.equals("s")) {
                    if (counter >= 2) {
                        stop = true;
                    } else {
                        System.out.println("Игроков должно быть двое или больше!");
                        continue;
                    }
                } else if (playersNames.contains(nameFromUser)) {
                    System.out.println("Это имя уже занято");
                    continue;
                } else {
                    ArrayList currentPlayer = new ArrayList<>(Arrays.asList(nameFromUser, 0));
                    playersList.put(counter, currentPlayer);
                    playersNames.add(nameFromUser);
                    counter++;
                }
            } else {
                System.out.println("Имя игрока не может быть пустым!");
                continue;
            }
        }
        return playersList;
    }

    private static void handleUserQuitOrRestart(String userEnter) {
        if (userEnter.equals("q")) {
            System.out.println("\nВыход...");
            System.exit(0);
        } else if (userEnter.equals("r")) {
            System.out.println("\nНачинаем сначала...");
            startGame();
        }
    }

    private static int getRandomNumInRange(int max) {
        return (int) (Math.random() * (max + 1));
    }
}