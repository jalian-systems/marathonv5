package net.sourceforge.marathon.javafxrecorder.component;

import java.lang.reflect.Constructor;
import java.util.LinkedList;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXComponentFactory {
	private JSONOMapConfig omapConfig;

	private static class InstanceCheckFinder implements IRFXComponentFinder {
		private Class<? extends Node> componentKlass;
		private Class<? extends RFXComponent> rComponentKlass;
		private IRecordOn recordOn;

		public InstanceCheckFinder(Class<? extends Node> componentKlass, Class<? extends RFXComponent> javaElementKlass,
				IRecordOn recordOn) {
			this.componentKlass = componentKlass;
			this.rComponentKlass = javaElementKlass;
			this.recordOn = recordOn;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.sourceforge.marathon.javaagent.IJavaElementFinder#get(java.awt
		 * .Node)
		 */
		@Override
		public Class<? extends RFXComponent> get(Node component) {
			if (componentKlass.isInstance(component))
				return rComponentKlass;
			return null;
		}

		@Override
		public Node getRecordOn(Node component, Point2D point) {
			if (recordOn != null)
				return recordOn.getRecordOn(component, point);
			return null;
		}
	}

	private static LinkedList<IRFXComponentFinder> entries = new LinkedList<IRFXComponentFinder>();

	static {
	}

	public static void add(Class<? extends Node> componentKlass, Class<? extends RFXComponent> rComponentKlass,
			IRecordOn recordOn) {
		add(new InstanceCheckFinder(componentKlass, rComponentKlass, recordOn));
	}

	public static void add(IRFXComponentFinder f) {
		entries.addFirst(f);
	}

	public static void reset() {
		entries.clear();
		add(Node.class, RFXUnknownComponent.class, null);
		add(TextInputControl.class, RFXTextInputControl.class, null);
		add(TreeView.class, RFXTreeView.class, new IRecordOn() {
			@Override
			public Node getRecordOn(Node component, Point2D point) {
				if (hasTreeCellParent(component)) {
					Node parent = component;
					while (parent != null) {
						if (parent instanceof TreeView)
							return parent;
						parent = parent.getParent();
					}
				}
				return null;
			}

			private boolean hasTreeCellParent(Node component) {
				Node parent = component;
				while (parent != null) {
					if (parent instanceof TreeCell<?>)
						return true;
					parent = parent.getParent();
				}
				return false;
			}
		});
	}

	static {
		reset();
	}

	public RFXComponentFactory(JSONOMapConfig objectMapConfiguration) {
		this.omapConfig = objectMapConfiguration;
	}

	public RFXComponent findRComponent(Node parent, Point2D point, IJSONRecorder recorder) {
		return findRawRComponent(getComponent(parent, point), point, recorder);
	}

	public RFXComponent findRawRComponent(Node source, Point2D point, IJSONRecorder recorder) {
		for (IRFXComponentFinder entry : entries) {
			Class<? extends RFXComponent> k = entry.get(source);
			if (k == null)
				continue;
			try {
				Constructor<? extends RFXComponent> cons = k.getConstructor(Node.class, JSONOMapConfig.class,
						Point2D.class, IJSONRecorder.class);
				return cons.newInstance(source, omapConfig, point, recorder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Node getComponent(Node component, Point2D point) {
		for (IRFXComponentFinder entry : entries) {
			Node recordOn = entry.getRecordOn(component, point);
			if (recordOn != null)
				return recordOn;
		}
		return component;

	}
}