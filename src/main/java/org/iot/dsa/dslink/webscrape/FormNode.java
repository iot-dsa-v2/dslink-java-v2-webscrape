package org.iot.dsa.dslink.webscrape;

import java.util.HashSet;
import java.util.Set;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FormNode extends ChildElementNode {
    
    public FormNode(QueryNode parent, int number) {
        super(parent, number);
    }
    
    public FormNode() {
        super();
    }
    
    @Override
    protected void init() {
        super.init();
        Elements inputs = getElement().select("input");
        put("Post Form", makePostFormAction(inputs));
    }

    private DSAction makePostFormAction(Elements inputs) {
        DSAction act = new DSAction() {
            @Override
            public void prepareParameter(DSInfo target, DSMap parameter) {
            }
            
            @Override
            public ActionResult invoke(DSInfo target, ActionInvocation request) {
                ((FormNode) target.get()).postForm(request.getParameters());
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
