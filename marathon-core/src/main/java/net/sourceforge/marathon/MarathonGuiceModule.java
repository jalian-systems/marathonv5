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
package net.sourceforge.marathon;

import java.util.Properties;
import java.util.logging.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.util.Providers;

import net.sourceforge.marathon.checklist.CheckListEditorProvider;
import net.sourceforge.marathon.display.AbstractGroupsPanel;
import net.sourceforge.marathon.display.Display.IDisplayProperties;
import net.sourceforge.marathon.display.FixtureSelector;
import net.sourceforge.marathon.display.IActionProvider;
import net.sourceforge.marathon.editor.IEditorProvider;
import net.sourceforge.marathon.editor.MultiEditorProvider;
import net.sourceforge.marathon.editor.ace.ACEEditorProvider;
import net.sourceforge.marathon.editor.html.HTMLEditorProvider;
import net.sourceforge.marathon.fx.display.IGroupTabPane;
import net.sourceforge.marathon.model.Group.FeaturesPanel;
import net.sourceforge.marathon.model.Group.GroupType;
import net.sourceforge.marathon.model.Group.IssuesPanel;
import net.sourceforge.marathon.model.Group.StoriesPanel;
import net.sourceforge.marathon.model.Group.SuitesPanel;
import net.sourceforge.marathon.providers.PlaybackResultProvider;
import net.sourceforge.marathon.providers.RecorderProvider;
import net.sourceforge.marathon.runtime.api.IMarathonRuntime;
import net.sourceforge.marathon.runtime.api.IRuntimeFactory;
import net.sourceforge.marathon.runtime.api.IScriptModel;
import net.sourceforge.marathon.runtime.api.ScriptModel;

public class MarathonGuiceModule extends AbstractModule {

    public static final Logger LOGGER = Logger.getLogger(MarathonGuiceModule.class.getName());

    protected MultiEditorProvider editorProvider;

    public MarathonGuiceModule() {
        editorProvider = new MultiEditorProvider();
        editorProvider.add(new HTMLEditorProvider(), false);
        IEditorProvider aceEditorProvider = new ACEEditorProvider();
        editorProvider.add(aceEditorProvider, true);
        CheckListEditorProvider marathonCheckListEditorProvider = new CheckListEditorProvider();
        editorProvider.add(marathonCheckListEditorProvider, false);
    }

    @Override
    protected void configure() {
        bind(Properties.class).annotatedWith(IDisplayProperties.class).toInstance(System.getProperties());
        bindRuntime();
        bind(RecorderProvider.class).toInstance(new RecorderProvider());
        bind(PlaybackResultProvider.class).toInstance(new PlaybackResultProvider());
        bind(IScriptModel.class).toInstance(ScriptModel.getModel());
        bind(IEditorProvider.class).toInstance(editorProvider);
        bind(FixtureSelector.class).toInstance(new FixtureSelector());
        bindActionProvider();
        bindPanels();
    }

    protected void bindActionProvider() {
        bind(IActionProvider.class).toInstance(new MarathonActionProvider(editorProvider));
    }

    protected void bindRuntime() {
        bind(IRuntimeFactory.class).toInstance(new IRuntimeFactory() {
            @Override
            public IMarathonRuntime createRuntime() {
                return null;
            }
        });
    }

    protected void bindPanels() {
        bind(AbstractGroupsPanel.class).annotatedWith(SuitesPanel.class).toInstance(new BlurbGroupsPanel(GroupType.SUITE));
        bind(AbstractGroupsPanel.class).annotatedWith(FeaturesPanel.class).toInstance(new BlurbGroupsPanel(GroupType.FEATURE));
        bind(AbstractGroupsPanel.class).annotatedWith(StoriesPanel.class).toInstance(new BlurbGroupsPanel(GroupType.STORY));
        bind(AbstractGroupsPanel.class).annotatedWith(IssuesPanel.class).toInstance(new BlurbGroupsPanel(GroupType.ISSUE));
        bind(IGroupTabPane.class).toProvider(Providers.of(null));
    }
}
