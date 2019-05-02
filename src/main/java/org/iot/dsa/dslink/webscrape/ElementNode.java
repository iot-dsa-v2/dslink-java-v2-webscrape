package org.iot.dsa.dslink.webscrape;

import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.action.DSActionValues;
import org.iot.dsa.node.action.ActionSpec.ResultType;
import org.jsoup.nodes.Element;

public abstract class ElementNode extends DSNode {
    private static DSAction getTextAction = makeGetTextAction();
    private static DSAction getHtmlAction = makeGetHtmlAction();
        
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Get Text", getTextAction);
        declareDefault("Get Html", getHtmlAction);
        declareDefault("Select Elements", makeSelectAction());
        declareDefault("Refresh", makeRefreshAction());
    }

    @Override
    protected void onStable() {
        super.onStable();
        init();
    }
    
    abstract protected void init();
    
    public abstract Element getElement();
    
    
    private static DSAction makeGetTextAction() {
        DSAction act = new DSAction.Parameterless() {
            
            @Override
            public ActionResult invoke(DSInfo target, ActionInvocation request) {
                return ((ElementNode) target.get()).getText();
            }
        };
        act.setResultType(ResultType.VALUES);
        act.addColumnMetadata("Text", DSValueType.STRING);
        return act;
    }
    
    private ActionResult getText() {
        String text = getElement().wholeText();
        return new DSActionValues(getTextAction).addResult(DSString.valueOf(text));
    }
    
    private static DSAction makeGetHtmlAction() {
        DSAction act = new DSAction.Parameterless() {
            
            @Override
            public ActionResult invoke(DSInfo target, ActionInvocation request) {
                return ((ElementNode) target.get()).getHtml();
            }
        };
        act.setResultType(ResultType.VALUES);
        act.addColumnMetadata("Html", DSValueType.STRING);
        return act;
    }
    
    private ActionResult getHtml() {
        String html = getElement().html();
        return new DSActionValues(getHtmlAction).addResult(DSString.valueOf(html));
    }
    
    private static DSAction makeSelectAction() {
        DSAction act = new DSAction() {
            @Override
            public void prepareParameter(DSInfo target, DSMap parameter) {
            }
            
            @Override
            public ActionResult invoke(DSInfo target, ActionInvocation request) {
                ((ElementNode) target.get()).select(request.getParameters());
                return null;
            }
        };
        act.addParameter("Name", DSValueType.STRING, null);
        act.addParameter("CSS Query", DSValueType.STRING, null);
        return act;
    }
    
    private void select(DSMap parameters) {
        String name = parameters.getString("Name");
        String query = parameters.getString("CSS Query");
        put(name, new QueryNode(this, query));
    }
    
    private static DSAction makeRefreshAction() {
        DSAction act = new DSAction.Parameterless() {
            @Override
            public ActionResult invoke(DSInfo target, ActionInvocation request) {
                ((ElementNode) target.get()).init();
                return null;
            }
        };
        return act;
    }
}
