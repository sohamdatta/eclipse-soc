package org.mati.zest.core.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutAnimator;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.SWTEventDispatcher;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.core.widgets.internal.ContainerFigure;
import org.eclipse.zest.core.widgets.internal.ZestRootLayer;
import org.eclipse.zest.layout.interfaces.Filter;
import org.eclipse.zest.layout.interfaces.LayoutAlgorithm;

public class Graph extends FigureCanvas {

	// CLASS CONSTANTS
	public static final int ANIMATION_TIME = 500;
	public static final int FISHEYE_ANIMATION_TIME = 100;

	// @tag CGraph.Colors : These are the colour constants for the graph, they
	// are disposed on clean-up
	public Color LIGHT_BLUE = new Color(null, 216, 228, 248);
	public Color LIGHT_BLUE_CYAN = new Color(null, 213, 243, 255);
	public Color GREY_BLUE = new Color(null, 139, 150, 171);
	public Color DARK_BLUE = new Color(null, 1, 70, 122);
	public Color LIGHT_YELLOW = new Color(null, 255, 255, 206);

	public Color HIGHLIGHT_COLOR = ColorConstants.yellow;
	public Color HIGHLIGHT_ADJACENT_COLOR = ColorConstants.orange;
	public Color DEFAULT_NODE_COLOR = LIGHT_BLUE;

	/**
	 * These are all the children of this graph. These lists contains all nodes
	 * and connections that have added themselves to this graph.
	 */
	private List nodes;
	protected List connections;
	private List selectedItems = null;
	IFigure fisheyedFigure = null;
	private List selectionListeners = null;

	/** This maps all visible nodes to their model element. */
	private HashMap figure2ItemMap = null;

	private int connectionStyle;
	private int nodeStyle;
	private ScalableFreeformLayeredPane fishEyeLayer = null;
	private LayoutAlgorithm layoutAlgorithm = null;
	private InternalLayoutContext layoutContext = null;
	private volatile boolean isLayoutScheduled;
	private Dimension preferredSize = null;
	int style = 0;

	private ScalableFreeformLayeredPane rootlayer;
	private ZestRootLayer zestRootLayer;

	/**
	 * Constructor for a Graph. This widget represents the root of the graph,
	 * and can contain graph items such as graph nodes and graph connections.
	 * 
	 * @param parent
	 * @param style
	 */
	public Graph(Composite parent, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		this.style = style;
		this.setBackground(ColorConstants.white);

		this.setViewport(new FreeformViewport());

		this.getVerticalBar().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Graph.this.redraw();
			}

		});
		this.getHorizontalBar().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Graph.this.redraw();
			}
		});

		// @tag CGraph.workaround : this allows me to handle mouse events
		// outside of the canvas
		this.getLightweightSystem().setEventDispatcher(new SWTEventDispatcher() {
			public void dispatchMouseMoved(org.eclipse.swt.events.MouseEvent me) {
				super.dispatchMouseMoved(me);

				// If the current event is null, return
				if (getCurrentEvent() == null) {
					return;
				}

				if (getMouseTarget() == null) {
					setMouseTarget(getRoot());
				}
				if ((me.stateMask & SWT.BUTTON_MASK) != 0) {
					// Sometimes getCurrentEvent() returns null
					getMouseTarget().handleMouseDragged(getCurrentEvent());
				} else {
					getMouseTarget().handleMouseMoved(getCurrentEvent());
				}
			}
		});

		this.setContents(createLayers());
		DragSupport dragSupport = new DragSupport();
		this.getLightweightSystem().getRootFigure().addMouseListener(dragSupport);
		this.getLightweightSystem().getRootFigure().addMouseMotionListener(dragSupport);

		this.nodes = new ArrayList();
		this.preferredSize = new Dimension(-1, -1);
		this.connectionStyle = ZestStyles.NONE;
		this.nodeStyle = ZestStyles.NONE;
		this.connections = new ArrayList();
		this.selectedItems = new ArrayList();
		this.selectionListeners = new ArrayList();
		this.figure2ItemMap = new HashMap();

		this.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (isLayoutScheduled) {
					applyLayoutInternal();
					isLayoutScheduled = false;
				}
			}
		});

	}

	/**
	 * This adds a listener to the set of listeners that will be called when a
	 * selection event occurs.
	 * 
	 * @param selectionListener
	 */
	public void addSelectionListener(SelectionListener selectionListener) {
		if (!selectionListeners.contains(selectionListener)) {
			selectionListeners.add(selectionListener);
		}
	}

	public void removeSelectionListener(SelectionListener selectionListener) {
		if (selectionListeners.contains(selectionListener)) {
			selectionListeners.remove(selectionListener);
		}
	}

	/**
	 * Gets a list of the GraphModelNode children objects under the root node in
	 * this diagram. If the root node is null then all the top level nodes are
	 * returned.
	 * 
	 * @return List of GraphModelNode objects
	 */
	public List getNodes() {
		return nodes;
	}

	/**
	 * Gets the root layer for this graph
	 * 
	 * @return
	 */
	public ScalableFigure getRootLayer() {
		return rootlayer;
	}

	/**
	 * Sets the default connection style.
	 * 
	 * @param connection
	 *            style the connection style to set
	 * @see org.eclipse.mylar.zest.core.widgets.ZestStyles
	 */
	public void setConnectionStyle(int connectionStyle) {
		this.connectionStyle = connectionStyle;
	}

	/**
	 * Gets the default connection style.
	 * 
	 * @return the connection style
	 * @see org.eclipse.mylar.zest.core.widgets.ZestStyles
	 */
	public int getConnectionStyle() {
		return connectionStyle;
	}

	/**
	 * Sets the default node style.
	 * 
	 * @param nodeStyle
	 *            the node style to set
	 * @see org.eclipse.mylar.zest.core.widgets.ZestStyles
	 */
	public void setNodeStyle(int nodeStyle) {
		this.nodeStyle = nodeStyle;
	}

	/**
	 * Gets the default node style.
	 * 
	 * @return the node style
	 * @see org.eclipse.mylar.zest.core.widgets.ZestStyles
	 */
	public int getNodeStyle() {
		return nodeStyle;
	}

	/**
	 * Gets the list of GraphModelConnection objects.
	 * 
	 * @return list of GraphModelConnection objects
	 */
	public List getConnections() {
		return this.connections;
	}

	/**
	 * Changes the selection to the list of items
	 * 
	 * @param l
	 */
	public void setSelection(GraphItem[] nodes) {
		clearSelection();
		if (nodes != null) {
			for (int i = 0; i < nodes.length; i++) {
				if (nodes[i] != null && nodes[i] instanceof GraphItem) {
					selectedItems.add(nodes[i]);
					(nodes[i]).highlight();
				}
			}
		}
		// TODO shouldn't this method fire a selection event?
	}

	public void selectAll() {
		clearSelection();
		for (int i = 0; i < nodes.size(); i++) {
			selectedItems.add(nodes.get(i));
			((GraphNode) nodes.get(i)).highlight();
		}
		// TODO shouldn't this method fire a selection event?
	}

	/**
	 * Gets the list of currently selected GraphNodes
	 * 
	 * @return Currently selected graph node
	 */
	public List getSelection() {
		return selectedItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Widget#toString()
	 */
	public String toString() {
		return "GraphModel {" + nodes.size() + " nodes, " + connections.size() + " connections}";
	}

	/**
	 * Dispose of the nodes and edges when the graph is disposed.
	 */
	public void dispose() {
		while (nodes.size() > 0) {
			GraphNode node = (GraphNode) nodes.get(0);
			if (node != null && !node.isDisposed()) {
				node.dispose();
			}
		}
		while (connections.size() > 0) {
			GraphConnection connection = (GraphConnection) connections.get(0);
			if (connection != null && !connection.isDisposed()) {
				connection.dispose();
			}
		}
		super.dispose();

		LIGHT_BLUE.dispose();
		LIGHT_BLUE_CYAN.dispose();
		GREY_BLUE.dispose();
		DARK_BLUE.dispose();
		LIGHT_YELLOW.dispose();
	}

	/**
	 * Runs the layout on this graph. If the view is not visible layout will be
	 * deferred until after the view is available.
	 */
	public void applyLayout() {
		scheduleLayoutOnReveal();
	}

	private void applyLayoutInternal() {
		if (layoutAlgorithm == null) {
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Animation.markBegin();
				layoutAlgorithm.applyLayout();
				layoutContext.flushChanges(false);
				Animation.run(ANIMATION_TIME);
				getLightweightSystem().getUpdateManager().performUpdate();
			}
		});
	}

	/**
	 * Sets the preferred size of the layout area. Size of ( -1, -1) uses the
	 * current canvas size.
	 * 
	 * @param width
	 * @param height
	 */
	public void setPreferredSize(int width, int height) {
		this.preferredSize = new Dimension(width, height);
	}

	/**
	 * 
	 * @return the preferred size of the layout area.
	 */
	public Dimension getPreferredSize() {
		if (preferredSize.width < 0 || preferredSize.height < 0) {
			org.eclipse.swt.graphics.Point size = getSize();
			return new Dimension(size.x, size.y);
		}
		return preferredSize;
	}

	InternalLayoutContext getLayoutContext() {
		if (layoutContext == null) {
			layoutContext = new InternalLayoutContext(this);
		}
		return layoutContext;
	}

	/**
	 * @param algorithm
	 */
	public void setLayoutAlgorithm(LayoutAlgorithm algorithm, boolean applyLayout) {
		if (this.layoutAlgorithm != null) {
			this.layoutAlgorithm.setLayoutContext(null);
		}
		this.layoutAlgorithm = algorithm;
		this.layoutAlgorithm.setLayoutContext(getLayoutContext());
		if (applyLayout) {
			applyLayout();
		}
	}

	public LayoutAlgorithm getLayoutAlgorithm() {
		return this.layoutAlgorithm;
	}

	/**
	 * Adds a filter used for hiding elements from layout algorithm.
	 * 
	 * NOTE: If a node or subgraph if filtered out, all connections adjacent to
	 * it should also be filtered out. Otherwise layout algorithm may behave in
	 * an unexpected way.
	 * 
	 * @param filter
	 *            filter to add
	 */
	public void addLayoutFilter(Filter filter) {
		getLayoutContext().addFilter(filter);
	}

	/**
	 * Removes given layout filter. If it had not been added to this graph, this
	 * method does nothing.
	 * 
	 * @param filter
	 *            filter to remove
	 */
	public void removeLayoutFilter(Filter filter) {
		getLayoutContext().removeFilter(filter);
	}

	/**
	 * Finds a figure at the location X, Y in the graph
	 * 
	 * This point should be translated to relative before calling findFigureAt
	 */
	public IFigure getFigureAt(int x, int y) {
		IFigure figureUnderMouse = this.getContents().findFigureAt(x, y, new TreeSearch() {

			public boolean accept(IFigure figure) {
				return true;
			}

			public boolean prune(IFigure figure) {
				IFigure parent = figure.getParent();
				// @tag TODO Zest : change these to from getParent to their
				// actual layer names

				if (parent == fishEyeLayer) {
					// If it node is on the fish eye layer, don't worry about
					// it.
					return true;
				}
				if (parent instanceof ContainerFigure && figure instanceof PolylineConnection) {
					return false;
				}
				if (parent == zestRootLayer || parent == zestRootLayer.getParent() || parent == zestRootLayer.getParent().getParent()) {
					return false;
				}
				GraphItem item = (GraphItem) figure2ItemMap.get(figure);
				if (item != null && item.getItemType() == GraphItem.CONTAINER) {
					return false;
				} else if (figure instanceof FreeformLayer || parent instanceof FreeformLayer || figure instanceof ScrollPane
						|| parent instanceof ScrollPane || parent instanceof ScalableFreeformLayeredPane
						|| figure instanceof ScalableFreeformLayeredPane || figure instanceof FreeformViewport || parent instanceof FreeformViewport) {
					return false;
				}
				return true;
			}

		});
		return figureUnderMouse;

	}

	private class DragSupport implements MouseMotionListener, org.eclipse.draw2d.MouseListener {

		Point lastLocation = null;
		GraphItem fisheyedItem = null;
		boolean isDragging = false;

		public void mouseDragged(org.eclipse.draw2d.MouseEvent me) {
			if (!isDragging) {
				return;
			}
			Point mousePoint = new Point(me.x, me.y);
			Point tempPoint = mousePoint.getCopy();
			if (selectedItems.size() > 0) {
				Iterator iterator = selectedItems.iterator();
				while (iterator.hasNext()) {
					GraphItem item = (GraphItem) iterator.next();
					if ((item.getItemType() == GraphItem.NODE) || (item.getItemType() == GraphItem.CONTAINER)) {
						// @tag Zest.selection Zest.move : This is where the
						// node movement is tracked
						Point pointCopy = mousePoint.getCopy();

						Point tempLastLocation = lastLocation.getCopy();
						item.getFigure().getParent().translateToRelative(tempLastLocation);
						item.getFigure().getParent().translateFromParent(tempLastLocation);

						item.getFigure().getParent().translateToRelative(pointCopy);
						item.getFigure().getParent().translateFromParent(pointCopy);
						Point delta = new Point(pointCopy.x - tempLastLocation.x, pointCopy.y - tempLastLocation.y);
						if (item.getItemType() == GraphItem.NODE || item.getItemType() == GraphItem.CONTAINER) {
							GraphNode node = (GraphNode) item;
							node.setLocation(node.getLocation().x + delta.x, node.getLocation().y + delta.y);

						}
					} else {
						// There is no movement for connection
					}
				}
				if (fisheyedFigure != null) {
					Point pointCopy = mousePoint.getCopy();

					Point tempLastLocation = lastLocation.getCopy();
					fisheyedFigure.translateToRelative(tempLastLocation);
					fisheyedFigure.translateFromParent(tempLastLocation);

					fisheyedFigure.translateToRelative(pointCopy);
					fisheyedFigure.translateFromParent(pointCopy);
					Point delta = new Point(pointCopy.x - tempLastLocation.x, pointCopy.y - tempLastLocation.y);
					Point point = new Point(fisheyedFigure.getBounds().x + delta.x, fisheyedFigure.getBounds().y + delta.y);
					fishEyeLayer.setConstraint(fisheyedFigure, new Rectangle(point, fisheyedFigure.getSize()));
					fishEyeLayer.getUpdateManager().performUpdate();
				}
			}
			lastLocation = tempPoint;
		}

		public void mouseEntered(org.eclipse.draw2d.MouseEvent me) {

		}

		public void mouseExited(org.eclipse.draw2d.MouseEvent me) {

		}

		public void mouseHover(org.eclipse.draw2d.MouseEvent me) {

		}

		/**
		 * This tracks whenever a mouse moves. The only thing we care about is
		 * fisheye(ing) nodes. This means whenever the mouse moves we check if
		 * we need to fisheye on a node or not.
		 */
		public void mouseMoved(org.eclipse.draw2d.MouseEvent me) {
			Point mousePoint = new Point(me.x, me.y);
			getRootLayer().translateToRelative(mousePoint);
			IFigure figureUnderMouse = getFigureAt(mousePoint.x, mousePoint.y);

			if (figureUnderMouse != null) {
				// There is a figure under this mouse
				GraphItem itemUnderMouse = (GraphItem) figure2ItemMap.get(figureUnderMouse);
				if (itemUnderMouse == fisheyedItem) {

				} else if (itemUnderMouse != null && itemUnderMouse.getItemType() == GraphItem.NODE) {
					fisheyedItem = itemUnderMouse;
					fisheyedFigure = ((GraphNode) itemUnderMouse).fishEye(true, true);
					if (fisheyedFigure == null) {
						// If there is no fisheye figure (this means that the
						// node does not support a fish eye)
						// then remove the fisheyed item
						fisheyedItem = null;
					}
				} else if (fisheyedItem != null) {
					((GraphNode) fisheyedItem).fishEye(false, true);
					fisheyedItem = null;
					fisheyedFigure = null;
				}
			} else {
				if (fisheyedItem != null) {
					((GraphNode) fisheyedItem).fishEye(false, true);
					fisheyedItem = null;
					fisheyedFigure = null;
				}
			}
		}

		public void mouseDoubleClicked(org.eclipse.draw2d.MouseEvent me) {

		}

		public void mousePressed(org.eclipse.draw2d.MouseEvent me) {
			isDragging = true;
			Point mousePoint = new Point(me.x, me.y);
			lastLocation = mousePoint.getCopy();

			getRootLayer().translateToRelative(mousePoint);

			if (me.getState() == org.eclipse.draw2d.MouseEvent.ALT) {
				double scale = getRootLayer().getScale();
				scale *= 1.05;
				getRootLayer().setScale(scale);
				Point newMousePoint = mousePoint.getCopy().scale(1.05);
				Point delta = new Point(newMousePoint.x - mousePoint.x, newMousePoint.y - mousePoint.y);
				Point newViewLocation = getViewport().getViewLocation().getCopy().translate(delta);
				getViewport().setViewLocation(newViewLocation);
				lastLocation.scale(scale);

				clearSelection();
				return;
			} else if (me.getState() == (org.eclipse.draw2d.MouseEvent.ALT | org.eclipse.draw2d.MouseEvent.SHIFT)) {
				double scale = getRootLayer().getScale();
				scale /= 1.05;
				getRootLayer().setScale(scale);

				Point newMousePoint = mousePoint.getCopy().scale(1 / 1.05);
				Point delta = new Point(newMousePoint.x - mousePoint.x, newMousePoint.y - mousePoint.y);
				Point newViewLocation = getViewport().getViewLocation().getCopy().translate(delta);
				getViewport().setViewLocation(newViewLocation);
				clearSelection();
				return;
			} else {
				boolean hasSelection = selectedItems.size() > 0;
				IFigure figureUnderMouse = getFigureAt(mousePoint.x, mousePoint.y);
				getRootLayer().translateFromParent(mousePoint);

				if (figureUnderMouse != null) {
					figureUnderMouse.getParent().translateFromParent(mousePoint);
				}
				// If the figure under the mouse is the canvas, and CTRL is not
				// being held down, then select
				// nothing
				if (figureUnderMouse == null || figureUnderMouse == Graph.this) {
					if (me.getState() != org.eclipse.draw2d.MouseEvent.CONTROL) {
						clearSelection();
						if (hasSelection) {
							fireWidgetSelectedEvent(null);
							hasSelection = false;
						}
					}
					return;
				}

				GraphItem itemUnderMouse = (GraphItem) figure2ItemMap.get(figureUnderMouse);
				if (itemUnderMouse == null) {
					if (me.getState() != org.eclipse.draw2d.MouseEvent.CONTROL) {
						clearSelection();
						if (hasSelection) {
							fireWidgetSelectedEvent(null);
							hasSelection = false;
						}
					}
					return;
				}
				if (selectedItems.contains(itemUnderMouse)) {
					// We have already selected this node, and CTRL is being
					// held down, remove this selection
					// @tag Zest.selection : This deselects when you have CTRL
					// pressed
					if (me.getState() == org.eclipse.draw2d.MouseEvent.CONTROL) {
						selectedItems.remove(itemUnderMouse);
						(itemUnderMouse).unhighlight();
						fireWidgetSelectedEvent(itemUnderMouse);
					}
					return;
				}

				if (me.getState() != org.eclipse.draw2d.MouseEvent.CONTROL) {
					clearSelection();
				}

				if (itemUnderMouse.getItemType() == GraphItem.NODE) {
					// @tag Zest.selection : This is where the nodes are
					// selected
					selectedItems.add(itemUnderMouse);
					((GraphNode) itemUnderMouse).highlight();
					fireWidgetSelectedEvent(itemUnderMouse);
				} else if (itemUnderMouse.getItemType() == GraphItem.CONNECTION) {
					selectedItems.add(itemUnderMouse);
					((GraphConnection) itemUnderMouse).highlight();
					fireWidgetSelectedEvent(itemUnderMouse);

				} else if (itemUnderMouse.getItemType() == GraphItem.CONTAINER) {
					selectedItems.add(itemUnderMouse);
					((GraphContainer) itemUnderMouse).highlight();
					fireWidgetSelectedEvent(itemUnderMouse);
				}
			}

		}

		public void mouseReleased(org.eclipse.draw2d.MouseEvent me) {
			isDragging = false;

		}

	}

	private void clearSelection() {
		if (selectedItems.size() > 0) {
			Iterator iterator = selectedItems.iterator();
			while (iterator.hasNext()) {
				GraphItem item = (GraphItem) iterator.next();
				item.unhighlight();
				iterator.remove();
			}
		}
	}

	private void fireWidgetSelectedEvent(Item item) {
		Iterator iterator = selectionListeners.iterator();
		while (iterator.hasNext()) {
			SelectionListener selectionListener = (SelectionListener) iterator.next();
			Event swtEvent = new Event();
			swtEvent.item = item;
			swtEvent.widget = this;
			SelectionEvent event = new SelectionEvent(swtEvent);
			selectionListener.widgetSelected(event);
		}

	}

	/**
	 * Moves the edge to the highlight layer. This moves the edge above the
	 * nodes
	 * 
	 * @param connection
	 */
	void highlightEdge(GraphConnection connection) {
		IFigure figure = connection.getConnectionFigure();
		if (figure != null && !connection.isHighlighted()) {
			zestRootLayer.highlightConnection(figure);
		}
	}

	/**
	 * Moves the edge from the edge feedback layer back to the edge layer
	 * 
	 * @param graphConnection
	 */
	void unhighlightEdge(GraphConnection connection) {
		IFigure figure = connection.getConnectionFigure();
		if (figure != null && connection.isHighlighted()) {
			zestRootLayer.unHighlightConnection(figure);
		}
	}

	/**
	 * Moves the node onto the node feedback layer
	 * 
	 * @param node
	 */
	void highlightNode(GraphNode node) {
		IFigure figure = node.getNodeFigure();
		if (figure != null && !node.isHighlighted()) {
			zestRootLayer.highlightNode(figure);
		}
	}

	/**
	 * Moves the node off the node feedback layer
	 * 
	 * @param node
	 */
	void unhighlightNode(GraphNode node) {
		IFigure figure = node.getNodeFigure();
		if (figure != null && node.isHighlighted()) {
			zestRootLayer.unHighlightNode(figure);
		}
	}

	/**
	 * Converts the list of GraphModelConnection objects into an array and
	 * returns it.
	 * 
	 * @return GraphModelConnection[]
	 */
	GraphConnection[] getConnectionsArray() {
		GraphConnection[] connsArray = new GraphConnection[connections.size()];
		connsArray = (GraphConnection[]) connections.toArray(connsArray);
		return connsArray;
	}

	void removeConnection(GraphConnection connection) {
		IFigure figure = connection.getConnectionFigure();
		PolylineConnection sourceContainerConnectionFigure = connection.getSourceContainerConnectionFigure();
		PolylineConnection targetContainerConnectionFigure = connection.getTargetContainerConnectionFigure();
		connection.removeFigure();
		this.getConnections().remove(connection);
		figure2ItemMap.remove(figure);
		if (sourceContainerConnectionFigure != null) {
			figure2ItemMap.remove(sourceContainerConnectionFigure);
		}
		if (targetContainerConnectionFigure != null) {
			figure2ItemMap.remove(targetContainerConnectionFigure);
		}
		getLayoutContext().fireConnectionRemovedEvent(connection.getLayout());
	}

	void removeNode(GraphNode node) {
		IFigure figure = node.getNodeFigure();
		if (figure.getParent() != null) {
			if (figure.getParent() instanceof ZestRootLayer) {
				((ZestRootLayer) figure.getParent()).removeNode(figure);
			} else {
				figure.getParent().remove(figure);
			}
		}
		this.getNodes().remove(node);
		figure2ItemMap.remove(figure);
		getLayoutContext().fireNodeRemovedEvent(node.getLayout());
	}

	void addConnection(GraphConnection connection, boolean addToEdgeLayer) {
		this.getConnections().add(connection);
		if (addToEdgeLayer) {
			zestRootLayer.addConnection(connection.getFigure());
		}
		getLayoutContext().fireConnectionAddedEvent(connection.getLayout());
	}

	void addNode(GraphNode node) {
		this.getNodes().add(node);
		zestRootLayer.addNode(node.getFigure());
		getLayoutContext().fireNodeAddedEvent(node.getLayout());
	}

	void registerItem(GraphItem item) {
		if (item.getItemType() == GraphItem.NODE) {
			IFigure figure = item.getFigure();
			figure2ItemMap.put(figure, item);
		} else if (item.getItemType() == GraphItem.CONNECTION) {
			IFigure figure = item.getFigure();
			figure2ItemMap.put(figure, item);
			if (((GraphConnection) item).getSourceContainerConnectionFigure() != null) {
				figure2ItemMap.put(((GraphConnection) item).getSourceContainerConnectionFigure(), item);
			}
			if (((GraphConnection) item).getTargetContainerConnectionFigure() != null) {
				figure2ItemMap.put(((GraphConnection) item).getTargetContainerConnectionFigure(), item);
			}
		} else if (item.getItemType() == GraphItem.CONTAINER) {
			IFigure figure = item.getFigure();
			figure2ItemMap.put(figure, item);
		} else {
			throw new RuntimeException("Unknown item type: " + item.getItemType());
		}
	}

	/**
	 * Schedules a layout to be performed after the view is revealed (or
	 * immediately, if the view is already revealed).
	 * 
	 * @param revealListener
	 */
	private void scheduleLayoutOnReveal() {

		final boolean[] isVisibleSync = new boolean[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				isVisibleSync[0] = isVisible();
			}
		});

		if (isVisibleSync[0]) {
			applyLayoutInternal();
		} else {
			isLayoutScheduled = true;
		}
	}

	private ScalableFigure createLayers() {
		rootlayer = new ScalableFreeformLayeredPane();
		rootlayer.setLayoutManager(new FreeformLayout());
		zestRootLayer = new ZestRootLayer();

		zestRootLayer.setLayoutManager(new FreeformLayout());

		fishEyeLayer = new ScalableFreeformLayeredPane();
		fishEyeLayer.setLayoutManager(new FreeformLayout());

		rootlayer.add(zestRootLayer);
		rootlayer.add(fishEyeLayer);

		zestRootLayer.addLayoutListener(LayoutAnimator.getDefault());
		fishEyeLayer.addLayoutListener(LayoutAnimator.getDefault());
		return rootlayer;
	}

	/**
	 * This removes the fisheye from the graph. It uses an animation to make the
	 * fisheye shrink, and then it finally clears the fisheye layer. This
	 * assumes that there is ever only 1 node on the fisheye layer at any time.
	 * 
	 * @param fishEyeFigure
	 *            The fisheye figure
	 * @param regularFigure
	 *            The regular figure (i.e. the non fisheye version)
	 */
	void removeFishEye(final IFigure fishEyeFigure, final IFigure regularFigure, boolean animate) {

		if (!fishEyeLayer.getChildren().contains(fishEyeFigure)) {
			return;
		}
		if (animate) {
			Animation.markBegin();
		}

		Rectangle bounds = regularFigure.getBounds().getCopy();
		regularFigure.translateToAbsolute(bounds);

		double scale = rootlayer.getScale();
		fishEyeLayer.setScale(1 / scale);
		fishEyeLayer.translateToRelative(bounds);
		fishEyeLayer.translateFromParent(bounds);

		fishEyeLayer.setConstraint(fishEyeFigure, bounds);

		if (animate) {
			Animation.run(FISHEYE_ANIMATION_TIME * 2);
		}
		this.getRootLayer().getUpdateManager().performUpdate();
		fishEyeLayer.removeAll();
		this.fisheyedFigure = null;

	}

	/**
	 * Replaces the old fisheye figure with a new one.
	 * 
	 * @param oldFigure
	 * @param newFigure
	 */
	boolean replaceFishFigure(IFigure oldFigure, IFigure newFigure) {
		if (this.fishEyeLayer.getChildren().contains(oldFigure)) {
			Rectangle bounds = oldFigure.getBounds();
			newFigure.setBounds(bounds);
			this.fishEyeLayer.remove(oldFigure);
			this.fishEyeLayer.add(newFigure);
			this.fisheyedFigure = newFigure;
			return true;
		}
		return false;
	}

	/**
	 * Add a fisheye version of the node. This works by animating the change
	 * from the original node to the fisheyed one, and then placing the fisheye
	 * node on the fisheye layer.
	 * 
	 * @param startFigure
	 *            The original node
	 * @param endFigure
	 *            The fisheye figure
	 * @param newBounds
	 *            The final size of the fisheyed figure
	 */
	void fishEye(IFigure startFigure, IFigure endFigure, Rectangle newBounds, boolean animate) {

		fishEyeLayer.removeAll();
		fisheyedFigure = null;
		if (animate) {
			Animation.markBegin();
		}

		double scale = rootlayer.getScale();
		fishEyeLayer.setScale(1 / scale);

		fishEyeLayer.translateToRelative(newBounds);
		fishEyeLayer.translateFromParent(newBounds);

		Rectangle bounds = startFigure.getBounds().getCopy();
		startFigure.translateToAbsolute(bounds);
		// startFigure.translateToRelative(bounds);
		fishEyeLayer.translateToRelative(bounds);
		fishEyeLayer.translateFromParent(bounds);

		endFigure.setLocation(bounds.getLocation());
		endFigure.setSize(bounds.getSize());
		fishEyeLayer.add(endFigure);
		fishEyeLayer.setConstraint(endFigure, newBounds);

		if (animate) {
			Animation.run(FISHEYE_ANIMATION_TIME);
		}
		this.getRootLayer().getUpdateManager().performUpdate();
	}

	public int getItemType() {
		return GraphItem.GRAPH;
	}

	GraphItem getGraphItem(IFigure figure) {
		return (GraphItem) figure2ItemMap.get(figure);
	}

	public void setExpanded(GraphNode node, boolean expanded) {
		layoutContext.setExpanded(node.getLayout(), expanded);
	}
}
