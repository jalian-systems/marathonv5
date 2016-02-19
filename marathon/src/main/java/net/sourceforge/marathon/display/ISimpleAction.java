package net.sourceforge.marathon.display;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) public @interface ISimpleAction {

    String value() default "";

    String icon() default "";

    String iconDisabled() default "";

    String iconPressed() default "";

    char mneumonic() default 0;

    String action() default "";

    String description() default "";
}
