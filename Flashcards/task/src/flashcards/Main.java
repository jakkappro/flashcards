package flashcards;

public class Main {
    public static void main(String[] args) {
        var importFile = "";
        var exportFile = "";
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-import"))
                importFile = args[i + 1];
            else if (args[i].equals("-export"))
                exportFile = args[i + 1];
        }
            var App = new App(importFile, exportFile);
    }
}
