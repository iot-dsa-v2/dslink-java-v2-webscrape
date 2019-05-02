package org.iot.dsa.dslink.webscrape;

import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.jsoup.nodes.Element;

public class ChildElementNode extends ElementNode {
    
    private QueryNode parent;
    private int number;
    
    public ChildElementNode(QueryNode parent, int number) {
        this.parent = parent;
        this.number = number;
    }
    
    public ChildElementNode() {
        
    }
    
    @Override
    protected void onStarted() {
        super.onStarted();
        DSIObject numobj = get("Number");
        if (numobj instanceof DSElement) {
            number = ((DSElement) numobj).toInt();
        }
    }
    
    
    @Override
    protected void init() {
        if (parent == null) {
            parent = (QueryNode) getParent();
        }
        put("Number", number);

    }

    @Override
    public Element getElement() {
        return parent.getElement(number);
    }

}
