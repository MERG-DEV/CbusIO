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

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.StyledDocument;

import co.uk.ccmr.cbus.driver.CbusDriver;
import co.uk.ccmr.cbus.sniffer.CbusEvent;
import co.uk.ccmr.cbus.sniffer.InvalidEventException;
import co.uk.ccmr.cbus.sniffer.Util;
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

public class ScriptInterpreter implements ScriptParserVisitor {
	
	private CbusDriver driver;
	private JFrame frame;
	private Variables variables;
	private MessageSource ms;
	private ScriptHandler scriptHandler;
	private StyledDocument log;

	public ScriptInterpreter(CbusDriver driver, ScriptHandler scriptHandler, MessageSource ms, JFrame frame, CbusEvent rx, StyledDocument log) {
		this.driver = driver;
		this.frame = frame;
		this.scriptHandler = scriptHandler;
		this.log = log;
		variables = new Variables();
		if (rx != null) {
			variables.put("this", new Value(rx.toString()));
		}
		this.ms = ms;
	}

	@Override
	public Object visit(SimpleNode node, Object data) {
		System.out.println("Shouldn't call Script Interpreter on a SimpleNode");
		return null;
	}

	@Override
	public Object visit(ASTProgram node, Object data) {
		System.out.println("Shouldn't call Script Interpreter on a Program");
		return null;
	}

	@Override
	public Object visit(ASTAction node, Object data) {
		System.out.println("Shouldn't call Script Interpreter on an Action");
		return null;
	}
	
	@Override
	public Object visit(ASTInclude node, Object data) {
		scriptHandler.load(new File((String)node.jjtGetValue()), log);
		data = node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTReceive node, Object data) {
		data = node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTMacro node, Object data) {
		//System.out.println("Visit Macro");
		data = node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTBlock node, Object data) {
		//System.out.println("Visit Block");
		data = node.childrenAccept(this, data);
		return data;
	}

	@Override
	public Object visit(ASTStatement node, Object data) {
		//System.out.println("Visit Statement");
		data = node.childrenAccept(this, data);
		return data;
	}
	
	// Expression, Block, Block
	@Override
	public Object visit(ASTIfStatement node, Object data) {
		Value test = (Value) node.jjtGetChild(0).jjtAccept(this, data);
		if (test.asBoolean()) {
			data = node.jjtGetChild(1).jjtAccept(this, data);
		} else {
			// check there is an else statement
			if (node.jjtGetNumChildren() == 3) {
				data = node.jjtGetChild(2).jjtAccept(this, data);
			}
		}
		return data;
	}

	@Override
	public Object visit(ASTPause node, Object data) {
		//System.out.println("Visit Pause");
		Value delay = (Value) node.jjtGetChild(0).jjtAccept(this, data);
		try {
			Thread.sleep(delay.asInt());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	@Override
	public Object visit(ASTExecute node, Object data) {
		//System.out.println("Visit Execute");
		Value cmd = (Value) node.jjtGetChild(0).jjtAccept(this, data);
		// Execute
		String cmdString = cmd.asString();
		
		
		String os = System.getProperty("os.name").toLowerCase();
		System.out.println("os="+os);
		Runtime rt = Runtime.getRuntime();
		Process p = null;
		try {
			if (os.startsWith("windows")) {
				System.out.println("Running:"+cmdString);
				p = rt.exec(new String[] {"cmd /c "+cmdString});
			} else if (os.startsWith("linux")) {
				System.out.println("Running:"+cmdString);
				p = rt.exec(new String[] {"sh", "-c", cmdString});
			} else {
				// others run directly
				System.out.println("Running:"+cmdString);
				p = rt.exec(cmdString);
			}
			p.getOutputStream().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
			
		Thread stdoutReader = new ReaderThread(p.getInputStream());
		stdoutReader.start();
		Thread stderrReader = new ReaderThread(p.getErrorStream());
		stderrReader.start();
		int ret=0;
		try {
			ret = p.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Command returned "+ret);
		try {
			stdoutReader.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			stderrReader.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Object visit(ASTTx node, Object data) {
		//System.out.println("Visit Tx");
		Value txMessage = (Value) node.jjtGetChild(0).jjtAccept(this, data);
		try {
			CbusEvent cmd = new CbusEvent(txMessage.asString());
			driver.queueForTransmit(cmd);
		} catch (InvalidEventException e) {
			System.out.println("Invalid event specification "+e);
		}
		return null;
	}

	@Override
	public Object visit(ASTTxrx node, Object data) {
		//System.out.println("Visit Txrx");
		// Set up receiver

		
		// send message
		Value txMessage = (Value) node.jjtGetChild(0).jjtAccept(this, data);
		CbusEvent ce;
		try {
			ce = new CbusEvent(txMessage.asString());
			//System.out.println("Sending "+ce);
			driver.queueForTransmit(ce);
		} catch (InvalidEventException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Get filter
		Value rxFilter = (Value) node.jjtGetChild(1).jjtAccept(this, data);
		Pattern pattern = Pattern.compile(rxFilter.asString());
		Value timeout = (Value) node.jjtGetChild(3).jjtAccept(this, data);
		long endTime = System.currentTimeMillis()+timeout.asInt();
		do {
			CbusEvent rce = ms.getEvent(endTime - System.currentTimeMillis());
//System.out.println("Received "+rce);
			if (System.currentTimeMillis() >= endTime) {
				// timeout
				node.jjtGetChild(4).jjtAccept(this, data);
				return null;
			}
			if (rce == null) {
				System.out.println("GOT null before timeout");
			} else {
				System.out.println("checking "+rce.toString()+" against:"+rxFilter.asString());
				Matcher m = pattern.matcher(rce.toString());
				if (m.matches()) {
					// run the block
					variables.put("this", new Value(rce.toString()));
					node.jjtGetChild(2).jjtAccept(this, data);
					break;
				}
			}
				
		} while (true);
		return null;
	}

	@Override
	public Object visit(ASTPrompt node, Object data) {
		//System.out.println("Visit Prompt");
		String type = (String) node.jjtGetChild(0).jjtAccept(this, data);
		Value message = (Value) node.jjtGetChild(1).jjtAccept(this, data);
		System.out.println("Message="+message.asString());
		String [] lines = message.asString().split("\\\\n");
		System.out.println("lines.legth="+lines.length);
		if("ok".equals(type)) {
			JOptionPane.showMessageDialog(frame, lines);
			return new Value("ok");
		} else
		if("yesno".equals(type)) {
			if (JOptionPane.showConfirmDialog(frame, lines, "prompt", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				return new Value("yes");
			}
			return new Value("no");
		} else
		if("string".equals(type)) {
			String in = JOptionPane.showInputDialog(frame, lines);
			if (in == null) {
				return new Value("");
			}
			return new Value(in);
		} else
		if("int".equals(type)) {
			String in = JOptionPane.showInputDialog(frame, lines);
			if (in == null) {
				return new Value(0);
			}
			return new Value(Integer.parseInt(in, 16));
		}
		return null;
	}

	@Override
	public Object visit(ASTType node, Object data) {
		return (String) node.jjtGetValue();
	}

	@Override
	public Object visit(ASTAssign node, Object data) {
		String varname = (String)node.jjtGetValue();
		Value value = (Value) node.jjtGetChild(0).jjtAccept(this, data);
		variables.put(varname, value);
		return null;
	}

	@Override
	public Object visit(ASTMessage node, Object data) {
		return (Value) node.jjtGetChild(0).jjtAccept(this, data);
	}

	@Override
	public Object visit(ASTTimeout node, Object data) {
		return (Value) node.jjtGetChild(0).jjtAccept(this, data);
	}

	@Override
	public Object visit(ASTName node, Object data) {
		return variables.get((String)node.jjtGetValue());
	}

	@Override
	public Object visit(ASTExpression node, Object data) {
		return (Value) node.jjtGetChild(0).jjtAccept(this, data);
	}

	@Override
	public Object visit(ASTConditionalOrExpression node, Object data) {
		Value v = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		if (node.jjtGetNumChildren() == 1) return v;
		Boolean ret = v.asBoolean();
		for (int i=1; i<node.jjtGetNumChildren(); i++) {
			if (ret) return new Value(true);
			v = (Value)(node.jjtGetChild(i).jjtAccept(this, data));
			ret = v.asBoolean();
		}
		return new Value(ret);
	}

	@Override
	public Object visit(ASTConditionalAndExpression node, Object data) {
		Value v = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		if (node.jjtGetNumChildren() == 1) return v;
		boolean ret = v.asBoolean();
		for (int i=1; i<node.jjtGetNumChildren(); i++) {
			if (ret == false) return new Value(false);
			v = (Value)(node.jjtGetChild(i).jjtAccept(this, data));
			ret = v.asBoolean();
		}
		return new Value(ret);
	}

	@Override
	public Object visit(ASTInclusiveOrExpression node, Object data) {
		Value v = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		if (node.jjtGetNumChildren() == 1) return v;
		int ret = v.asInt();
		for (int i=1; i<node.jjtGetNumChildren(); i++) {
			v = (Value)(node.jjtGetChild(i).jjtAccept(this, data));
			ret |= v.asInt();
		}
		return new Value(ret);
	}

	@Override
	public Object visit(ASTExclusiveOrExpression node, Object data) {
		Value v = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		if (node.jjtGetNumChildren() == 1) return v;
		int ret = v.asInt();
		for (int i=1; i<node.jjtGetNumChildren(); i++) {
			v = (Value)(node.jjtGetChild(i).jjtAccept(this, data));
			ret ^= v.asInt();
		}
		return new Value(ret);
	}

	@Override
	public Object visit(ASTAndExpression node, Object data) {
		Value v = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		if (node.jjtGetNumChildren() == 1) return v;
		int ret = v.asInt();
		for (int i=1; i<node.jjtGetNumChildren(); i++) {
			v = (Value)(node.jjtGetChild(i).jjtAccept(this, data));
			ret &= v.asInt();
		}
		return new Value(ret);
	}

	@Override
	public Object visit(ASTEqualityExpression node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));

		if (node.jjtGetNumChildren() > 1) {
			Value rhs = (Value)(node.jjtGetChild(1).jjtAccept(this, data));
			if ("==".equals(node.jjtGetValue())) {
				if (lhs.asString().equals(rhs.asString())) {
					return new Value(true);
				}
				return new Value(false);
			}
			if ("!=".equals(node.jjtGetValue())) {
				if (lhs.asString().equals(rhs.asString())) {
					return new Value(false);
				}
				return new Value(true);
			}
			return null;
		}
		return lhs;
	}

	@Override
	public Object visit(ASTRelationalExpression node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		if (node.jjtGetNumChildren() > 1) {
			Value rhs = (Value)(node.jjtGetChild(1).jjtAccept(this, data));
			if ("<".equals(node.jjtGetValue())) {
				return new Value(lhs.asInt() < rhs.asInt());
			}
			if (">".equals(node.jjtGetValue())) {
				return new Value(lhs.asInt() > rhs.asInt());
			}
			if ("<=".equals(node.jjtGetValue())) {
				return new Value(lhs.asInt() <= rhs.asInt());
			}
			if (">=".equals(node.jjtGetValue())) {
				return new Value(lhs.asInt() >= rhs.asInt());
			}
			return null;
		}
		return lhs;
	}

	@Override
	public Object visit(ASTShiftExpression node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		if (node.jjtGetNumChildren() > 1) {
			Value rhs = (Value)(node.jjtGetChild(1).jjtAccept(this, data));
			if ("<<".equals(node.jjtGetValue())) {
				return new Value(lhs.asInt() << rhs.asInt());
			} 
			if (">>".equals(node.jjtGetValue())) {
				return new Value(lhs.asInt() >> rhs.asInt());
			}
			if (">>>".equals(node.jjtGetValue())) {
				return new Value(lhs.asInt() >>> rhs.asInt());
			}
			return null;
		}
		return lhs;
	}

	@Override
	public Object visit(ASTAdditiveExpression node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		for (int i=1; i<node.jjtGetNumChildren(); i++) {
			Value rhs = (Value)(node.jjtGetChild(i).jjtAccept(this, data));
			if ("+".equals(node.jjtGetValue())) {
				if ((lhs.getType() == Value.Type.STRING) || (rhs.getType() == Value.Type.STRING)) {
					lhs = new Value(lhs.asString() + rhs.asString());
				} else {
					lhs = new Value(lhs.asInt() + rhs.asInt());
				}
			} else
			if ("-".equals(node.jjtGetValue())) {
				lhs = new Value(lhs.asInt() - rhs.asInt());
			}else return null;
		}
		return lhs;
	}

	@Override
	public Object visit(ASTMultiplicativeExpression node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		for (int i=1; i<node.jjtGetNumChildren(); i++) {
			Value rhs = (Value)(node.jjtGetChild(1).jjtAccept(this, data));
			if ("*".equals(node.jjtGetValue())) {
				lhs = new Value(lhs.asInt() * rhs.asInt());
			} else
			if ("/".equals(node.jjtGetValue())) {
				lhs = new Value(lhs.asInt() / rhs.asInt());
			} else
			if ("%".equals(node.jjtGetValue())) {
				lhs = new Value(lhs.asInt() % rhs.asInt());
			} else	return null;
		}
		return lhs;
	}

	@Override
	public Object visit(ASTUnaryExpression node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		if ((node.jjtGetValue() != null) && ("-".equals(node.jjtGetValue()))) {
			return new Value(-lhs.asInt());
		}
		return lhs;
	}

	@Override
	public Object visit(ASTPreIncrementExpression node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		String name = lhs.asString();
		Value v = variables.get(name);
		v = new Value(v.asInt() + 1);
		variables.put(name,v);
		return v;
	}

	@Override
	public Object visit(ASTPreDecrementExpression node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		String name = lhs.asString();
		Value v = variables.get(name);
		v = new Value(v.asInt() - 1);
		variables.put(name,v);
		return v;
	}

	@Override
	public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		if ((node.jjtGetValue() != null) && ("~".equals(node.jjtGetValue()))) {
			return new Value(~lhs.asInt());
		}
		if ((node.jjtGetValue() != null) && ("!".equals(node.jjtGetValue()))) {
			return new Value(!lhs.asBoolean());
		}
		return lhs;
	}

	@Override
	public Object visit(ASTPostfixExpression node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		String name = lhs.asString();
		if ((node.jjtGetValue() != null) && ("++".equals(node.jjtGetValue()))) {
			Value v = variables.get(name);
			Value nv = new Value(v.asInt() + 1);
			variables.put(name,nv);
			return v;
		
		}
		if ((node.jjtGetValue() != null) && ("--".equals(node.jjtGetValue()))) {
			Value v = variables.get(name);
			Value nv = new Value(v.asInt() - 1);
			variables.put(name,nv);
			return v;
		}
		return lhs;
	}

	@Override
	public Object visit(ASTPrimaryExpression node, Object data) {
		//System.out.println("Visit PrimaryExpression");
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		if (node.jjtGetNumChildren() > 1) {
			//System.out.println("PrimaryExpression has []");
			Value rhs = (Value)( node.jjtGetChild(1).jjtAccept(this, data));
			//System.out.println("PrimaryExpression index = "+rhs.asInt()+" lhs="+lhs.asString());
			// [rhs]
			int start = 7+rhs.asInt()*2;
			if (lhs.asString().length() < start+2) {
				return new Value("");
			}
			char c1 = lhs.asString().charAt(start);
			char c2 = lhs.asString().charAt(start+1);
			Value nv = new Value(Util.getNum(c1,c2));
			//System.out.println("PrimatyExpression returning "+nv.asInt());
			return nv;
		}
		return lhs;
	}

	@Override
	public Object visit(ASTPrimaryPrefix node, Object data) {
		if (node.jjtGetValue() != null) {
			return variables.get("this");
		}
		return (Value)(node.jjtGetChild(0).jjtAccept(this, data));
	}

	@Override
	public Object visit(ASTPrimarySuffix node, Object data) {
		if (node.jjtGetNumChildren() > 0) {
			Value index = (Value) node.jjtGetChild(0).jjtAccept(this, data);
//System.out.println("Visit PrimarySuffix val="+index.asInt());
			return index;
		}
		return new Value(0);
	}

	@Override
	public Object visit(ASTLiteral node, Object data) {
		return (Value)node.jjtGetValue();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Object visit(ASTCharConversion node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
//System.out.println("Visit char lhs="+lhs+" type="+lhs.getType());
		switch(lhs.getType()) {
		case INT:
			if ((lhs.asInt() >= 0x20) && (lhs.asInt() <= 0x7E)) return new Value(""+(char)(lhs.asInt()));
		case STRING:
			return new Value(lhs.asString().substring(0, 1));
		}
		return lhs;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Object visit(ASTHex1Conversion node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		switch(lhs.getType()) {
		case INT:
			String hex = "00"+Integer.toHexString(lhs.asInt());
			return new Value(hex.substring(hex.length()-2, hex.length()));
		case STRING:
			hex = "00"+lhs.asString();
			return new Value(hex.substring(hex.length()-2, hex.length()));
		}
		return lhs;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Object visit(ASTHex2Conversion node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		switch(lhs.getType()) {
		case INT:
			String hex = "0000"+Integer.toHexString(lhs.asInt());
			return new Value(hex.substring(hex.length()-4, hex.length()));
		case STRING:
			hex = "0000"+lhs.asString();
			return new Value(hex.substring(hex.length()-4, hex.length()));
		}
		return lhs;
	}

	@Override
	public Object visit(ASTMid node, Object data) {
		Value lhs = (Value)( node.jjtGetChild(0).jjtAccept(this, data));
		Value start = (Value)( node.jjtGetChild(1).jjtAccept(this, data));
		Value len = (Value)( node.jjtGetChild(2).jjtAccept(this, data));
		String s = lhs.asString();
		return new Value(s.substring(start.asInt(), start.asInt() + len.asInt()));
	}

}
