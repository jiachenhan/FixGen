package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;

public class LambdaExpr extends ExprNode {
    public LambdaExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }
}
