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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.text.StyledDocument;

import co.uk.ccmr.cbus.CbusReceiveListener;
import co.uk.ccmr.cbus.driver.CbusDriver;
import co.uk.ccmr.cbus.sniffer.CbusEvent;
import co.uk.ccmr.cbusio.sniffer.parser.ASTReceive;

public class ReceiverRunner implements Runnable, CbusReceiveListener, MessageSource {
	
	private JFrame frame;
	private CbusDriver driver;
	private CbusEvent event;
	private ASTReceive receiver;
	private BlockingQueue<CbusEvent> inq;
	private ScriptHandler scriptHandler;
	private StyledDocument log;

	public ReceiverRunner(ASTReceive receiver, CbusDriver driver, ScriptHandler scriptHandler, JFrame frame, CbusEvent ce, StyledDocument log) {
		this.driver = driver;
		this.frame = frame;
		this.receiver = receiver;
		this.event = ce;
		this.scriptHandler = scriptHandler;
		this.log = log;
	}

	@Override
	public void run() {
		inq = new LinkedBlockingQueue<CbusEvent>();
		driver.addListener(this);
		ScriptInterpreter si = new ScriptInterpreter(driver, scriptHandler, this, frame, event, log);
		si.visit(receiver, null);
		driver.removeListener(this);
	}

	@Override
	public void receiveMessage(CbusEvent ce) {
System.out.println("Receiver runner received event "+ce);
		try {
			inq.put(ce);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public void receiveString(String input) {
	}
	
	public CbusEvent getEvent(long timeout) {
System.out.println("Receiver runner getting event timeout="+timeout);
		if (timeout <= 0L) return null;
		CbusEvent ce=null;
		try {
			ce = inq.poll(timeout, TimeUnit.MILLISECONDS);
System.out.println("Receiver runner got event "+ce);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ce;
	}
}
