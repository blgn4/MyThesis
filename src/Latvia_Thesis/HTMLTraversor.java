package Latvia_Thesis;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

class HTMLTraversor implements NodeVisitor {

	private StringBuilder accum = new StringBuilder(); // holds the accumulated
														// text
	private StringBuilder content = new StringBuilder(); // holds the actual
															// text
	private boolean isContent = true;

	// hit when the node is first seen
	public void head(Node node, int depth) {
		if (node instanceof TextNode) {
			String nodeText = ((TextNode) node).text();
			if (nodeText.length() > 50 || accum.toString().trim().length()>0){
				nodeText = nodeText.trim();
					append(nodeText);
			}
		}
	}

	// hit when all of the node's children (if any) have been visited
	public void tail(Node node, int depth) {
		String name = node.nodeName();
		if (name.equals("div")) {
			Node siblingNode = node.nextSibling();
			if (siblingNode == null && content.length() < accum.length()
					&& isContent) {
				this.content = accum;
				accum = new StringBuilder();
				if (content.length() > 300)
					isContent = false;
			}

		} else if (StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5"))
			append(" ");
		else if (name.equals("br"))
			append(" ");
	}

	// appends text to the string builder with a simple word wrap method
	private void append(String text) {
		// fits as is, without need to wrap text
		accum.append(text+" ");
	}

	public String toString() {
		if (content.length() == 0)
			return accum.toString();
		else
			return content.toString();
	}

}
