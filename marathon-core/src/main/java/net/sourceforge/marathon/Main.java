/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon;

import java.util.logging.Logger;

public class Main {

    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        boolean isDemo = false;
        boolean isBatch = false;

        for (String arg : args) {
            if (arg.equals("-batch") || arg.equals("-b"))
                isBatch = true;
            if (arg.equals("-demo"))
                isDemo = true;
        }
        if (isBatch && !isDemo)
            RealMain.realmain(args);
        else
            GUIMain.main(args);
    }

}
