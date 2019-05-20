package org.iot.dsa.dslink.webscrape;

import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;

public class DocumentNodeFromForm extends DocumentNode {
    
    private DSMap inputs;
    private ChildElementNode formParent;
//    private String button;
    
    public DocumentNodeFromForm(ChildElementNode formParent, DSMap inputs) {
        super(formParent.getDocParent(), "");
        this.inputs = inputs;
        this.formParent = formParent;
//        this.button = button;
    }
    
    public DocumentNodeFromForm() {
        super();
    }
    
    @Override
    protected void onStarted() {
        super.onStarted();
        DSIObject inputsobj = get("inputs");
        if (inputsobj instanceof DSMap) {
            inputs = (DSMap) inputsobj;
        }
//        DSIObject buttonObj = get("button");
//        if (buttonObj instanceof DSString) {
//            button = buttonObj.toString();
//        }
        formParent = (ChildElementNode) getAncestor(ChildElementNode.class);
    }

    @Override
    protected void init() {
        if (inputs != null) {
//            put("URL", url);
            put("inputs", inputs.copy());
//            put("button", button);
            page = formParent.submitForm(inputs);
        }
    }

}
