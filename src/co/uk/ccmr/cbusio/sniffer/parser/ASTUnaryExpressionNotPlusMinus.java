/* Generated By:JJTree: Do not edit this line. ASTUnaryExpressionNotPlusMinus.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package co.uk.ccmr.cbusio.sniffer.parser;

public
class ASTUnaryExpressionNotPlusMinus extends SimpleNode {
  public ASTUnaryExpressionNotPlusMinus(int id) {
    super(id);
  }

  public ASTUnaryExpressionNotPlusMinus(ScriptParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ScriptParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e38de29e076389479ea143cf911400a8 (do not edit this line) */