package org.iot.dsa.dslink.webscrape;

import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;

public class QueryNode extends DSNode {
    
    private ElementNode parent;
    private String query;
    private DomNodeList<DomNode> elements;

    public QueryNode(ElementNode parent, String query) {
        this.parent = parent;
        this.query = query;
    }
    
    public QueryNode() {
        
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Refresh", makeRefreshAction());
    }
    
    private DSIObject makeRefreshAction() {
        DSAction act = new DSAction.Parameterless() {
            @Override
            public ActionResult invoke(DSInfo target, ActionInvocation request) {
                ((QueryNode) target.get()).init();
                return null;
            }
        };
        return act;
    }

    @Override
    protected void onStarted() {
        super.onStarted();
        DSIObject queryobj = get("CSS Query");
        if (queryobj instanceof DSString) {
            this.query = ((DSString) queryobj).toString();
        }
    }
    
    @Override
    protected void onStable() {
        init();
    }
    
    private void init() {
        if (parent == null) {
            parent = (ElementNode) getParent();
        }
        if (query != null) {
            put("CSS Query", query);
            DomNode parentElem = parent.getElement();
            if (parentElem == null) {
                return;
            }
            elements = parentElem.querySelectorAll(query);
            for (int i=0; i < elements.size(); i++) {
//                if (getElement(i).is("form")) {
//                    put(String.valueOf(i), new FormNode(this, i));
//                }
                put(String.valueOf(i), new ChildElementNode(this, i));
            }
        }
    }
    
    public DomNode getElement(int index) {
        if (elements != null && index < elements.size()) {
            return elements.get(index);
        } else {
            return null;
        }
    }
}
