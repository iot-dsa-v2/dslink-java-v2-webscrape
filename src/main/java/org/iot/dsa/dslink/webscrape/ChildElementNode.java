package org.iot.dsa.dslink.webscrape;

import java.util.HashSet;
import java.util.List;
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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

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
        WebElement me = getElement();
        if (me.getTagName().equals("form")) {
            List<WebElement> inputs = me.findElements(By.tagName("input"));
            put("Post Form", makePostFormAction(inputs));
        }
    }

    @Override
    public WebElement getElement() {
        return parent.getElement(number);
    }
    
    @Override
    public String getWindowHandle() {
        return getDocParent().getWindowHandle();
    }
    
    private DSAction makePostFormAction(List<WebElement> inputs) {
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
        for (WebElement inputElem: inputs) {
            String inpName = inputElem.getAttribute("name");
            if (inpName != null && !inpName.isEmpty() && !inpNames.contains(inpName)) {
                String inpVal = inputElem.getAttribute("value");
                act.addDefaultParameter(inpName, DSString.valueOf(inpVal != null ? inpVal : ""), null);
                inpNames.add(inpName);
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
    
    public DocumentNode getDocParent() {
        return (DocumentNode) getAncestor(DocumentNode.class);
    }
    
    public String submitForm(DSMap inputs) {
        WebElement me = getElement();
        DocumentNode docParent = getDocParent();
        String windowHandle = docParent.getWindowHandle();
        FirefoxDriver driver = docParent.getWebClient();
        synchronized(driver) {
            driver.switchTo().window(windowHandle);
            for (Entry entry: inputs) {
                WebElement input = me.findElement(By.cssSelector("input[name=" + entry.getKey() + "]"));
                try {
                    input.sendKeys(entry.getValue().toString());
                } catch (Exception e) {}
            }
            me.submit();
            docParent.init();
            return windowHandle;
        }
//        DomElement buttonElem = null;
//        DomNodeList<DomNode> submitButtons = form.querySelectorAll("[type=submit]");
//        for (DomNode dn: submitButtons) {
//            if (dn instanceof DomElement) {
//                buttonElem = (DomElement) dn;
//                break;
//            }
//        }
//        if (buttonElem == null) {
//            return null;
//        }
    }

}
