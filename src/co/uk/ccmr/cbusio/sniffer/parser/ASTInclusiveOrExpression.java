/* Generated By:JJTree: Do not edit this line. ASTInclusiveOrExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package co.uk.ccmr.cbusio.sniffer.parser;

public
class ASTInclusiveOrExpression extends SimpleNode {
  public ASTInclusiveOrExpression(int id) {
    super(id);
  }

  public ASTInclusiveOrExpression(ScriptParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ScriptParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=bbf4f410b49da6984e450135cebea8eb (do not edit this line) */
