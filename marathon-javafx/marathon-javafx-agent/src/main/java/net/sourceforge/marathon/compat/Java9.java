package net.sourceforge.marathon.compat;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.stream.Collectors;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.TransformationList;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Java9 {

    public static class CastedList<E, F> extends TransformationList<E, F> {

        protected CastedList(ObservableList<? extends F> source) {
            super(source);
        }

        @SuppressWarnings("unchecked") @Override protected void sourceChanged(Change<? extends F> c) {
            beginChange();
            while (c.next()) {
                if (c.wasPermutated()) {
                    int from = c.getFrom();
                    int to = c.getTo();
                    int[] perm = new int[to - from];
                    for (int i = from; i < to; i++) {
                        perm[i] = c.getPermutation(i);
                    }
                    nextPermutation(from, to, perm);
                } else if (c.wasUpdated()) {
                    for (int pos = c.getFrom(); pos < c.getTo(); pos++)
                        nextUpdate(pos);
                } else {
                    if (c.wasAdded())
                        nextAdd(c.getFrom(), c.getTo());
                    if (c.wasRemoved()) {
                        nextRemove(c.getFrom(), c.getRemoved().stream().map((f) -> {
                            return (E) f;
                        }).collect(Collectors.toList()));
                    }
                }
            }
            endChange();
        }

        @Override public int getSourceIndex(int index) {
            return index;
        }

        public int getViewIndex(int index) {
            return index;
        }

        @SuppressWarnings("unchecked") @Override public E get(int index) {
            return (E) getSource().get(index);
        }

        @Override public int size() {
            return getSource().size();
        }

    }

    private static ObservableList<Window> windows;
    private static CastedList<Stage, Window> stages;

    public static ObservableList<Stage> getStages() {
        if(stages != null)
            return stages;
        stages = new CastedList<Stage, Window>(new FilteredList<>(getWindows_internal(), w -> w instanceof Stage));
        return stages;
    }

    @SuppressWarnings("unchecked") private static ObservableList<Window> getWindows_internal() {
        if (windows != null)
            return windows;
        try {
            windows = (ObservableList<Window>) Window.class.getMethod("getWindows").invoke(null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
        }
        return windows;
    }

    public static Iterator<Window> getWindows() {
        return getWindows_internal().iterator();
    }

    public static String getChar(KeyCode keyCode) {
        try {
            return (String) KeyCode.class.getMethod("getChar").invoke(keyCode);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getCode(KeyCode keyCode) {
        try {
            return (int) KeyCode.class.getMethod("getCode").invoke(keyCode);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static String getUrl(Image image) {
        try {
            return (String) Image.class.getMethod("getUrl").invoke(image);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

}
