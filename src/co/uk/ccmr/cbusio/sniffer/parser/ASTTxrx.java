/* Generated By:JJTree: Do not edit this line. ASTTxrx.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package co.uk.ccmr.cbusio.sniffer.parser;

public
class ASTTxrx extends SimpleNode {
  public ASTTxrx(int id) {
    super(id);
  }

  public ASTTxrx(ScriptParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ScriptParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=a9a02cdaef6fd899ff2b75a878d6cc0d (do not edit this line) */