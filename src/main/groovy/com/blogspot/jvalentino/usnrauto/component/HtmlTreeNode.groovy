package com.blogspot.jvalentino.usnrauto.component

import javax.swing.tree.DefaultMutableTreeNode;

class HtmlTreeNode extends DefaultMutableTreeNode {

	String url
	
	HtmlTreeNode(String name, String url) {
		super(name)
		this.url = url
	}
}
