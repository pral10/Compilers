// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a for-statement.
 */
class JForStatement extends JStatement {
    // Initialization.
    private ArrayList<JStatement> init;

    // Test expression
    private JExpression condition;

    // Update.
    private ArrayList<JStatement> update;

    // The body.
    private JStatement body;

    // For Break Statemnt
    public String breakLabel;

    // For Continue Statement
    public String continueLabel;


    /**
     * Constructs an AST node for a for-statement.
     *
     * @param line      line in which the for-statement occurs in the source file.
     * @param init      the initialization.
     * @param condition the test expression.
     * @param update    the update.
     * @param body      the body.
     */
    public JForStatement(int line, ArrayList<JStatement> init, JExpression condition,
                         ArrayList<JStatement> update, JStatement body) {
        super(line);
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    /**
     * {@inheritDoc}
     */
    public JForStatement analyze(Context context) {

        JMember.enclosingStatement.push(this);
        LocalContext locContext = new LocalContext(context);

        if (init != null ) {
            ArrayList<JStatement> newInit = new ArrayList<>();
            for (JStatement i : init) {
                i = (JStatement) i.analyze(locContext);
                newInit.add(i);
            }
            this.init = newInit;
        }
        // Analyze  condition and check if it's boolean
        if (condition != null)
            condition = condition.analyze(locContext);

        // Condition should only have one expression

        // Now for Update

        if (update != null) {
            ArrayList<JStatement> newUpdate = new ArrayList<>();
            for (JStatement u : update) {
                u = (JStatement) u.analyze(locContext);
                newUpdate.add(u);
            }
            this.update = newUpdate;
        }
        // Body
        if (body != null)
            body = (JStatement) body.analyze(locContext);

        JMember.enclosingStatement.pop();


        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        String conditionalLabel = output.createLabel();
        breakLabel = output.createLabel();
        continueLabel = output.createLabel();


        // Generate codes for all the statements in intialization for(init;;)
        if (init != null) {
            for (JStatement i : init) {
                i.codegen(output);
            }
        }

       
        output.addLabel(conditionalLabel);
        if (condition != null)
            condition.codegen(output, breakLabel, false);

     
        body.codegen(output);
        output.addLabel(continueLabel);

        if (update != null) {
            for (JStatement u : update) {
                u.codegen(output);
            }
        }
        output.addBranchInstruction(GOTO, conditionalLabel);
        output.addLabel(breakLabel);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JForStatement:" + line, e);
        if (init != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Init", e1);
            for (JStatement stmt : init) {
                stmt.toJSON(e1);
            }
        }
        if (condition != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Condition", e1);
            condition.toJSON(e1);
        }
        if (update != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Update", e1);
            for (JStatement stmt : update) {
                stmt.toJSON(e1);
            }
        }
        if (body != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Body", e1);
            body.toJSON(e1);
        }
    }
}
