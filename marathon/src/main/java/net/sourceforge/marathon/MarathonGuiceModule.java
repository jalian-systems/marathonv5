package net.sourceforge.marathon;

import java.util.Properties;

import net.sourceforge.marathon.display.Display.IDisplayProperties;
import net.sourceforge.marathon.display.FixtureSelector;
import net.sourceforge.marathon.display.IActionProvider;
import net.sourceforge.marathon.editor.IEditorProvider;
import net.sourceforge.marathon.editor.MultiEditorProvider;
import net.sourceforge.marathon.editor.rsta.RSTAEditorProvider;
import net.sourceforge.marathon.providers.PlaybackResultProvider;
import net.sourceforge.marathon.providers.RecorderProvider;
import net.sourceforge.marathon.runtime.api.IMarathonRuntime;
import net.sourceforge.marathon.runtime.api.IRuntimeFactory;
import net.sourceforge.marathon.runtime.api.IScriptModel;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.suite.editor.SuiteEditorProvider;

import com.google.inject.AbstractModule;

public class MarathonGuiceModule extends AbstractModule {

    protected MultiEditorProvider editorProvider;

    public MarathonGuiceModule() {
        editorProvider = new MultiEditorProvider();
        IEditorProvider rstaEditorProvider = new RSTAEditorProvider();
        editorProvider.add(rstaEditorProvider, true);

        SuiteEditorProvider suiteEditorProvider = new SuiteEditorProvider();
        editorProvider.add(suiteEditorProvider, false);
    }

    @Override protected void configure() {
        bind(Properties.class).annotatedWith(IDisplayProperties.class).toInstance(System.getProperties());
        bindRuntime();
        bind(RecorderProvider.class).toInstance(new RecorderProvider());
        bind(PlaybackResultProvider.class).toInstance(new PlaybackResultProvider());
        bind(IScriptModel.class).toInstance(ScriptModel.getModel());
        bind(IEditorProvider.class).toInstance(editorProvider);
        bind(FixtureSelector.class).toInstance(new FixtureSelector());
        bindActionProvider();
    }

    protected void bindActionProvider() {
        bind(IActionProvider.class).toInstance(new MarathonActionProvider(editorProvider));
    }

    protected void bindRuntime() {
        bind(IRuntimeFactory.class).toInstance(new IRuntimeFactory() {
            @Override public IMarathonRuntime createRuntime() {
                return null;
            }
        });
    }
}
