package com.blogspot.jvalentino.usnrauto.component

import javax.swing.tree.DefaultMutableTreeNode;

class DownloadTreeNode extends DefaultMutableTreeNode {

	String url
	
	DownloadTreeNode(String name, String url) {
		super(name)
		this.url = url
	}
}
