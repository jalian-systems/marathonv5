package net.sourceforge.marathon.util;

import java.io.File;

public interface INameValidateChecker {

    boolean okToOverwrite(File file);

}
