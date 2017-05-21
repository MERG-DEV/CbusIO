/* Generated By:JavaCC: Do not edit this line. ScriptParserVisitor.java Version 5.0 */
package co.uk.ccmr.cbusio.sniffer.parser;

public interface ScriptParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTProgram node, Object data);
  public Object visit(ASTAction node, Object data);
  public Object visit(ASTInclude node, Object data);
  public Object visit(ASTReceive node, Object data);
  public Object visit(ASTMacro node, Object data);
  public Object visit(ASTBlock node, Object data);
  public Object visit(ASTStatement node, Object data);
  public Object visit(ASTIfStatement node, Object data);
  public Object visit(ASTPause node, Object data);
  public Object visit(ASTExecute node, Object data);
  public Object visit(ASTTx node, Object data);
  public Object visit(ASTTxrx node, Object data);
  public Object visit(ASTPrompt node, Object data);
  public Object visit(ASTType node, Object data);
  public Object visit(ASTAssign node, Object data);
  public Object visit(ASTMessage node, Object data);
  public Object visit(ASTTimeout node, Object data);
  public Object visit(ASTName node, Object data);
  public Object visit(ASTExpression node, Object data);
  public Object visit(ASTConditionalOrExpression node, Object data);
  public Object visit(ASTConditionalAndExpression node, Object data);
  public Object visit(ASTInclusiveOrExpression node, Object data);
  public Object visit(ASTExclusiveOrExpression node, Object data);
  public Object visit(ASTAndExpression node, Object data);
  public Object visit(ASTEqualityExpression node, Object data);
  public Object visit(ASTRelationalExpression node, Object data);
  public Object visit(ASTShiftExpression node, Object data);
  public Object visit(ASTAdditiveExpression node, Object data);
  public Object visit(ASTMultiplicativeExpression node, Object data);
  public Object visit(ASTUnaryExpression node, Object data);
  public Object visit(ASTPreIncrementExpression node, Object data);
  public Object visit(ASTPreDecrementExpression node, Object data);
  public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data);
  public Object visit(ASTPostfixExpression node, Object data);
  public Object visit(ASTPrimaryExpression node, Object data);
  public Object visit(ASTPrimaryPrefix node, Object data);
  public Object visit(ASTCharConversion node, Object data);
  public Object visit(ASTHex1Conversion node, Object data);
  public Object visit(ASTHex2Conversion node, Object data);
  public Object visit(ASTMid node, Object data);
  public Object visit(ASTPrimarySuffix node, Object data);
  public Object visit(ASTLiteral node, Object data);
}
/* JavaCC - OriginalChecksum=dab8a1bed838d6130317836ddd018a09 (do not edit this line) */