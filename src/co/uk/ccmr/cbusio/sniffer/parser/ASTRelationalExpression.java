/* Generated By:JJTree: Do not edit this line. ASTRelationalExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package co.uk.ccmr.cbusio.sniffer.parser;

public
class ASTRelationalExpression extends SimpleNode {
  public ASTRelationalExpression(int id) {
    super(id);
  }

  public ASTRelationalExpression(ScriptParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ScriptParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=3d12c05f74e63dc1e183779d7e6c1044 (do not edit this line) */
