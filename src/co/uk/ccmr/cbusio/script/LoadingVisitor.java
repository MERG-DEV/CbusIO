/*
 * (c) Ian Hogg 2017
 */
/* This work is licensed under the:
      Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
   To view a copy of this license, visit:
      http://creativecommons.org/licenses/by-nc-sa/4.0/
   or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.

   License summary:
    You are free to:
      Share, copy and redistribute the material in any medium or format
      Adapt, remix, transform, and build upon the material

    The licensor cannot revoke these freedoms as long as you follow the license terms.

    Attribution : You must give appropriate credit, provide a link to the license,
                   and indicate if changes were made. You may do so in any reasonable manner,
                   but not in any way that suggests the licensor endorses you or your use.

    NonCommercial : You may not use the material for commercial purposes. **(see note below)

    ShareAlike : If you remix, transform, or build upon the material, you must distribute
                  your contributions under the same license as the original.

    No additional restrictions : You may not apply legal terms or technological measures that
                                  legally restrict others from doing anything the license permits.

   ** For commercial use, please contact the original copyright holder(s) to agree licensing terms

    This software is distributed in the hope that it will be useful, but WITHOUT ANY
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE

**************************************************************************************************************
  Note:   This source code has been written using a tab stop and indentation setting
          of 4 characters. To see everything lined up correctly, please set your
          IDE or text editor to the same settings.
*******************************************************************************************************/
package co.uk.ccmr.cbusio.script;

import co.uk.ccmr.cbusio.sniffer.parser.ASTAction;
import co.uk.ccmr.cbusio.sniffer.parser.ASTAdditiveExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTAndExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTAssign;
import co.uk.ccmr.cbusio.sniffer.parser.ASTBlock;
import co.uk.ccmr.cbusio.sniffer.parser.ASTCharConversion;
import co.uk.ccmr.cbusio.sniffer.parser.ASTConditionalAndExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTConditionalOrExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTEqualityExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTExclusiveOrExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTExecute;
import co.uk.ccmr.cbusio.sniffer.parser.ASTExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTHex1Conversion;
import co.uk.ccmr.cbusio.sniffer.parser.ASTHex2Conversion;
import co.uk.ccmr.cbusio.sniffer.parser.ASTIfStatement;
import co.uk.ccmr.cbusio.sniffer.parser.ASTInclude;
import co.uk.ccmr.cbusio.sniffer.parser.ASTInclusiveOrExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTLiteral;
import co.uk.ccmr.cbusio.sniffer.parser.ASTMacro;
import co.uk.ccmr.cbusio.sniffer.parser.ASTMessage;
import co.uk.ccmr.cbusio.sniffer.parser.ASTMid;
import co.uk.ccmr.cbusio.sniffer.parser.ASTMultiplicativeExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTName;
import co.uk.ccmr.cbusio.sniffer.parser.ASTPause;
import co.uk.ccmr.cbusio.sniffer.parser.ASTPostfixExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTPreDecrementExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTPreIncrementExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTPrimaryExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTPrimaryPrefix;
import co.uk.ccmr.cbusio.sniffer.parser.ASTPrimarySuffix;
import co.uk.ccmr.cbusio.sniffer.parser.ASTProgram;
import co.uk.ccmr.cbusio.sniffer.parser.ASTPrompt;
import co.uk.ccmr.cbusio.sniffer.parser.ASTReceive;
import co.uk.ccmr.cbusio.sniffer.parser.ASTRelationalExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTShiftExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTStatement;
import co.uk.ccmr.cbusio.sniffer.parser.ASTTimeout;
import co.uk.ccmr.cbusio.sniffer.parser.ASTTx;
import co.uk.ccmr.cbusio.sniffer.parser.ASTTxrx;
import co.uk.ccmr.cbusio.sniffer.parser.ASTType;
import co.uk.ccmr.cbusio.sniffer.parser.ASTUnaryExpression;
import co.uk.ccmr.cbusio.sniffer.parser.ASTUnaryExpressionNotPlusMinus;
import co.uk.ccmr.cbusio.sniffer.parser.ScriptParserVisitor;
import co.uk.ccmr.cbusio.sniffer.parser.SimpleNode;

public class LoadingVisitor implements ScriptParserVisitor {

	@Override
	public Object visit(SimpleNode node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTProgram node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTAction node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}
	
	@Override
	public Object visit(ASTInclude node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(ASTReceive node, Object data) {
		ScriptHandler h = (ScriptHandler) data;
		h.addReceiver(node);
		return null;
	}

	@Override
	public Object visit(ASTMacro node, Object data) {
		ScriptHandler h = (ScriptHandler) data;
		h.addMacro(node);
		return null;
	}

	@Override
	public Object visit(ASTBlock node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTStatement node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTIfStatement node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTPause node, Object data) {
		return null;
	}
	
	@Override
	public Object visit(ASTExecute node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTTx node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTTxrx node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTPrompt node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTType node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTAssign node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTMessage node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTTimeout node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTName node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTConditionalOrExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTConditionalAndExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTInclusiveOrExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTExclusiveOrExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTAndExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTEqualityExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTRelationalExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTShiftExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTAdditiveExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTMultiplicativeExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTUnaryExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTPreIncrementExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTPreDecrementExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTPostfixExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTPrimaryExpression node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTPrimaryPrefix node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTPrimarySuffix node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTLiteral node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTCharConversion node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTHex1Conversion node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTHex2Conversion node, Object data) {
		return null;
	}

	@Override
	public Object visit(ASTMid node, Object data) {
		return null;
	}

}
