package org.eclipse.nebula.widgets.formattedtext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Text;

public class IPAddressFormatter extends AbstractFormatter{

	/**Current edited value*/
	private StringBuffer inputCache;
	/**Begin position and end position between which current caret is in*/
	private int begin=0,end=0;
	/** Key listener */
	protected KeyListener klistener;
	/** Mouse listener,used to control the position of the cursor when mouse clicking*/
	protected MouseListener mlistener;
	/**
	 * An empty constructer.<br>
	 * An ip address will have an unique format,so there is no necessary to offer an new format
	 */
	public IPAddressFormatter(){
		
		inputCache = new StringBuffer();
		for(int i=1;i<=12;i++){
			inputCache.append(SPACE);
			if(i!=12&&i%3==0) inputCache.append('.');
		}
		
		klistener = new KeyListener(){
			public void keyPressed(KeyEvent e) {
				int currPos=text.getCaretPosition();
				int nextBegin=4*(currPos/4+1);
				int nextEnd=nextBegin+3;
				if(nextBegin<=12&&e.character==SPACE) text.setSelection(nextBegin, nextEnd);
				else return;
				e.doit=false;
			}
			public void keyReleased(KeyEvent e) {
				
			}
			
		};
		
		mlistener = new MouseAdapter(){
			public void mouseDown(MouseEvent arg0) {
				
			}
			
		};
	}
	/**
	 * Locate current edited area,then (begin,end) will be represented as this area.<br/>
	 * This area is just a 1/4 part of the ip address.
	 */
	private void locateCurrentArea(){
		int pos=text.getCaretPosition();
		end=pos;
		while(end<15&&inputCache.charAt(end)!='.') end++;
		begin=end-4;
	}
	/**
	 * Test a 1/4 part of the ip address represented in the String format is valid or not
	 * @param ipp the 1/4 part of the address
	 * @return if valid,return true;else false
	 */
	private boolean isValidPart(String ipp){
		int num=0;
		for(char c:ipp.toCharArray()){
			int n = c - '0';
			if(n>=0&&n<=9) num=10*num+n;
			else return false;
		}
		if(num>=0&&num<=255) return true;
		else return false;
	}
	/**
	 * Clear a part of the input cache.<br>
	 * Characters are replaced by spaces in the fields, but separators are
	 * preserved.
	 * 
	 * @param b beginning index (inclusive)
	 * @param e end index (exclusive)
	 * @return Return new position of the cursor
	 */
	private int clear(int b,int e){
		for(int pos=b;pos<e;pos++){
			if(inputCache.charAt(pos)!='.') inputCache.setCharAt(pos, SPACE);
		}
		adjustInputCache();
		locateCurrentArea();
		if(e>0&&inputCache.charAt(e-1)==SPACE) return begin;
		else return e;
	}
	/**
	 * When doing some modifications,the input cache should be adjusted,thus every number in
	 * the area will be layed next to the splitting dot flag.
	 */
	private void adjustInputCache(){
		String[] parts=inputCache.toString().split("[.]");
		int i=1;
		for(String part:parts){
			part=part.trim();
			StringBuffer temp=new StringBuffer(part);
			for(int j=0;j<temp.length();j++) if(temp.charAt(j)==SPACE) temp.deleteCharAt(j);//remove SPACE in the middle
			int spaceLength=3-temp.length();
			while(spaceLength-->0) temp.insert(0, SPACE);
			part=temp.toString();
			inputCache.replace(4*(i-1), 4*i-1, part);
			i++;
		}
	}
	/**
	 * Inserts a sequence of characters in the input buffer. The current content
     * of the buffer is overrided. The new position of the cursor is computed and
     * returned.
	 * @param txt String of characters to insert
	 * @param pos Starting position of insertion
	 * @return New position of the cursor
	 */
	private int insert(String txt,int pos){
		locateCurrentArea();
		String currPart = inputCache.substring(begin+1, end);
		if(txt.length()>3) return end;
		if((txt.length()+currPart.trim().length())>3) return end;
		int currPos=text.getCaretPosition();
		if(isValidPart(currPart.trim())&&currPart.trim().length()==3&&currPos==end)
			return end+2;//step to next ip part
		
		StringBuffer currEdit=new StringBuffer(currPart);
		int b=0;//first pos containing a none space character
		while(b<3&&currEdit.charAt(b)==SPACE) b++;
		int relativeInsertPos = pos-begin-1;
		if(relativeInsertPos>end||relativeInsertPos<b) relativeInsertPos=3;
		currEdit.insert(relativeInsertPos, txt);
		if(!isValidPart(currEdit.toString().trim())){
			beep();
			return end;
		}else{
			int l=currEdit.length();
			currEdit.delete(0, l-3);
			inputCache.replace(begin+1, end, currEdit.toString());
			if(pos==end&&currEdit.toString().trim().length()==3) return end+1;
			return end;
		}
	}
	/**
	 * Called when the formatter is replaced by an other one in the <code>FormattedText</code>
	 * control. Allow to release ressources like additionnal listeners.<p>
	 * 
	 * Removes the <code>KeyListener</code> on the text widget.
	 * 
	 * @see ITextFormatter#detach()
	 */
	public void detach() {
		text.removeKeyListener(klistener);
	}
	/**
	 * Sets the <code>Text</code> widget that will be managed by this formatter.<p>
	 * 
	 * The ancestor is overrided to add a key listener on the text widget.
	 * 
	 * @param text Text widget
	 * @see ITextFormatter#setText(Text)
	 */
	public void setText(Text text) {
		super.setText(text);
		text.addKeyListener(klistener);
	}
	/**
	 * Returns the current value formatted for display.
	 * This method is called by <code>FormattedText</code> when the <code>Text</code>
	 * widget looses focus.
	 * In case the input is invalid (eg. not an invalid ip address), the edit
	 * string is returned in place of the display string.
	 * 
	 * @return display string if valid, edit string else
	 * @see ITextFormatter#getDisplayString()
	 */
	public String getDisplayString() {
		return inputCache.toString();
	}
	/**
	 * Returns the current value formatted for input.
	 * This method is called by <code>FormattedText</code> when the <code>Text</code>
	 * widget gains focus.
	 * The value returned is the content of the StringBuilder used as cache.
	 * 
	 * @return edit string
	 * @see ITextFormatter#getEditString()
	 */
	public String getEditString() {
		return inputCache.toString();
	}
	/**
	 * Returns the current value of the text control if it is a valid ip address.<br>
	 * If invalid, returns <code>null</code>.
	 * @return current ip address if valid in which the spaces has been removed, 
	 * <code>null</code> else
	 * @see ITextFormatter#getValue()
	 */
	public Object getValue() {
		if(isValid()){
			StringBuffer value=new StringBuffer(inputCache);
			int i=0;
			for(i=0;i<value.length();i++)
				if(value.charAt(i)==SPACE)
					value.deleteCharAt(i);
			return value.toString();
		}else 
			return null;
	}
	/**
	 * Returns <code>true</code> if current edited value is valid, else returns
	 * <code>false</code>. 
	 * 
	 * @return true if valid, else false
	 * @see ITextFormatter#isValid()
	 */
	public boolean isValid() {
		String[] parts = inputCache.toString().split("[.]");
		for(String part:parts){
			part=part.trim();
			if(!isValidPart(part)) return false;
		}
		return true;
	}
	/**
	 * Sets the value to edit. The value provided must be a valid ip address in String format.
	 * 
	 * @param value new ip address
	 * @throws IllegalArgumentException if not an invalid ip
	 * @see ITextFormatter#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		if(value instanceof String){
			String ip=value.toString();
			String[] parts = ip.split("[.]");
			int i=1;
			for(String part:parts){
				if(!isValidPart(part)){
					throw new IllegalArgumentException("Invalid ip address");
				}else{
					inputCache.replace(4*i-1-part.length(), 4*i-1, part);
				}
				i++;
			}
		}else if(value==null){
			clear(0,15);
		}else{
			throw new IllegalArgumentException("Invalid ip address");
		}
	}
	public void verifyText(VerifyEvent e) {
		if(ignore) return;
		e.doit = false;
	  	if ( e.keyCode == SWT.BS || e.keyCode == SWT.DEL ) {
	  		e.start = clear(e.start, e.end);
	  	} else {
	  		e.start = insert(e.text, e.start);
	  	}
		updateText(inputCache.toString(),e.start);
	}

}
