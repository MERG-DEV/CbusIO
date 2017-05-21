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

public class Value {
	enum Type {
		INT,
		STRING,
		BOOLEAN
	};
	
	private Type type;
	private Integer i;
	private String s;
	private Boolean b;
	
	public Value(Integer ii) {
		i = ii;
		type = Type.INT;
	}
	public Value(String ss) {
		s = ss;
		type = Type.STRING;
	}
	public Value(Boolean bb) {
		b = bb;
		type = Type.BOOLEAN;
	}
	public boolean asBoolean() {
		switch (type) {
		case BOOLEAN:
			return b;
		case STRING:
			return Boolean.parseBoolean(s);
		case INT:
			return i != 0;
		}
		return false;
	}
	public int asInt() {
		switch (type) {
		case BOOLEAN:
			return b?1:0;
		case STRING:
			return Integer.parseInt(s,16);
		case INT:
			return i;
		}
		return 0;
	}
	public String asString() {
		switch (type) {
		case BOOLEAN:
			return b?"TRUE":"FALSE";
		case STRING:
			return s;
		case INT:
			return Integer.toHexString(i);
		}
		return null;
	}
	public Type getType() {
		return type;
	}

}
