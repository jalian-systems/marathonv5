package net.sourceforge.marathon.screencapture;

import net.sourceforge.marathon.screencapture.ImagePanel.Annotation;

public interface IAnnotationListener {

    void annotationRemoved();

    void annotationSelected(Annotation selectedAnnotation);

    void annotationAdded(Annotation selectedAnnotation);

}
