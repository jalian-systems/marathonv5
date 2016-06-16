package net.sourceforge.marathon.display.readline;

import jline.Completor;
import jline.History;

public class Readline {

    private History history;

    public Readline() {
        history = new History();
    }

    public Completor getCompletor() {
        return null;
    }

    public History getHistory() {
        return history;
    }

}
