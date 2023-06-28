// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.security.cert.TrustAnchor;

import static jminusminus.CLConstants.*;

/**
 * An AST node for a break-statement.
 */
public class JBreakStatement extends JStatement {
    JStatement s = null;
    /**
     * Constructs an AST node for a break-statement.
     *
     * @param line line in which the break-statement occurs in the source file.
     */
    public JBreakStatement(int line) {
        super(line);
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        // peek into enclosingStatement
        s = JMember.enclosingStatement.peek();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        if (s instanceof JDoStatement) {
            output.addBranchInstruction(GOTO, ((JDoStatement) s).breakLabel);
        } else if (s instanceof JWhileStatement) {
            output.addBranchInstruction(GOTO, ((JWhileStatement) s).breakLabel);
        } else if (s instanceof JForStatement) {
            output.addBranchInstruction(GOTO, ((JForStatement) s).breakLabel);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JBreakStatement:" + line, e);
    }
}
