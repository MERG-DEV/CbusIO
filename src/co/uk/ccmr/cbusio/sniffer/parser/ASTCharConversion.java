/* Generated By:JJTree: Do not edit this line. ASTCharConversion.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package co.uk.ccmr.cbusio.sniffer.parser;

public
class ASTCharConversion extends SimpleNode {
  public ASTCharConversion(int id) {
    super(id);
  }

  public ASTCharConversion(ScriptParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ScriptParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=6e1f7580d095b0bc7c684db94fd0d717 (do not edit this line) */