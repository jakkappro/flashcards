package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class App {
    private final HashMap<String, String> cards;
    private final HashMap<String, Integer> stats;
    private boolean running;
    private final Scanner inputScanner;
    private final ArrayList<String> log;
    private final String importFile;
    private final String exportFile;

    public App(String importFile, String exportFile) {
        this.cards = new LinkedHashMap<>();
        this.stats = new LinkedHashMap<>();
        this.log = new ArrayList<>();
        this.running = true;
        this.inputScanner = new Scanner(System.in);
        this.importFile = importFile;
        this.exportFile = exportFile;

        if(!this.importFile.equals("")) {
            autoImport();
            System.out.printf("%d cards have been loaded.\n", cards.size());
        }
        run();
    }

    private void run() {
        while(this.running) {
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            var input = inputScanner.nextLine();
            log.add("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            log.add(input);
            switch (input) {
                case "exit":
                    System.out.println("Bye bye!");
                    this.running = false;
                    if (!exportFile.equals(""))
                        autoExport();
                    break;
                case "add":
                    addCard();
                    break;

                case "remove":
                    removeCard();
                    break;

                case "import":
                    importCards();
                    break;

                case "export":
                    exportCards();
                    break;

                case "ask":
                    askCard();
                    break;

                case "log":
                    log();
                    break;

                case "hardest card":
                    hardestCard();
                    break;

                case "reset stats":
                    resetStats();
                    break;
            }
        }
    }

    private void addCard() {
        System.out.println("The card:");
        log.add("The card:");
        var term = inputScanner.nextLine();
        log.add(term);
        if (cards.containsKey(term)) {
            System.out.println("The card \"" + term + "\" already exists.");
            log.add("The card \"" + term + "\" already exists.");
            return;
        }
        System.out.println("The definition of the card:");
        log.add("The definition of the card:");
        var definition = inputScanner.nextLine();
        log.add(definition);
        if (cards.containsValue(definition)) {
            System.out.println("The definition \"" + definition + "\" already exists.");
            log.add("The definition \"" + definition + "\" already exists.");
            return;
        }
        cards.put(term, definition);
        stats.put(term, 0);
        System.out.println("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
        log.add("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
    }

    private void removeCard() {
        System.out.println("Which card?");
        log.add("Which card?");
        var term = inputScanner.nextLine();
        log.add(term);
        if (cards.containsKey(term)) {
            cards.remove(term);
            stats.remove(term);
            System.out.println("The card has been removed.");
            log.add("The card has been removed.");
        } else {
            System.out.printf("Can't remove \"%s\": there is no such card.\n", term);
            log.add(String.format("Can't remove \"%s\": there is no such card.\n", term));
        }
    }

    private void askCard() {
        System.out.println("How many times to ask?");
        log.add("How many times to ask?");
        var numberOfCards = inputScanner.nextInt();
        log.add(String.valueOf(numberOfCards));
        var counter = 0;
        inputScanner.nextLine();
        for (var card : cards.entrySet()) {
            if (counter == numberOfCards)
                break;

            System.out.println("Print the definition of \"" + card.getKey() + "\"");
            log.add("Print the definition of \"" + card.getKey() + "\"");

            var answer = inputScanner.nextLine();
            log.add(answer);
            if (answer.equals(card.getValue())) {
                System.out.println("Correct!");
            } else {
                if (!cards.containsValue(answer)) {
                    System.out.println("Wrong. The right answer is \"" + card.getValue() + "\".");
                } else {
                    for (var answerCard : cards.entrySet()) {
                        if (answerCard.getValue().equals(answer)) {
                            System.out.println("Wrong. The right answer is \"" + card.getValue() + "\", but your definition is correct for \"" + answerCard.getKey() + "\".");
                            log.add("Wrong. The right answer is \"" + card.getValue() + "\", but your definition is correct for \"" + answerCard.getKey() + "\".");
                        }
                    }
                }
                stats.replace(card.getKey(), stats.get(card.getKey()) + 1);
            }

            counter++;
        }
    }

    private void importCards() {
        try (Scanner scanner = new Scanner(getFile())) {
            importing(scanner);
        }  catch (FileNotFoundException e) {
            System.out.println("File not found.");
            log.add("File not found.");
        }
    }

    private void exportCards() {
        try (FileWriter writer = new FileWriter(getFile())) {
            cards.forEach((k, v) -> {
                try {
                    writer.write(k + " : " + v + " : " + stats.get(k) + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.printf("%d cards have been saved.", cards.size());
            log.add(String.format("%d cards have been saved.", cards.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void autoExport() {
        try (FileWriter writer = new FileWriter(exportFile)) {
            cards.forEach((k, v) -> {
                try {
                    writer.write(k + " : " + v + " : " + stats.get(k) + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.printf("%d cards have been saved.", cards.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void autoImport() {
        try (Scanner scanner = new Scanner(new File(importFile))) {
            importing(scanner);
        }  catch (FileNotFoundException e) {
            System.out.println("File not found.");
            log.add("File not found.");
        }
    }

    private void importing(Scanner scanner) {
        var counter = 0;
        while (scanner.hasNextLine()) {
            var card = scanner.nextLine().split(" : ");
            cards.put(card[0], card[1]);
            stats.put(card[0], Integer.parseInt(card[2]));
            counter++;
        }
        System.out.printf("%d cards have been loaded.\n", counter);
        log.add(String.format("%d cards have been loaded.\n", counter));
    }

    private File getFile() {
        System.out.println("File name:");
        log.add("File name:");
        var path = inputScanner.next();
        log.add(path);
        return new File(path);
    }

    private void log() {
        var file = getFile();
        try(FileWriter writer = new FileWriter(file)) {
            log.forEach((n) -> {
                try {
                    writer.write(n + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("The log has been saved.");
            log.add("The log has been saved");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void hardestCard() {
        var values = stats.values().stream().sorted().collect(Collectors.toList());
        var max = values.size() > 0 ? values.get(values.size() - 1) : 0;

        var hardest = new ArrayList<String>();

        for (var card : stats.entrySet()) {
            if (Objects.equals(card.getValue(), max)) {
                hardest.add(card.getKey());
            }
        }

        if (max == 0) {
          System.out.println("There are no cards with errors.\n");
          log.add("There are no cards with errors.\n");
        } else if (hardest.size() == 1) {
            System.out.printf("The hardest card is \"%s\". You have %d errors answering it.\n", hardest.get(0), max);
            log.add(String.format("The hardest card is \"%s\". You have %d errors answering it.\n", hardest.get(0), max));
        } else {
            StringBuilder hardestCards = new StringBuilder();
            for (var card : hardest) {
                hardestCards.append("\"").append(card).append("\", ");
            }
            System.out.printf("The hardest cards are %s. You have %d errors answering them.\n", hardestCards.substring(0, hardestCards.length() - 2), max);
            log.add(String.format("The hardest cards are %s. You have %d errors answering them.\n", hardestCards.substring(0, hardestCards.length() - 2), max));
        }
    }

    private void resetStats() {
        stats.forEach((k, v) -> stats.put(k, 0));
        System.out.println("Card statistics have been reset.");
        log.add("Card statistics have been reset.");
    }
}
