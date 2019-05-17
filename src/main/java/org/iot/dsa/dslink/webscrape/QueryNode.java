package org.iot.dsa.dslink.webscrape;

import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QueryNode extends DSNode {
    
    private ElementNode parent;
    private String query;
    private Elements elements;

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
            elements = parent.getElement().select(query);
            for (int i=0; i < elements.size(); i++) {
//                if (getElement(i).is("form")) {
//                    put(String.valueOf(i), new FormNode(this, i));
//                }
                put(String.valueOf(i), new ChildElementNode(this, i));
            }
        }
    }
    
    public Element getElement(int index) {
        return elements.get(index);
    }
}
