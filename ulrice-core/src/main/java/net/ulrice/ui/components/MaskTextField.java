package net.ulrice.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.metal.MetalTextFieldUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.FieldView;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * Formatted text field with mask support based on the idea of core swing: advanced programming
 * 
 * @author christof
 */
public class MaskTextField extends JTextField implements FocusListener {

    private static final long serialVersionUID = 5133016182441447477L;

    public static final char PLACEHOLDER_CHAR = '_';
    public static final char ALL_MASK_CHAR = '*';
    public static final char NUM_MASK_CHAR = '#';
    public static final char CHARACTER_MASK_CHAR = '?';
    public static final char CHARACTER_UPPERCASE_MASK_CHAR = 'U';
    public static final char CHARACTER_LOWERCASE_MASK_CHAR = 'L';
    public static final char ALL_MASK_UPPERCASE_CHAR = 'u';
    public static final char ALL_MASK_LOWERCASE_CHAR = 'l';
    public static final char CHARACTER_OR_NUMBER_CHAR = 'A';
    public static final char CHARACTER_OR_NUMBER_UPPERCASE_CHAR = 'C';
    public static final char CHARACTER_OR_NUMBER_LOWERCASE_CHAR = 'c';
    /**
     * Can contain character, number, underscore _ or hyphen -
     */
    public static final char CHARACTER_OR_NUMBER_SPECIAL_CHAR = 'g';
    public static final char CHARACTER_OR_NUMBER_SPECIAL_UPPERCASE_CHAR = 'H';
    public static final char CHARACTER_OR_NUMBER_SPECIAL_LOWERCASE_CHAR = 'h';
    public static final char ESCAPE_CHAR = '\'';

    private static final char HYPHEN = '-';
    private static final char UNDERSCORE = '_';

    private final List<Boolean> maskCharList = new ArrayList<Boolean>();

    private int maskLen = 0;
    private String mask = null;
    private String cleanedMask = null;
    private String displayedMask = null;

    private int numMaskChars;

    private boolean textWasMarked = false;

    private String regex;
    private boolean useRegex;

    public MaskTextField() {
        super();

        addFocusListener(this);
        setOpaque(false);
    }

    public void setMask(String mask) {
        String oldMask = this.mask;
        if (mask == null) {
            return;
        }
        if (((oldMask == null) && (mask != null)) || ((oldMask != null) && !oldMask.equals(mask))) {

            StringBuilder builderDisplay = new StringBuilder();
            StringBuilder builderMask = new StringBuilder();
            numMaskChars = 0;
            boolean lastWasEscape = false;
            for (int i = 0; i < mask.length(); i++) {
                char curMaskChar = mask.charAt(i);
                if (!lastWasEscape && isMaskChar(curMaskChar)) {
                    numMaskChars++;
                    maskCharList.add(Boolean.TRUE);
                    builderDisplay.append(PLACEHOLDER_CHAR);
                    builderMask.append(curMaskChar);
                }
                else if ((!lastWasEscape && (curMaskChar != ESCAPE_CHAR)) || (lastWasEscape)) {
                    maskCharList.add(Boolean.FALSE);
                    builderDisplay.append(curMaskChar);
                    builderMask.append(curMaskChar);
                }
                lastWasEscape = !lastWasEscape && (curMaskChar == ESCAPE_CHAR);
            }
            this.mask = mask;
            this.cleanedMask = builderMask.toString();
            this.maskLen = cleanedMask.length();
            setColumns(maskLen);

            ((MaskTextFieldDocument) getDocument()).setMaxLen(numMaskChars == 0 ? Integer.MAX_VALUE : numMaskChars);
            displayedMask = builderDisplay.toString();
            updateUI();
        }
    }

    public void setRegex(String regex) {
        this.regex = regex;
        this.useRegex = true;
    }

    private boolean isMaskChar(int idx) {
        return maskCharList.get(idx);
    }

    private boolean isMaskChar(char chr) {
        return (chr == ALL_MASK_CHAR) || (chr == NUM_MASK_CHAR) || (chr == CHARACTER_MASK_CHAR) || (chr == CHARACTER_OR_NUMBER_CHAR) || (chr == CHARACTER_LOWERCASE_MASK_CHAR)
            || (chr == CHARACTER_UPPERCASE_MASK_CHAR) || (chr == ALL_MASK_LOWERCASE_CHAR) || (chr == ALL_MASK_UPPERCASE_CHAR) || (chr == CHARACTER_OR_NUMBER_UPPERCASE_CHAR)
            || (chr == CHARACTER_OR_NUMBER_LOWERCASE_CHAR) || (chr == CHARACTER_OR_NUMBER_SPECIAL_CHAR) || (chr == CHARACTER_OR_NUMBER_SPECIAL_LOWERCASE_CHAR)
            || (chr == CHARACTER_OR_NUMBER_SPECIAL_UPPERCASE_CHAR);
    }

    @Override
    public Color getBackground() {
        if (isEditable()) {
            return UIManager.getColor("TextField.background");
        }
        else {
            return UIManager.getColor("TextField.disabledBackground");
        }
    }

    @Override
    public void updateUI() {
        setUI(new MaskTextFieldUI());
        setFont(UIManager.getFont("MaskTextField.font"));
    }

    @Override
    protected Document createDefaultModel() {
        return new MaskTextFieldDocument();
    }

    @Override
    public void replaceSelection(String content) {
        int p0 = Math.min(getCaret().getDot(), getCaret().getMark());
        int p1 = Math.max(getCaret().getDot(), getCaret().getMark());

        textWasMarked = p0 != p1;
        super.replaceSelection(content);
    }

    private class MaskTextFieldDocument extends PlainDocument {

        private static final long serialVersionUID = -832649015081183062L;
        private int maxLen = Integer.MAX_VALUE;

        public void setMaxLen(int maxLen) {
            this.maxLen = maxLen;
        }

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) {
                return;
            }
            if ((cleanedMask != null) && !"".equals(cleanedMask)) {
                StringBuilder resultStr = new StringBuilder();
                char[] maskArr = cleanedMask.toCharArray();
                int idxMask = 0;
                int idxStr = 0;
                for (int i = 0; (i < maskArr.length) && (idxStr < str.length()); i++) {
                    char maskChar = maskArr[i];
                    if (isMaskChar(i)) {
                        if ((idxMask >= offs) && (idxMask < (offs + str.length()))) {
                            char inputChar = str.charAt(idxStr);

                            if (useRegex && !String.valueOf(inputChar).matches(regex)) {
                                return;
                            }
                            else {
                                if (!validInputChar(inputChar, maskChar)) {
                                    return;
                                }
                            }
                            switch (maskChar) {
                                case ALL_MASK_LOWERCASE_CHAR:
                                case CHARACTER_LOWERCASE_MASK_CHAR:
                                case CHARACTER_OR_NUMBER_LOWERCASE_CHAR:
                                case CHARACTER_OR_NUMBER_SPECIAL_LOWERCASE_CHAR:
                                    resultStr.append(Character.toLowerCase(inputChar));
                                    break;
                                case ALL_MASK_UPPERCASE_CHAR:
                                case CHARACTER_UPPERCASE_MASK_CHAR:
                                case CHARACTER_OR_NUMBER_UPPERCASE_CHAR:
                                case CHARACTER_OR_NUMBER_SPECIAL_UPPERCASE_CHAR:
                                    resultStr.append(Character.toUpperCase(inputChar));
                                    break;
                                default:
                                    resultStr.append(inputChar);
                                    break;
                            }
                            idxStr++;
                        }
                        idxMask++;
                    }
                }
                int lenAfterInsert = (maxLen - str.length()) + 1;
                String text = resultStr.toString();
                if (lenAfterInsert < 0) {
                    text = str.substring(0, -(lenAfterInsert + 1));
                }

                if ((offs < getLength()) && !textWasMarked) {
                    // super.remove(offs, text.length()); no override
                }

                if ((getLength() >= maxLen) & ((getLength() + str.length()) <= maxLen)) {
                    text = str.substring(0, maxLen);
                }
                else if (str.length() >= maxLen) {
                    text = str.substring(0, maxLen);
                }

                if ((getLength() < maxLen) & !((getLength() + text.length()) > maxLen)) {
                    super.insertString(offs, text, a);
                }
            }
            else {
                super.insertString(offs, str, a);
            }

        }

        private boolean validInputChar(char inputChar, char maskChar) {
            switch (maskChar) {
                case CHARACTER_LOWERCASE_MASK_CHAR:
                case CHARACTER_UPPERCASE_MASK_CHAR:
                case CHARACTER_MASK_CHAR:
                    return Character.isLetter(inputChar) || (inputChar == ' ');
                case ALL_MASK_LOWERCASE_CHAR:
                case ALL_MASK_UPPERCASE_CHAR:
                    return inputChar <= 0xff;
                case CHARACTER_OR_NUMBER_CHAR:
                case CHARACTER_OR_NUMBER_UPPERCASE_CHAR:
                case CHARACTER_OR_NUMBER_LOWERCASE_CHAR:
                    boolean valid = Character.isLetter(inputChar) || Character.isDigit(inputChar);
                    return valid;
                case CHARACTER_OR_NUMBER_SPECIAL_CHAR:
                case CHARACTER_OR_NUMBER_SPECIAL_LOWERCASE_CHAR:
                case CHARACTER_OR_NUMBER_SPECIAL_UPPERCASE_CHAR:
                    boolean validSpecial = Character.isLetter(inputChar) || Character.isDigit(inputChar) || (inputChar == HYPHEN) || (inputChar == UNDERSCORE);
                    return validSpecial;
                case NUM_MASK_CHAR:
                    return Character.isDigit(inputChar);
                default:
                    return true;
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        repaint();
    }

    private class MaskTextFieldUI extends MetalTextFieldUI {

        @SuppressWarnings("rawtypes")
        private Map desktopHints;

        @Override
        public View create(Element elem) {
            if (mask != null) {
                return new MaskFieldView(elem);
            }
            else {
                return super.create(elem);
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void paintSafely(Graphics g) {
            if (desktopHints == null) { 
                Toolkit tk = Toolkit.getDefaultToolkit(); 
                desktopHints = (Map) (tk.getDesktopProperty("awt.font.desktophints")); 
            }
            if (desktopHints != null) { 
                ((Graphics2D)g).addRenderingHints(desktopHints); 
            } 
            
            //((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            paintInsets(g);

            super.paintSafely(g);
        }

        protected void paintInsets(Graphics g) {
            JTextComponent component = MaskTextFieldUI.this.getComponent();
            Insets insets = component.getInsets();
            int left = insets.left;
            int top = insets.top;
            int right = insets.right;
            int bottom = insets.bottom;

            g.setColor(component.getBackground());
            g.fillRect(left, top, component.getWidth() - left - right, component.getHeight() - top - bottom);
        }

    }

    private class MaskFieldView extends FieldView {

        private static final char WIDE_CHAR = 'X';
        private char[] contentChars = new char[0];
        private char[] displayMaskChars = new char[0];
        private int[] markerOffsets = new int[0];

        private final Segment workSegment = new Segment();
        private final Segment maskSegment = new Segment();
        private final Segment contentSegment = new Segment();

        public MaskFieldView(Element elem) {
            super(elem);
            buildMapping();
            buildContent();
        }

        @Override
        public Shape modelToView(int pos, Shape a, Bias b) throws BadLocationException {
            if (cleanedMask == null) {
                return super.modelToView(pos, a, b);
            }

            a = adjustAllocation(a);
            Rectangle rect = new Rectangle(a.getBounds());
            rect.height = getFontMetrics().getHeight();

            int oldCount = contentSegment.count;

            if (pos < markerOffsets.length) {
                contentSegment.count = markerOffsets[pos];
            }
            else if (markerOffsets.length > 0) {
                contentSegment.count = markerOffsets[markerOffsets.length - 1] + 1;
            }
            else {
                contentSegment.count = 0;
            }

            int offset = Utilities.getTabbedTextWidth(contentSegment, getFontMetrics(), 0, this, getElement().getStartOffset());
            contentSegment.count = oldCount;

            rect.x += offset;
            rect.width = 1;

            return rect;
        }

        @Override
        public int viewToModel(float fx, float fy, Shape a, Position.Bias[] bias) {
            if (cleanedMask == null) {
                return super.viewToModel(fx, fy, a, bias);
            }

            a = adjustAllocation(a);
            bias[0] = Position.Bias.Forward;

            int x = (int) fx;
            int y = (int) fy;
            Rectangle rect = a.getBounds();
            int startOffset = getElement().getStartOffset();
            int endOffset = getElement().getEndOffset();

            if ((x < rect.x) || (y < rect.y)) {
                return startOffset;
            }
            else if ((x > (rect.x + rect.width)) || (y > (rect.y + rect.height))) {
                return endOffset - 1;
            }

            int offset = Utilities.getTabbedTextOffset(contentSegment, getFontMetrics(), rect.x, x, this, startOffset);

            for (int i = 0; i < markerOffsets.length; i++) {
                if (offset <= markerOffsets[i]) {
                    offset = i;
                    break;
                }
            }

            if (offset > (endOffset - 1)) {
                offset = endOffset - 1;
            }
            return offset;
        }

        @Override
        public void insertUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
            if (cleanedMask == null) {
                super.insertUpdate(changes, a, f);
            }
            else {
                super.insertUpdate(changes, adjustAllocation(a), f);
                buildContent();
            }
        }

        @Override
        public void removeUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
            if (cleanedMask == null) {
                super.insertUpdate(changes, a, f);
            }
            else {
                super.removeUpdate(changes, adjustAllocation(a), f);
                buildContent();
            }
        }

        @Override
        protected void drawLine(int line, Graphics g, int x, int y) {
            if (cleanedMask == null) {
                super.drawLine(line, g, x, y);
                return;
            }

            int p0 = getElement().getStartOffset();
            int p1 = getElement().getEndOffset() - 1;
            int sel0 = ((JTextComponent) getContainer()).getSelectionStart();
            int sel1 = ((JTextComponent) getContainer()).getSelectionEnd();

            try {

                if ((p0 == p1) || (sel0 == sel1) || (inView(p0, p1, sel0, sel1) == false)) {
                    drawUnselectedText(g, x, y, 0, contentSegment.count);
                    return;
                }

                int mappedSel0 = mapOffset(Math.max(sel0 - p0, 0));
                int mappedSel1 = mapOffset(Math.min(sel1 - p0, p1 - p0));

                if (mappedSel0 > 0) {
                    x = drawUnselectedText(g, x, y, 0, mappedSel0);
                }
                x = drawSelectedText(g, x, y, mappedSel0, mappedSel1);

                if (mappedSel1 < contentSegment.count) {
                    drawUnselectedText(g, x, y, mappedSel1, contentSegment.count);
                }
            }
            catch (BadLocationException e) {
                //
            }
        }

        @Override
        protected int drawUnselectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
            if (cleanedMask == null) {
                return super.drawSelectedText(g, x, y, p0, p1);
            }
            g.setColor(getUnselectedColor());
            workSegment.array = contentSegment.array;
            workSegment.offset = p0;
            workSegment.count = p1 - p0;
            return Utilities.drawTabbedText(workSegment, x, y, g, this, p0);
        }

        @Override
        protected int drawSelectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
            if (cleanedMask == null) {
                return super.drawSelectedText(g, x, y, p0, p1);
            }
            workSegment.array = contentSegment.array;
            workSegment.offset = p0;
            workSegment.count = p1 - p0;
            g.setColor(getSelectedColor());
            return Utilities.drawTabbedText(workSegment, x, y, g, this, p0);
        }

        private int mapOffset(int pos) {
            pos -= getElement().getStartOffset();
            if (pos >= markerOffsets.length) {
                return contentSegment.count;
            }
            else {
                return markerOffsets[pos];
            }
        }

        private boolean inView(int p0, int p1, int sel0, int sel1) {
            if ((sel0 >= p0) && (sel0 < p1)) {
                return true;
            }

            if ((sel0 < p0) && (sel1 >= p0)) {
                return true;
            }

            return false;
        }

        private Color getSelectedColor() {
            MaskTextField tf = (MaskTextField) getContainer();
            return tf.getCaret().isSelectionVisible() ? tf.getSelectedTextColor() : getUnselectedColor();
        }

        private Color getUnselectedColor() {
            MaskTextField tf = (MaskTextField) getContainer();
            return (tf.isEnabled()) ? tf.getForeground() : tf.getDisabledTextColor();
        }

        private void buildContent() {
            try {
                Element elem = getElement();
                Document doc = getDocument();
                int startOffset = elem.getStartOffset();
                int length = elem.getEndOffset() - elem.getStartOffset() - 1;

                if (maskLen == 0) {
                    doc.getText(startOffset, length, contentSegment);
                }
                else {
                    doc.getText(startOffset, length, workSegment);
                    System.arraycopy(displayMaskChars, 0, contentChars, 0, maskLen);

                    int count = Math.min(length, numMaskChars);
                    int firstOffset = workSegment.offset;

                    for (int i = 0; i < count; i++) {
                        contentChars[markerOffsets[i]] = workSegment.array[i + firstOffset];
                    }
                }
            }
            catch (BadLocationException e) {
                contentSegment.count = 0;
            }
        }

        private void buildMapping() {
            if (maskLen != 0) {
                displayMaskChars = displayedMask.toCharArray();

                contentChars = new char[maskLen];
                contentSegment.offset = 0;
                contentSegment.array = contentChars;
                contentSegment.count = maskLen;

                maskSegment.offset = 0;
                maskSegment.array = cleanedMask.toCharArray();
                maskSegment.count = maskLen;

                markerOffsets = new int[numMaskChars];
                int markerCounter = 0;
                for (int i = 0; i < maskLen; i++) {
                    if (isMaskChar(i)) {
                        markerOffsets[markerCounter++] = i;
                        maskSegment.array[i] = WIDE_CHAR;
                    }
                }
            }
        }

        @Override
        public float getPreferredSpan(int axis) {
            if ((axis == View.Y_AXIS) || (cleanedMask == null)) {
                return super.getPreferredSpan(axis);
            }
            return Math.max(getSegmentWidth(maskSegment), getSegmentWidth(contentSegment));
        }

        private int getSegmentWidth(Segment segment) {
            return Utilities.getTabbedTextWidth(segment, getFontMetrics(), 0, this, 0);
        }
    }
}
