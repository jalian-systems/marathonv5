/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.ruby;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.jruby.Ruby;

public class RubyInterpreters {

    public static final Logger LOGGER = Logger.getLogger(RubyInterpreters.class.getName());

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

    public synchronized static void clear() {
        slots.clear();

    }
}
