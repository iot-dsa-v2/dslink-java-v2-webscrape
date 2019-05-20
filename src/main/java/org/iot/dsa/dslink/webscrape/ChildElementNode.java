package org.iot.dsa.dslink.webscrape;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSMap.Entry;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ChildElementNode extends ElementNode {
    
    private QueryNode parent;
    private int number;
//    private Map<String, DomElement> buttons;
    
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
        DomNode me = getElement();
        if (me instanceof HtmlForm) {
            HtmlForm form = (HtmlForm) me;
            DomNodeList<HtmlElement> inputs = form.getElementsByTagName(HtmlInput.TAG_NAME);
//            DomNodeList<DomNode> submitButtons = form.querySelectorAll("[type=submit]");
//            if (!submitButtons.isEmpty()) {
                put("Post Form", makePostFormAction(inputs));
//            }
        }
    }

    @Override
    public DomNode getElement() {
        return parent.getElement(number);
    }
    
    private DSAction makePostFormAction(DomNodeList<HtmlElement> inputs) {
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
//        act.addDefaultParameter("POST URL", DSString.valueOf(getElement().attr("action")), null);
        Set<String> inpNames = new HashSet<String>();
        for (HtmlElement inputElem: inputs) {
            if (inputElem instanceof HtmlInput) {
                HtmlInput input = (HtmlInput) inputElem;
                String inpName = input.getNameAttribute();
                if (!inpName.isEmpty() && !inpNames.contains(inpName)) {
                    act.addDefaultParameter(inpName, DSString.valueOf(input.getValueAttribute()), null);
                    inpNames.add(inpName);
                }
            }
        }
//        buttons = new HashMap<String, DomElement>();
//        DSList texts = new DSList();
//        for (DomNode button: submitButtons) {
//            if (button instanceof DomElement) {
//                String text = button.asText();
//                buttons.put(text, (DomElement) button);
//                texts.add(text);
//            }
//        }
//        if (texts.isEmpty()) {
//            return null;
//        }
//        act.addParameter("Submit Button", DSFlexEnum.valueOf(texts.get(0).toString(), texts), null);
        return act;
    }
    
    private void postForm(DSMap parameters) {
        String name = parameters.remove("Document Name").toString();
//        String button = parameters.remove("Submit Button").toString();
        //String posturl = parameters.remove("POST URL").toString();
        //DocumentFetcher docparent = (DocumentFetcher) getAncestor(DocumentFetcher.class);
        put(name, new DocumentNodeFromForm(this, parameters.copy()));
    }
    
    public DocumentFetcher getDocParent() {
        return (DocumentFetcher) getAncestor(DocumentFetcher.class);
    }
    
    public HtmlPage submitForm(DSMap inputs) {
        DomNode me = getElement();
        if (!(me instanceof HtmlForm)) {
            return null;
        }
        HtmlForm form = (HtmlForm) me;
        for (Entry entry: inputs) {
            HtmlInput input = form.getInputByName(entry.getKey());
            input.setValueAttribute(entry.getValue().toString());
        }
        DomElement buttonElem = null;
        DomNodeList<DomNode> submitButtons = form.querySelectorAll("[type=submit]");
        for (DomNode dn: submitButtons) {
            if (dn instanceof DomElement) {
                buttonElem = (DomElement) dn;
                break;
            }
        }
        if (buttonElem == null) {
            return null;
        }
        try {
            return buttonElem.click();
        } catch (IOException e) {
            warn("", e);
            return null;
        }
    }

}
