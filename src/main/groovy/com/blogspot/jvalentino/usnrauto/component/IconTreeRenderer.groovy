package com.blogspot.jvalentino.usnrauto.component

import java.awt.Component;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

class IconTreeRenderer extends DefaultTreeCellRenderer {

	private static final Icon app = 
		new ImageIcon(IconTreeRenderer.class.getResource("/icons/AppIcon-32x32.png"))
	private static final Icon download = 
		new ImageIcon(IconTreeRenderer.class.getResource("/icons/DownloadIcon-32x32.png"))
	private static final Icon help =
		new ImageIcon(IconTreeRenderer.class.getResource("/icons/HelpIcon-32x32.png"))
	

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
		
		super.getTreeCellRendererComponent(
			tree, value, sel, exp, leaf, row, hasFocus);
		
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      
		if (node instanceof NavTreeNode) {
			setIcon(app)
		} else if (node instanceof HtmlTreeNode) {
			setIcon(help)
		} else if (node instanceof DownloadTreeNode) {
			setIcon(download)
		} else {
			setIcon(this.getDefaultOpenIcon())
		}		
        
        return this;
    }

}
