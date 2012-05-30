package net.ulrice.dashboard.module;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.ulrice.dashboard.Dashboard;
import net.ulrice.dashboard.DashboardComponent;
import net.ulrice.dashboard.UlriceDashboard;

/**
 * The view of the dashboard module
 * 
 * @author christof
 */
public class VDashboard implements Dashboard, MouseMotionListener {

    /**
     * This enum contains information of different modification of components on the dashboard.
     * 
     * @author dv20jac
     */
    enum Mode {
        /** The component is located in resize mode */
        RESIZE,
        /** The component is dragged on the dashboard */
        DRAG,
        /** The component is dragged fromm the commandbar on the dashboard */
        MODULEDRAG,
        /** Default value */
        NON
    }

    /** The logger used by this class. */
    private static final Logger LOG = Logger.getLogger(VDashboard.class.getName());
    
    /** List of cell components */
    private List<CellComponent> dashboard;
    
    /** the default space between components on the grid*/
    private int spaceBetweenCell = 1;

    /** The view component. */
    private JPanel view;

    /** The dashboard controller. */
    private CDashboard controller;

    /** Number of cell's on the x-axis */
    private int numberOfCellX;

    /** Number of cell's on the y-axis */
    private int numberOfCellY;

    /** The current selected component during the drag operation */
    private CellComponent selectedCellComponent;

    /** The latest visited component */
    private CellComponent latestVisitedCellComponent;

    /** */
    private int xAdjustment;

    /** */
    private int yAdjustment;

    /** The x position of the dragged component in cell unit */
    private int dragCellX;

    /** The y position of the dragged component in cell unit */
    private int dragCellY;

    /** The dragged component */
    private JComponent draggedView;

    /** The current mode */
    private Mode currentMode;

    /** Backup of the size */
    private Rectangle selectedComponentBak;

    /** Threshold for click events on a component */
    public static int clickThreshold = 10;

    /** The grid color */
    private Color gridColor = new Color(210, 210, 210);
    
    private boolean initialized;

    {
        currentMode = Mode.NON;
    }


    /**
     * @see net.ulrice.module.IFView#getView()
     */
    public JComponent getView() {
        return view;
    }

    /**
     * @see net.ulrice.module.IFView#initialize()
     */
    public void initialize(CDashboard controller) {
    	this.controller = controller;

        // dash pattern 1 visible, 2 invisible . . . .
        float dashPattern[] = { 1, 2 };
        final BasicStroke dashed =
                new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f);

        view = new JPanel() {

            /**
             * The generated serial id
             */
            private static final long serialVersionUID = -8440110307904478770L;

            /**
             * {@inheritDoc}
             * 
             * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
             */
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;

                // paint the background
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, (int) view.getSize().getWidth(), (int) view.getSize().getHeight());

                // paint the grid
                g2.setColor(gridColor);
                Stroke stroke = g2.getStroke();
                g2.setStroke(dashed);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                double y = getCellSize();
                numberOfCellX = 0;
                numberOfCellY = 0;
                if (y > 0) {
                    while (y < view.getSize().getHeight()) {
                        if (!currentMode.equals(Mode.NON)) {
                            g2.drawLine(0, (int) y, (int) view.getSize().getWidth(), (int) y);
                        }
                        y += getCellSize();
                        numberOfCellY++;
                    }
                }
                y = getCellSize();
                if (y > 0) {
                    while (y < view.getSize().getWidth()) {
                        if (!currentMode.equals(Mode.NON)) {
                            g2.drawLine((int) y, 0, (int) y, (int) view.getSize().getHeight());
                        }
                        y += getCellSize();
                        numberOfCellX++;
                    }
                }
                g2.setStroke(stroke);
            }
        };
        view.addComponentListener(new DashboardListener());
        view.addMouseListener(new DashboardMouseAdapter());
        view.addMouseMotionListener(this);
        view.setLayout(null);
        view.setBackground(Color.WHITE);
        view.setDropTarget(new DropTarget(view, new DashbordDropTargetListener()));
    }

    /**
     * Add the UI component of an module on the dashbord. It creates the <code>CellComponent</code> an add it to the
     * list. Also the position on the dashbord will be stored.
     * 
     * @param ifController The controller of a module
     */
    private void addModuleToDashboard(DashboardComponent dashboard) {

        JComponent component = dashboard.getDashboardComponent();

        component.setSize(dashboard.getDashboardSize());

        Cell startCell = new Cell(dragCellX, dragCellY);
        Cell endCell =
                new Cell(startCell.getX() + getExpectedCellX(component.getSize().getWidth()), startCell.getY()
                    + getExpectedCellY(component.getSize().getHeight()));

        CellComponent cellComponent = new CellComponent(component, dashboard, startCell, endCell);

        cellComponent.setWidth(endCell.getX() - startCell.getX() + 1);
        cellComponent.setHeight(endCell.getY() - startCell.getY() + 1);

        controller.addDashboardComponent(dashboard.getUniqueId(), cellComponent);

        // draw the new added component
        int startX = (int) getXPosition(startCell);
        int startY = (int) getYPosition(startCell);

        component.setBounds(startX, startY, (int) (cellComponent.getWidth() * getCellSize()),
            (int) (cellComponent.getHeight() * getCellSize()));

        view.add(component);

        controller.saveDashBoardComponentProperties(cellComponent);

        view.revalidate();
        view.repaint();
    }
    
    private void placeDashboardComponentOnDashboard(CellComponent cellComponent) {
        JComponent component = cellComponent.getJComponent();
        
        cellComponent.setWidth(cellComponent.getEndCell().getX() - cellComponent.getStartCell().getX() + 1);
        cellComponent.setHeight(cellComponent.getEndCell().getY() - cellComponent.getStartCell().getY() + 1);

        controller.addDashboardComponent(cellComponent.getDashboardComponent().getUniqueId(), cellComponent);

        // draw the new added component
        int startX = (int) getXPosition(cellComponent.getStartCell());
        int startY = (int) getYPosition(cellComponent.getStartCell());
        
        component.setBounds(startX, startY, (int) (cellComponent.getWidth() * getCellSize()),
            (int) (cellComponent.getHeight() * getCellSize()));

        view.add(component);

        controller.saveDashBoardComponentProperties(cellComponent);

        view.revalidate();
        view.repaint();
    }

    /**
     * Restores a module that are saved in a properties file or database
     * 
     * @param controller The controller of the module that has to be restored
     * @param restoringInformation Special information string the contains the start and end cell's encoded
     */
    private void restoreModule(DashboardComponent dashboardComponent, String restoringInformation) {
        // analyze string and split the information
        JComponent component = dashboardComponent.getDashboardComponent();// ifView.getView();
        Cell startCell = null;
        Cell endCell = null;

        String[] cells = restoringInformation.split("-");
        String[] sCell = null;
        String[] eCell = null;
        if (cells.length == 2) {
            sCell = cells[0].split(";");
            eCell = cells[1].split(";");
        }

        if (sCell != null && sCell.length == 2) {
            startCell = new Cell(Integer.parseInt(sCell[0]), Integer.parseInt(sCell[1]));
        }
        if (eCell != null && eCell.length == 2) {
            endCell = new Cell(Integer.parseInt(eCell[0]), Integer.parseInt(eCell[1]));
        }
        if (startCell != null && endCell != null) {
            CellComponent cellComponent =
                    new CellComponent(component, dashboardComponent, startCell, endCell);

            cellComponent.setWidth(endCell.getX() - startCell.getX() + 1);
            cellComponent.setHeight(endCell.getY() - startCell.getY() + 1);

            controller.addDashboardComponent(dashboardComponent.getUniqueId(), cellComponent);

            // draw the new added component
            int startX = (int) getXPosition(startCell);
            int startY = (int) getYPosition(startCell);

            component.setBounds(startX, startY, (int) (cellComponent.getWidth() * getCellSize()),
                (int) (cellComponent.getHeight() * getCellSize()));
            
            view.add(component);

            view.revalidate();
            view.repaint();
        }
        else {
            LOG.log(Level.SEVERE, "Configuration exception occurred.");
        }
    }

    /**
     * Returns the cell size for the grid
     * 
     * @return The cell size
     */
    private double getCellSize() {
        return Cell.CELLSIZE;
    }
    
    private int getDashboardWidthInCellUnits() {
        return numberOfCellX;
    }
    
    private int getDashboardHeightInCellUnits() {
        return numberOfCellY;
    }

    /**
     * Returns the x-cell position by a given x position from the <code>JComponent<code> coordinate system.
     * 
     * @param x The x position from the <code>JComponent</code> coordinate system
     * @return The x cell position
     */
    private int getCellPositionX(int x) {
        boolean found = false;
        int xCell = 0;
        int h = 0;
        while (!found) {
            if (x > h && x <= h + (int) getCellSize()) {
                found = true;
            }
            h += (int) getCellSize();
            xCell++;
        }
        return xCell;
    }

    /**
     * Returns the y-cell position by a given y position from the <code>JComponent<code> coordinate system.
     * 
     * @param y The y position from the <code>JComponent</code> coordinate system
     * @return The y cell position
     */
    private int getCellPositionY(int y) {
        boolean found = false;
        int yCell = 0;
        int h = 0;
        while (!found) {
            if (y >= h && y <= h + (int) getCellSize()) {
                found = true;
            }
            h += (int) getCellSize();
            yCell++;
        }
        return yCell;
    }

    /**
     * Returns the expected width in cell unit.
     * 
     * @param width The width from the jpanel
     * @return The width in cell's
     */
    private int getExpectedCellX(double width) {
        int cellNumber = 1;
        double cellSize = getCellSize();
        boolean found = false;
        while (!found) {
            if (width < (cellNumber * cellSize)) {
                found = true;
            }
            else {
                cellNumber++;
            }
        }
        return cellNumber;
    }

    /**
     * Returns the expected height in cell unit.
     * 
     * @param height The height from the jcomponent
     * @return The height in cell's
     */
    private int getExpectedCellY(double height) {
        int cellNumber = 1;
        boolean found = false;
        while (!found) {
            if (height < (cellNumber * getCellSize())) {
                found = true;
            }
            else {
                cellNumber++;
            }
        }
        return cellNumber;
    }

    /**
     * Translate cell position into real system of <code>JComponent</code>
     * 
     * @param cell The cell position
     * @return The translated x position
     */
    private double getXPosition(Cell cell) {
        return (cell.getX() -1) * getCellSize();
    }

    /**
     * Translate cell position into real system of <code>JComponent</code>
     * 
     * @param cell The cell position
     * @return The translated y position
     */
    private double getYPosition(Cell cell) {
        return (cell.getY() -1) * getCellSize();//- 1
    }

    /**
     * Removes a <code>CellComponent</code> from the list an from the dashboard panel. Also unregister the module
     * 
     * @param cellComponent The component that is to be deleted.
     */
    private void removeModuleFromDashboard(CellComponent cellComponent) {
        // remove from dashboard view
        view.remove(cellComponent.getJComponent());
        view.revalidate();
        view.repaint();

        // inform listener about removing
        controller.removeDashbordModuleEvent(cellComponent.getDashboardComponent());

        // remove informations from properties file
         controller.deleteDashBoardComponent(cellComponent.getDashboardComponent().getUniqueId());
         controller.deleteDashBoardComponentProperties(cellComponent.getDashboardComponent().getUniqueId());
    }

    /**
     * Find the right <code>IFController</code> by the module id.
     * 
     * @param moduleId
     */
    private void loadModule(String uniqueId) {
        for (DashboardComponent dashboardComponent : controller.getDashboardComponentList()) {
            if (uniqueId.equals(dashboardComponent.getUniqueId())) {
                addModuleToDashboard(dashboardComponent);
            }
        }
    }

    
    /**
     * Restores the modules that are placed on the dashbord before. At first the
     * information are received from the properties file later by a database.
     */
    @Override
    public void restoreModules() {
//        List<String> list = new ArrayList<String>();
//        for (DashboardComponent dashboardComponent : controller.getDashboardComponentProvider().getDashboardComponentList()) {
//            String value = UlriceDashboard.getSettings().getValue(dashboardComponent.getUniqueId());
//            list.add(value);
//            if (value != null) {
//                restoreModule(dashboardComponent, value);
//            }
//        }
    }
    
    /**
     * The algorithm to order dashboard components on the dashboard.
     * The strategy is from left to right and top to bottom
     * 
     * @param list Complete list of dashboard components
     */
    private void binPack(List<DashboardComponent> list) {
        
        List<Cell> possibleStartCell = new ArrayList<Cell>();
        dashboard = new ArrayList<CellComponent>();
        
        int startX = 1 + spaceBetweenCell;
        int startY = 1 + spaceBetweenCell;
        
        
        int dashboardWidth = getDashboardWidthInCellUnits();
        int dashboardHeight = getDashboardHeightInCellUnits();

        A:
        for(DashboardComponent dashboardComponent : list) {
            //calculate width and height
            int componentWidthInXCell = getExpectedCellX(dashboardComponent.getDashboardSize().getWidth());
            int componentHeightInYCell = getExpectedCellY(dashboardComponent.getDashboardSize().getHeight());
            
            Collections.sort(possibleStartCell);
            //dashboard is clean
            if (possibleStartCell.isEmpty()) {
                if ((startX + componentWidthInXCell -1) < dashboardWidth 
                        && (startX+ componentHeightInYCell -1) < dashboardHeight) {
                    
                    dashboard.add(new CellComponent(dashboardComponent.getDashboardComponent(), 
                        dashboardComponent, new Cell(startX, startY), 
                        new Cell(startX + componentWidthInXCell -1, componentHeightInYCell)));   
                    
                    //possible start cell
                    possibleStartCell.add(new Cell(startX, startY + componentHeightInYCell));
                    
                    //new pos below this component
                    startX = startX + componentWidthInXCell + spaceBetweenCell;
                    possibleStartCell.add(new Cell(startX, startY));
                }
            }
            else {
                for (Cell posStartCell : possibleStartCell) {
                    if ((posStartCell.getX() + componentWidthInXCell - 1) < dashboardWidth) {
                        //not 1. row, collision with lower  comps is possible
                        if (posStartCell.getY() > 1) {

                            Cell expectedEndCellX = new Cell((posStartCell.getX() + componentWidthInXCell -1), 
                                posStartCell.getY());

                            // find the startCell
                            Cell newCell = findOptimalStartCell(posStartCell, expectedEndCellX);
                            dashboard.add(new CellComponent(dashboardComponent.getDashboardComponent(), 
                                dashboardComponent, 
                                newCell, 
                                new Cell(newCell.getX() + componentWidthInXCell - 1, 
                                    newCell.getY() + componentHeightInYCell -1)));
                            
                            possibleStartCell.remove(posStartCell);
                            
                            //add new possible start position
                            Cell nextPossibleStartCell = findBestStartPosition(new Cell(newCell.getX() + 
                                componentWidthInXCell + 1, newCell.getY()));
                            possibleStartCell.add(nextPossibleStartCell);
                            
                            // if the new component is placed on the left side, add a additional start position
                            if (posStartCell.getX() == 2) {
                                possibleStartCell.add(new Cell(posStartCell.getX(), 
                                    posStartCell.getY() + componentHeightInYCell +1));
                            }
                            continue A;
                        }
                        else {
                            dashboard.add(new CellComponent(dashboardComponent.getDashboardComponent(), dashboardComponent, 
                                posStartCell, new Cell(startX + componentWidthInXCell - 1, componentHeightInYCell)));
                            
                            startX = startX + componentWidthInXCell + spaceBetweenCell ;
                            
                            possibleStartCell.add(new Cell(startX, startY));
                            possibleStartCell.remove(posStartCell);
                            continue A;
                        }
                    }
                }
            }
        }
        
        for (CellComponent cellComponent : dashboard) {
            placeDashboardComponentOnDashboard(cellComponent);
        }
        
        dashboard = null;
        possibleStartCell = null;
        initialized = true;
    }
    
    /**
     * If a collision occurred then look for the next possible start position along 
     * the y-axis or x-axis.
     * 
     * @param startCell The possible start position of the component
     * @param expectedEndCellX The expected end position along the x-axis
     * 
     * @return If a collision is detected then an correction along the y-axis will be done
     */
    private Cell findOptimalStartCell(Cell startCell, Cell expectedEndCellX) {
        int differenceY = 0;
        Cell newStartCell = new Cell(startCell.getX(), startCell.getY());
        
        for (int i = dashboard.size() - 1; i >= 0; i--) {
            CellComponent cellComponent = dashboard.get(i);
            if (cellComponent.getEndCell().getX() < startCell.getX()) {
                continue;
            }
            //expected end cell is higher then the end y position of another component
            if (expectedEndCellX.getX() >= cellComponent.getStartCell().getX() //if (expectedEndCellX.getX() >= cellComponent.getStartCell().getX() 
                    && expectedEndCellX.getY() <= (cellComponent.getEndCell().getY() + spaceBetweenCell)) {
                
                differenceY = (((cellComponent.getEndCell().getY() + spaceBetweenCell) - expectedEndCellX.getY() + 1) > differenceY) ? 
                        ((cellComponent.getEndCell().getY() + spaceBetweenCell) - expectedEndCellX.getY() + 1) : differenceY;
            }
            else if ((expectedEndCellX.getX() + spaceBetweenCell) >= cellComponent.getStartCell().getX() 
                    && expectedEndCellX.getY() <= cellComponent.getEndCell().getY()) {
                if ((cellComponent.getEndCell().getY() - expectedEndCellX.getY()) >= 0) {
                    
                    differenceY = (((cellComponent.getEndCell().getY() - expectedEndCellX.getY()) + 2) > differenceY)? 
                            (((cellComponent.getEndCell().getY() - expectedEndCellX.getY()) + 2)) : differenceY;
                }
            }
        }
        newStartCell.setY(newStartCell.getY() + differenceY);
        return newStartCell;
    }
    
    /**
     * Find the optimal next start position for a given component. In the case that the
     * first try is not valid, take a look for the next possible position. The strategy for the
     * search is to analyze the component where a collision is occurred. There a two ways. 
     * At first take a look in x-axis and then y-axis. If a possible start position lays on a 
     * component then analyze the area. Is the hit inside the upper 2/3 of the height the look 
     * for the next position along the x-axis otherwise the y-axis.
     * 
     * @param cellToTest The first position for the start cell
     * 
     * @return The correction of the cell position or the first try if fine
     */
    private Cell findBestStartPosition(Cell cellToTest) {
        int differenceY = 0;
        //ignore last position. this is the recently placed component
        for (int i = dashboard.size() -2; i >= 0 ; i--) {
            CellComponent cellComponent = dashboard.get(i);
            //cellToTest is located inside of an other component
            if ((cellToTest.getX() <= cellComponent.getEndCell().getX() 
                    && cellToTest.getX() >= cellComponent.getStartCell().getX()) 
                    && (cellToTest.getY() <= cellComponent.getEndCell().getY() 
                    && cellToTest.getY() >= cellComponent.getStartCell().getY())) {
                
                int height = cellComponent.getEndCell().getY() - cellComponent.getStartCell().getY() + 1;
                
                //check y position to find an other position
                if (cellToTest.getY() <= (height / 3) * 2) {
                    cellToTest.setX(cellComponent.getEndCell().getX() + spaceBetweenCell + 1);
                    //call recursive because the new position is to check as well for another collision
                    findBestStartPosition(cellToTest);
                }
                else {
                    differenceY = ((cellComponent.getEndCell().getY() + spaceBetweenCell) - cellToTest.getY());
                    differenceY += 1;
                    cellToTest.setY(cellToTest.getY() + differenceY);
                    break;
                }
                
            }
            //outside of an component but on the space buffer between components
            else if ((cellToTest.getX() <= cellComponent.getEndCell().getX() 
                    && cellToTest.getX()>= cellComponent.getStartCell().getX()) 
                    && cellComponent.getEndCell().getY() + spaceBetweenCell >= cellToTest.getY()
                    && !(cellToTest.getY() <= cellComponent.getEndCell().getY() 
                    && cellToTest.getY() >= cellComponent.getStartCell().getY())) {
                differenceY = ((cellComponent.getEndCell().getY() + spaceBetweenCell) - cellToTest.getY()) + 1;
                cellToTest.setY(cellToTest.getY() + differenceY);
                break;
            }
        }
        return cellToTest;
    }
    
    /**
     * 
     * TODO: description
     *
     * @author dv20jac
     *
     */
    private class DashboardListener extends ComponentAdapter {
       
        /**
         * 
         * {@inheritDoc}
         * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
         */
        public void componentResized(ComponentEvent ev) {
            if (getDashboardWidthInCellUnits() > 0  && !initialized) {
                int countStoredModules = 0;
                for (DashboardComponent dashboardComponent : controller.getDashboardComponentList()) {
                    String value = UlriceDashboard.getSettings().getValue(dashboardComponent.getUniqueId());
                    if (value != null) {
                        countStoredModules++;
                    }
                }

                if (controller.getDashboardComponentList().size() != countStoredModules) {
                    binPack(controller.getDashboardComponentList());
                }
                else {
                    for (DashboardComponent dashboardComponent : controller.getDashboardComponentList()) {
                        String value = UlriceDashboard.getSettings().getValue(dashboardComponent.getUniqueId());
                        if (value != null) {
                            restoreModule(dashboardComponent, value);
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent ev) {
        if (selectedCellComponent == null) {
            return;
        }

        switch (currentMode) {
            case DRAG:
                selectedCellComponent.getJComponent().setLocation(ev.getX() + xAdjustment, ev.getY() + yAdjustment);
                break;
            case RESIZE:
                double startX = getXPosition(selectedCellComponent.getStartCell());
                double startY = getYPosition(selectedCellComponent.getStartCell());

                double newWidth = (ev.getX() - startX);
                double newHeight = (ev.getY() - startY);

                selectedCellComponent.getJComponent().setBounds(selectedCellComponent.getJComponent().getX(),
                    selectedCellComponent.getJComponent().getY(), (int) newWidth, (int) newHeight);
                break;
            default:
                break;
        }
        view.repaint();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent ev) {
        CellComponent component =
                this.controller.getDashBoardComponent(getCellPositionX(ev.getX()), getCellPositionY(ev.getY()));

        if (component != null) {
            latestVisitedCellComponent = component;
            double startX = getXPosition(component.getStartCell());
            double startY = getYPosition(component.getStartCell());
            double modulePosX = (ev.getX() - startX);
            double modulePosY = (ev.getY() - startY);
            if ((int) modulePosY >= (component.getJComponent().getHeight() - clickThreshold)
                && (int) modulePosX >= (component.getJComponent().getWidth() - clickThreshold)) {
                component.getDashboardComponent().resizingEnabled(true);
            }
            else {
                component.getDashboardComponent().resizingEnabled(false);
            }

            if ((int) modulePosX >= (component.getJComponent().getWidth() - 15)
                && (modulePosY >= 1 && modulePosY <= 15)) {
                component.getDashboardComponent().closingEnabled(true);
            }
            else {
                component.getDashboardComponent().closingEnabled(false);
            }
            component.getJComponent().repaint();
            return;
        }

        // remove the resizing or closing icon from the latest visited component
        if (latestVisitedCellComponent != null) {
            latestVisitedCellComponent.getDashboardComponent().resizingEnabled(false);
            latestVisitedCellComponent.getDashboardComponent().closingEnabled(false);
            latestVisitedCellComponent.getJComponent().repaint();
            latestVisitedCellComponent = null;
        }
    }
    
    private class DashboardMouseAdapter extends MouseAdapter {
        /**
         * {@inheritDoc}
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent ev) {
            CellComponent component =
                    VDashboard.this.controller.getDashBoardComponent(getCellPositionX(ev.getX()), getCellPositionY(ev.getY()));

            if (component != null) {

                double startX = getXPosition(component.getStartCell());
                double startY = getYPosition(component.getStartCell());
                double modulePosX = (ev.getX() - startX);
                double modulePosY = (ev.getY() - startY);

                if ((int) modulePosY >= (component.getJComponent().getHeight() - clickThreshold)
                    && (int) modulePosX >= (component.getJComponent().getWidth() - clickThreshold)) {
                    selectedCellComponent = component;
                    currentMode = Mode.RESIZE;

                    component.getDashboardComponent().resizingEnabled(true);
                    selectedComponentBak = selectedCellComponent.getJComponent().getBounds();
                }
                else if ((int) modulePosX >= (component.getJComponent().getWidth() - 15)
                    && (modulePosY >= 1 && modulePosY <= 15)) {

                    removeModuleFromDashboard(component);
                }
                else {
                    component.getDashboardComponent().resizingEnabled(false);
                    selectedCellComponent = component;
                    currentMode = Mode.DRAG;

                    xAdjustment = selectedCellComponent.getJComponent().getLocation().x - ev.getX();
                    yAdjustment = selectedCellComponent.getJComponent().getLocation().y - ev.getY();
                }
            }
        }
        
        /**
         * {@inheritDoc}
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent ev) {
            if (selectedCellComponent == null) {
                return;
            }
            Rectangle rectangle;
            switch (currentMode) {
                case DRAG:
                    int sXCell = getCellPositionX((int) selectedCellComponent.getJComponent().getLocation().getX() + 1);
                    int sYCell = getCellPositionY((int) selectedCellComponent.getJComponent().getLocation().getY() + 1);
                    int eXCell = sXCell + selectedCellComponent.getWidth() - 1;
                    int eYCell = sYCell + selectedCellComponent.getHeight() - 1;
                    int offSet = 10;

                    rectangle = selectedCellComponent.getJComponent().getBounds();
                    rectangle.setFrame((int) (rectangle.getX() - (1 * getCellSize())),
                        (int) (rectangle.getY() - (1 * getCellSize())), rectangle.getWidth() + (2 * getCellSize())
                            - offSet, rectangle.getHeight() + (2 * getCellSize()) - offSet);

                    if (!VDashboard.this.controller.checkCollision(
                        selectedCellComponent.getDashboardComponent().getUniqueId(), rectangle)
                        && (sXCell > 1 && sXCell <= numberOfCellX)
                        && (sYCell > 1 && sYCell <= numberOfCellY)
                        && (eXCell > 1 && eXCell < numberOfCellX) && (eYCell > 1 && eYCell < numberOfCellY)) {

                        selectedCellComponent.getEndCell().setX(eXCell);
                        selectedCellComponent.getEndCell().setY(eYCell);

                        selectedCellComponent.getStartCell().setX(sXCell);
                        selectedCellComponent.getStartCell().setY(sYCell);

                        selectedCellComponent.paintComponent();

                        // save new position into properties file
                        controller.saveDashBoardComponentProperties(selectedCellComponent);
                    }
                    else {
                        // set old position
                        selectedCellComponent.paintComponent();
                    }
                    break;
                case RESIZE:
                    selectedCellComponent.getDashboardComponent().resizingEnabled(false);
                    rectangle = selectedCellComponent.getJComponent().getBounds();
                    rectangle.setFrame((int) (rectangle.getX()), (int) (rectangle.getY()), rectangle.getWidth()
                        + getCellSize(), rectangle.getHeight() + getCellSize());
                    Cell temporaryEndCell =
                            new Cell(selectedCellComponent.getStartCell().getX()
                                + (getExpectedCellX(selectedCellComponent.getJComponent().getSize().getWidth()) - 1),
                                selectedCellComponent.getStartCell().getY()
                                    + (getExpectedCellY(selectedCellComponent.getJComponent().getSize().getHeight()) - 1));

                    if (!VDashboard.this.controller.checkCollision(selectedCellComponent.getDashboardComponent()
                        .getUniqueId(), rectangle)
                        && (temporaryEndCell.getX() > 1 && temporaryEndCell.getX() <= numberOfCellX)
                        && (temporaryEndCell.getY() > 1 && temporaryEndCell.getY() <= numberOfCellY)) {

                        // sets the new end position
                        selectedCellComponent.setEndCell(temporaryEndCell);

                        // sets the new width, height and bounds
                        selectedCellComponent.setWidth(temporaryEndCell.getX()
                            - selectedCellComponent.getStartCell().getX() + 1);
                        selectedCellComponent.setHeight(temporaryEndCell.getY()
                            - selectedCellComponent.getStartCell().getY() + 1);

                        int startX = (int) getXPosition(selectedCellComponent.getStartCell());
                        int startY = (int) getYPosition(selectedCellComponent.getStartCell());

                        selectedCellComponent.getJComponent().setBounds(startX, startY,
                            (int) ((selectedCellComponent.getWidth()) * getCellSize()),
                            (int) ((selectedCellComponent.getHeight()) * getCellSize()));

                        controller.saveDashBoardComponentProperties(selectedCellComponent);
                    }
                    else {
                        selectedCellComponent.getJComponent().setBounds(selectedComponentBak);
                    }
                    break;
                default:
                    break;
            }
            view.revalidate();
            view.repaint();

            selectedCellComponent = null;
            currentMode = Mode.NON;
        }
    }

    /**
     * The Cell class represents the position on the dashboard grid. It contains the x-axis and y-axis information.
     * The height and width of the cell itself contains the variable CELLSIZE inside the class VDashboard.
     * 
     * @author dv20jac
     */
    protected class Cell implements Comparable<Cell>{

        /** The X position */
        private int x;

        /** The Y position */
        private int y;

        /** The default grid size */
        public static final int CELLSIZE = 15;

        /**
         * Constructor of this class
         * 
         * @param x The x-axis position
         * @param y The y-axis position
         */
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Gets the x-axis position
         * 
         * @return The x-axis position
         */
        public int getX() {
            return x;
        }

        /**
         * Sets the x-axis position
         * 
         * @param x The x-axis position
         */
        public void setX(int x) {
            this.x = x;
        }

        /**
         * Gets the y-axis position
         * 
         * @return The y-axis position
         */
        public int getY() {
            return y;
        }

        /**
         * Sets the y-axis position
         * 
         * @param y The y-axis position
         */
        public void setY(int y) {
            this.y = y;
        }
        
        /**
         * 
         * {@inheritDoc}
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            int hashMultiplicator = 59;
            int hashCode = 17;
            
            hashCode = hashCode * hashMultiplicator + getX();
            hashCode = hashCode * hashMultiplicator + getY();
            
            return hashCode;
        }
        
        /**
         * 
         * {@inheritDoc}
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object o) {
            if (o instanceof Cell) {
                if (((Cell) o).getX() == this.getX() &&
                        ((Cell) o).getY() == this.getY()) {
                    return true;
                }
                else {
                    return false;
                }
            }
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "Cell Position x: " + x + " y: " + y +" hashCode "+hashCode();
        }

        /**
         * 
         * {@inheritDoc}
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(Cell cell) {
            if (y > cell.getY()){
                return 1;
            }
            else if (y < cell.getY()) {
                return -1;
            }
            else {
                if (x > cell.getX()){
                    return 1;
                }
                else if (x < cell.getX()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        }
    }

    /**
     * This class represents a UI component on the dashboard. Each component exist of an start and end cell. The start
     * cell conforms to the upper left position on an coordinate system. The end cell is located in the lowest right
     * area. This component is also connected to the module via the unique module id.
     * 
     * @author dv20jac
     */
    protected class CellComponent {

        /** The corresponding dashboard interface */
        private DashboardComponent dashboardComponent;

        /** The special dashboard UI component */
        private JComponent component;

        /** The start cell of the component */
        private Cell start;

        /** The end cell of the component */
        private Cell end;

        /** The height of the component in cell unit */
        private int height;

        /** The width of the component in cell unit */
        private int width;

        /**
         * Constructor of this class
         * 
         * @param component The UI component
         * @param dashboard The dashboard
         * @param moduleId The unique module id
         * @param start The start cell of the component
         * @param end The end cell of the component
         */
        public CellComponent(JComponent component, DashboardComponent dashboardComponent, Cell start, Cell end) {
            this.component = component;
            this.dashboardComponent = dashboardComponent;
            this.start = start;
            this.end = end;
        }


        /**
         * Sets the start position of the component
         * 
         * @param start The start position on the grid
         */
        public void setStartCell(Cell start) {
            this.start = start;
        }

        /**
         * Gets the start position of the component
         * 
         * @return The start position on the grid
         */
        public Cell getStartCell() {
            return start;
        }

        /**
         * Sets the end cell of the component
         * 
         * @param end The end cell
         */
        public void setEndCell(Cell end) {
            this.end = end;
        }

        /**
         * Returns the end cell of the component
         * 
         * @return The end cell
         */
        public Cell getEndCell() {
            return end;
        }

        /**
         * Returns the <code>JComponent</code> that should be painted on the dashboard.
         * 
         * @return The UI component
         */
        public JComponent getJComponent() {
            return component;
        }

        /**
         * Returns the height of an component in cell unit
         * 
         * @return Height in cell unit
         */
        public int getHeight() {
            return height;
        }

        /**
         * Sets the height for an component in cell unit
         * 
         * @param height Height in cell unit
         */
        public void setHeight(int height) {
            this.height = height;
        }

        /**
         * Returns the width of an component in cell unit
         * 
         * @return Width in cell units
         */
        public int getWidth() {
            return width;
        }

        /**
         * Sets the width for an component in cell unit
         * 
         * @param width The width in cell unit
         */
        public void setWidth(int width) {
            this.width = width;
        }

        /**
         * Return the <code>DashboardComponent</code> for the component
         * 
         * @return The DashboarComponent
         */
        public DashboardComponent getDashboardComponent() {
            return dashboardComponent;
        }

        /**
         * Paint the component
         */
        public void paintComponent() {
            int startX = (int) getXPosition(selectedCellComponent.getStartCell());
            int startY = (int) getYPosition(selectedCellComponent.getStartCell());

            component.setLocation(startX, startY);
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "start cell: " + start + " end cell: " + end + " height: " + getHeight() + " width: " + getWidth()
                + " ifController " + controller;
        }
    }

    /**
     * This class implements the methods for drop support on the dashboard
     * 
     * @author dv20jac
     */
    private class DashbordDropTargetListener extends DropTargetAdapter {

        /**
         * {@inheritDoc}
         * 
         * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dragEnter(DropTargetDragEvent ev) {
            try {
                Transferable transferable = ev.getTransferable();
                String val = (String) transferable.getTransferData(transferable.getTransferDataFlavors()[0]);
                for (DashboardComponent dashboardComponent : controller.getDashboardComponentList()) {
                    if (val.equals(dashboardComponent.getUniqueId())) {
                        draggedView = dashboardComponent.getDashboardComponent();
                        draggedView.setSize(dashboardComponent.getDashboardSize());
                        view.add(draggedView);

                        currentMode = Mode.MODULEDRAG;
                    }
                }

            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, "An error during dragEnter has been occurred", ex);
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
         */
        @Override
        public void dragExit(DropTargetEvent ev) {
            if (draggedView != null) {
                view.remove(draggedView);
                view.repaint();

                currentMode = Mode.NON;
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dragOver(DropTargetDragEvent ev) {
            if (draggedView != null) {
                draggedView.setLocation(ev.getLocation());
                view.validate();
                view.repaint();
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
         */
        @Override
        public void drop(DropTargetDropEvent ev) {
            try {
                Transferable transferable = ev.getTransferable();
                String val = (String) transferable.getTransferData(transferable.getTransferDataFlavors()[0]);

                dragCellX = getCellPositionX((int) ev.getLocation().getX());
                dragCellY = getCellPositionY((int) ev.getLocation().getY());
                // remove the preview component from the jpanel
                view.remove(draggedView);

                int offSet = 10;
                Rectangle rectangle = draggedView.getBounds();
                rectangle.setFrame((int) (rectangle.getX() - (1 * getCellSize())),
                    (int) (rectangle.getY() - (1 * getCellSize())), rectangle.getWidth() + (2 * getCellSize())
                        - offSet, rectangle.getHeight() + (2 * getCellSize()) - offSet);

                // calculate start and end cell
                Cell startCell = new Cell(dragCellX, dragCellY);
                Cell endCell =
                        new Cell(startCell.getX() + getExpectedCellX(draggedView.getSize().getWidth()),
                            startCell.getY() + getExpectedCellY(draggedView.getSize().getHeight()));

                if (!VDashboard.this.controller.checkCollision(val, rectangle)
                    && (startCell.getX() > 1 && startCell.getX() <= numberOfCellX)
                    && (startCell.getY() > 1 && startCell.getY() <= numberOfCellY)
                    && (endCell.getX() > 1 && endCell.getX() < numberOfCellX)
                    && (endCell.getY() > 1 && endCell.getY() < numberOfCellY)) {

                    loadModule(val);

                    ev.dropComplete(true);
                    currentMode = Mode.NON;

                    return;
                }

            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, "An error during drop has been occurred", ex);
            }
            ev.rejectDrop();
            view.invalidate();
            view.repaint();

            currentMode = Mode.NON;
        }
    }
}
