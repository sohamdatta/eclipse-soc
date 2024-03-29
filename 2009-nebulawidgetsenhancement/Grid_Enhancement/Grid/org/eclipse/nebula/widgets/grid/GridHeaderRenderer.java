package org.eclipse.nebula.widgets.grid;

import org.eclipse.swt.graphics.Rectangle;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 * The super class for all grid header renderers.  Contains the properties specific
 * to a grid header.
 *
 * @author chris.gross@us.ibm.com
 */
public abstract class GridHeaderRenderer extends AbstractInternalWidget
{
    /**
     * Returns the bounds of the text in the cell.  This is used when displaying in-place tooltips.
     * If <code>null</code> is returned here, in-place tooltips will not be displayed.  If the 
     * <code>preferred</code> argument is <code>true</code> then the returned bounds should be large
     * enough to show the entire text.  If <code>preferred</code> is <code>false</code> then the 
     * returned bounds should be be relative to the current bounds.
     * 
     * @param value the object being rendered.
     * @param preferred true if the preferred width of the text should be returned.
     * @return bounds of the text.
     */
    public Rectangle getTextBounds(Object value, boolean preferred)
    {
        return null;
    }
    
    /**
     * Returns the bounds of the toggle within the header (typically only group headers have toggles)
     * or null.
     * 
     * @return toggle bounds or null if no toggle exists.
     */
    public Rectangle getToggleBounds()
    {
        return null;
    }

	/**
	 * Returns the bounds of the control to display
	 * 
	 * @return the bounds for the control or <code>null</code> if no control is
	 *         rendered
	 */
	protected Rectangle getControlBounds(Object value, boolean preferred) {
		return null;
	}
}
