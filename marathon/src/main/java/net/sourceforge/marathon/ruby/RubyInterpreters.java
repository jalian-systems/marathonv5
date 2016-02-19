package net.sourceforge.marathon.ruby;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.jruby.Ruby;

public class RubyInterpreters {

    public static class RubySlot {
        private boolean used = false;
        private Ruby ruby;
    }

    static List<RubySlot> slots = new ArrayList<RubySlot>();

    public synchronized static Ruby get(Callable<Ruby> callable) {
        for (RubySlot slot : slots) {
            if (!slot.used) {
                slot.used = true;
                Logger.getLogger(RubyInterpreters.class.getName()).info("Reusing a interpreter: " + slot.ruby);
                return slot.ruby;
            }
        }
        RubySlot slot = new RubySlot();
        slot.used = true;
        try {
            slot.ruby = callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        slots.add(slot);
        Logger.getLogger(RubyInterpreters.class.getName()).info("Created a new interpreter");
        return slot.ruby;
    }

    public synchronized static void release(Ruby ruby) {
        for (RubySlot slot : slots) {
            if (slot.ruby == ruby) {
                slot.used = false;
                Logger.getLogger(RubyInterpreters.class.getName()).info("Releasing a interpreter: " + slot.ruby);
                return;
            }
        }
    }
}
