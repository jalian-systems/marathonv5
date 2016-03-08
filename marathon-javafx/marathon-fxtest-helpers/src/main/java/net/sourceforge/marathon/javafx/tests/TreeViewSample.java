/*
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.sourceforge.marathon.javafx.tests;

import java.io.File;

import ensemble.Sample;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;

/**
 * An implementation of the TreeView control displaying an expandable tree root
 * node.
 *
 * @see javafx.scene.control.TreeView
 */
public class TreeViewSample extends Sample {

	private TreeView<File> treeView;

	public TreeViewSample() {
		String dir = "./src";
		final CheckBoxTreeItem<File> treeRoot = buildRoot(dir);

		treeView = new TreeView<File>();
		treeView.setCellFactory(CheckBoxTreeCell.<File> forTreeView());
		treeView.setShowRoot(true);
		treeView.setRoot(treeRoot);
		treeRoot.setExpanded(true);

		getChildren().add(treeView);
	}

	private CheckBoxTreeItem<File> buildRoot(String dir) {
		return buildNode(new File(dir));
	}

	private CheckBoxTreeItem<File> buildNode(File file) {
		CheckBoxTreeItem<File> node = new CheckBoxTreeItem<File>(file);
		if (file.isDirectory()) {
			ObservableList<TreeItem<File>> children = node.getChildren();
			File[] listFiles = file.listFiles();
			for (File f : listFiles) {
				children.add(buildNode(f));
			}
		}
		return node;
	}

	public void printCells() {
		TreeItem<File> root = treeView.getRoot();
		printCell(root, "");
	}

	private void printCell(TreeItem<File> root, String indent) {
		System.out.println(indent + printUsingTreeCell(root));
		ObservableList<TreeItem<File>> children = root.getChildren();
		if (children == null)
			return;
		for (TreeItem<File> object : children) {
			printCell(object, indent + "    ");
		}
	}

	private String printUsingTreeCell(TreeItem<File> root) {
		TreeCell<File> call = (TreeCell<File>) treeView.getCellFactory().call(treeView);
		call.setItem(root.getValue());
		call.updateTreeView(treeView);
		return call.toString();
	}
}
