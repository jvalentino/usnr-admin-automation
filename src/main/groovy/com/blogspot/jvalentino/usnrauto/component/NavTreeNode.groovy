package com.blogspot.jvalentino.usnrauto.component

import javax.swing.tree.DefaultMutableTreeNode;

class NavTreeNode extends DefaultMutableTreeNode {

	Class<?> navClass
	
	NavTreeNode(String name, Class<?> navClass) {
		super(name)
		this.navClass = navClass
	}
}
