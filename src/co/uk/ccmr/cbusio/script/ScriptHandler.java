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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.uk.ccmr.cbus.CbusReceiveListener;
import co.uk.ccmr.cbus.driver.CbusDriver;
import co.uk.ccmr.cbus.sniffer.CbusEvent;
import co.uk.ccmr.cbusio.sniffer.parser.ASTMacro;
import co.uk.ccmr.cbusio.sniffer.parser.ASTProgram;
import co.uk.ccmr.cbusio.sniffer.parser.ASTReceive;
import co.uk.ccmr.cbusio.sniffer.parser.ScriptParser;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class ScriptHandler implements CbusReceiveListener {
	private List<ASTMacro> macros;
	private List<ASTReceive> receivers;
	private List<MacroChangeListener> macroListeners;
	private JFrame frame;
	private CbusDriver driver;
	private StyledDocument log;

	
	public ScriptHandler(CbusDriver driver, JFrame frame, StyledDocument log) {
		macros = new ArrayList<ASTMacro>();
		receivers = new ArrayList<ASTReceive>();
		macroListeners = new ArrayList<MacroChangeListener>();
		this.frame = frame;
		this.driver = driver;
		this.log = log;
	}
	
	public void load(File script, StyledDocument log) {
		InputStream in;
		try {
			in = new FileInputStream(script);
			ScriptParser parser = new ScriptParser(in);
		    try {
				ASTProgram program = parser.program();
				System.out.println("Parsed");
				LoadingVisitor lv = new LoadingVisitor();
				System.out.println("Load Visiting");
				lv.visit(program, this);
				fireMacrosChanged();
				System.out.println("Loaded");
				if (log != null) {
					StyleContext sc = StyleContext.getDefaultStyleContext();
					AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
							StyleConstants.Foreground, new Color(0x80,0,0));
					try {
						log.insertString(log.getLength(), "Loaded script file "+script.getAbsolutePath()+"\n", aset);
					} catch (BadLocationException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
			} catch (Exception e) {
				if (log != null) {
					StyleContext sc = StyleContext.getDefaultStyleContext();
					AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
		    	                                        StyleConstants.Foreground, Color.RED);
					try {
						log.insertString(log.getLength(), "Failed to parse script "+script.getAbsolutePath()+"\n", aset);
						log.insertString(log.getLength(), e.getMessage(), aset);
					} catch (BadLocationException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			if (log != null) {
				StyleContext sc = StyleContext.getDefaultStyleContext();
				AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
	    	                                        StyleConstants.Foreground, Color.RED);
				try {
					log.insertString(log.getLength(), "Script file "+script.getAbsolutePath()+" not found\n", aset);
				} catch (BadLocationException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		}
		
	}
	
	public ASTMacro[] getMacros() {
		return (ASTMacro[]) macros.toArray(new ASTMacro[0]);
	}
	
	public void runMacro(ASTMacro m) {
		// run the macro in a new thread
		System.out.println("Run macro "+m.name);
		Thread script = new Thread(new MacroRunner(m, driver, this, frame, log));
		script.start();
	}
	
	public void runReceiver(ASTReceive r, CbusEvent e) {
		// run the macro in a new thread
		System.out.println("Run receiver "+r.trigger);
		Thread script = new Thread(new ReceiverRunner(r, driver, this, frame, e, log));
		script.start();
	}

	@Override
	public void receiveMessage(CbusEvent ce) {
		String cname = ce.toString();
		
		System.out.println("Script handler got a message:"+ce);
		for (ASTReceive r : receivers) {
			if (r.pattern == null) {
				r.pattern = Pattern.compile(r.trigger);
			}
			System.out.println("checking against:"+r.trigger);
			Matcher m = r.pattern.matcher(cname);
			if (m.matches()) {
				// run the receiver script in new thread
				System.out.println("Run receiver "+r.trigger);
				runReceiver(r, ce);
			}
		}

	}
	
	@Override
	public void receiveString(String input) {
	}

	public void addReceiver(ASTReceive node) {
		for (int i=0; i<receivers.size(); i++) {
			ASTReceive m = receivers.get(i);
			if (m.trigger.equals(node.trigger)) {
				// replace
				System.out.println("Receiver for \""+node.trigger+"\" replaced");
				receivers.remove(i);
				receivers.add(node);
				return;
			}
		}
		// add
		System.out.println("Receiver for \""+node.trigger+"\" added");
		receivers.add(node);	
	}

	public void addMacro(ASTMacro node) {
		
		for (int i=0; i<macros.size(); i++) {
			ASTMacro m = macros.get(i);
			if (m.name.equals(node.name)) {
				// replace
				System.out.println("Macro name="+node.name+" replaced");
				macros.remove(i);
				macros.add(node);
				return;
			}
		}
		// add
		System.out.println("Macro name="+node.name+" added");
		macros.add(node);		
	}

	public void addMacroChangeListener(MacroChangeListener mcl) {
		macroListeners.add(mcl);
		
	}
	
	private void fireMacrosChanged() {
		for (MacroChangeListener mcl : macroListeners) {
			mcl.macrosChanged();
		}
	}

}
