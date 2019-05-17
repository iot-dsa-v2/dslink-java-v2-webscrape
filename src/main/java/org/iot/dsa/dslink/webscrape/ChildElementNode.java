package org.iot.dsa.dslink.webscrape;

import java.util.HashSet;
import java.util.Set;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
        Element me = getElement();
        if (me.is("form")) {
            Elements inputs = getElement().select("input");
            put("Post Form", makePostFormAction(inputs));
        }
    }

    @Override
    public Element getElement() {
        return parent.getElement(number);
    }
    
    private DSAction makePostFormAction(Elements inputs) {
        DSAction act = new DSAction() {
            @Override
            public void prepareParameter(DSInfo target, DSMap parameter) {
            }
            
            @Override
            public ActionResult invoke(DSInfo target, ActionInvocation request) {
                ((ChildElementNode) target.get()).postForm(request.getParameters());
                return null;
            }
        };
        act.addParameter("Document Name", DSValueType.STRING, null);
        act.addDefaultParameter("POST URL", DSString.valueOf(getElement().attr("action")), null);
        Set<String> inpNames = new HashSet<String>();
        for (Element input: inputs) {
            String inpName = input.attr("name");
            if (!inpName.isEmpty() && !inpNames.contains(inpName)) {
                act.addDefaultParameter(input.attr("name"), DSString.valueOf(input.attr("value")), null);
                inpNames.add(inpName);
            }
        }
        return act;
    }
    
    private void postForm(DSMap parameters) {
        String name = parameters.remove("Document Name").toString();
        String posturl = parameters.remove("POST URL").toString();
        DocumentFetcher docparent = (DocumentFetcher) getAncestor(DocumentFetcher.class);
        put(name, new DocumentNodeFromForm(docparent, posturl, parameters.copy()));
    }

}
