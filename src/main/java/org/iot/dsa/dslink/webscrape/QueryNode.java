package org.iot.dsa.dslink.webscrape;

import java.util.List;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class QueryNode extends DSNode {
    
    private ElementNode parent;
    private String query;
    private List<WebElement> elements;

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
            WebElement parentElem = parent.getElement();
            if (parentElem == null) {
                return;
            }
            elements = parentElem.findElements(By.cssSelector(query));
            for (int i=0; i < elements.size(); i++) {
//                if (getElement(i).is("form")) {
//                    put(String.valueOf(i), new FormNode(this, i));
//                }
                put(String.valueOf(i), new ChildElementNode(this, i));
            }
        }
    }
    
    public WebElement getElement(int index) {
        if (elements != null && index < elements.size()) {
            return elements.get(index);
        } else {
            return null;
        }
    }
}
