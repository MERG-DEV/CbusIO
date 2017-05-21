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
package co.uk.cbusio.ui;

/**
 * Source: http://www.camick.com/java/source/LimitLinesDocumentListener.java
 * More info: https://tips4java.wordpress.com/2008/11/08/message-console/
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

/*
 *  A class to control the maximum number of lines to be stored in a Document
 *
 *  Excess lines can be removed from the start or end of the Document
 *  depending on your requirement.
 *
 *  a) if you append text to the Document, then you would want to remove lines
 *     from the start.
 *  b) if you insert text at the beginning of the Document, then you would
 *     want to remove lines from the end.
 */
public class LimitLinesDocumentListener implements DocumentListener {
    private int maximumLines;
    private boolean isRemoveFromStart;

    /*
     *  Specify the number of lines to be stored in the Document.
     *  Extra lines will be removed from the start of the Document.
     */
    public LimitLinesDocumentListener(int maximumLines) {
        this(maximumLines, true);
    }

    /*
     *  Specify the number of lines to be stored in the Document.
     *  Extra lines will be removed from the start or end of the Document,
     *  depending on the boolean value specified.
     */
    public LimitLinesDocumentListener(int maximumLines, boolean isRemoveFromStart) {
        setLimitLines(maximumLines);
        this.isRemoveFromStart = isRemoveFromStart;
    }

    /*
     *  Return the maximum number of lines to be stored in the Document
     */
    public int getLimitLines() {
        return maximumLines;
    }

    /*
     *  Set the maximum number of lines to be stored in the Document
     */
    public void setLimitLines(int maximumLines) {
        if (maximumLines < 1) {
            String message = "Maximum lines must be greater than 0";
            throw new IllegalArgumentException(message);
        }

        this.maximumLines = maximumLines;
    }

    //  Handle insertion of new text into the Document

    public void insertUpdate(final DocumentEvent e) {
        //  Changes to the Document can not be done within the listener
        //  so we need to add the processing to the end of the EDT

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                removeLines(e);
            }
        });
    }

    public void removeUpdate(DocumentEvent e) {
    }

    public void changedUpdate(DocumentEvent e) {
    }

    /*
     *  Remove lines from the Document when necessary
     */
    private void removeLines(DocumentEvent e) {
        //  The root Element of the Document will tell us the total number
        //  of line in the Document.

        Document document = e.getDocument();
        Element root = document.getDefaultRootElement();

        while (root.getElementCount() > maximumLines) {
            if (isRemoveFromStart) {
                removeFromStart(document, root);
            } else {
                removeFromEnd(document, root);
            }
        }
    }

    /*
     *  Remove lines from the start of the Document
     */
    private void removeFromStart(Document document, Element root) {
        Element line = root.getElement(0);
        int end = line.getEndOffset();

        try {
            document.remove(0, end);
        } catch (BadLocationException ble) {
            System.out.println(ble);
        }
    }

    /*
     *  Remove lines from the end of the Document
     */
    private void removeFromEnd(Document document, Element root) {
        //  We use start minus 1 to make sure we remove the newline
        //  character of the previous line

        Element line = root.getElement(root.getElementCount() - 1);
        int start = line.getStartOffset();
        int end = line.getEndOffset();

        try {
            document.remove(start - 1, end - start);
        } catch (BadLocationException ble) {
            System.out.println(ble);
        }
    }
}
