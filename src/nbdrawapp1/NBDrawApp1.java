import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.io.*;


public class NBDrawApp1 extends JFrame
{
    // GUI Component dimensions.
    private final int CANVAS_INITIAL_WIDTH = 800;
    private final int CANVAS_INITIAL_HEIGHT = 640;
    private final int CONTROL_PANEL_WIDTH = 200;
    private final int MESSAGE_AREA_HEIGHT = 100;


    // Drawing area class (inner class).
    class Canvas extends JPanel
    {
        // Called every time there is a change in the canvas contents.
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);        
            draw(g);
        }
    } // end inner class Canvas
    
    
    private Canvas canvas;
    
    private JPanel controlPanel;
    private JLabel coordinatesLabel;
    private JRadioButton lineRadioButton, ovalRadioButton, rectangleRadioButton, freehandRadioButton;
    private JSlider freehandSizeSlider;
    private JCheckBox fineCheckBox, coarseCheckBox;
    private JButton colourButton, clearButton, animateButton;
    
    private JTextArea messageArea;
    
    private JMenuBar menuBar;
        
    
    /*****************************************************************
     * 
     * Constructor method starts here
     *    ... and goes on for quite a few lines of code 
     */
    public NBDrawApp1()
    {
        setTitle("Drawing Application (da1)");
        setLayout(new BorderLayout());  // Layout manager for the frame.
        
        // Canvas
        canvas = new Canvas();
          canvas.setBorder(new TitledBorder(new EtchedBorder(), "Canvas"));
          canvas.setPreferredSize(new Dimension(CANVAS_INITIAL_WIDTH, CANVAS_INITIAL_HEIGHT));
          // next line changes the cursor's rendering whenever the mouse drifts onto the canvas
          canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        add(canvas, BorderLayout.CENTER);
        class CanvasMouseMotionListener implements MouseMotionListener
        {
            public void mouseMoved(MouseEvent event)
            {
                coordinatesLabel.setText(event.getX() + "x " + event.getY() + "y");
            }
            public void mouseDragged(MouseEvent event)
            {
                ;
            }
        }
        canvas.addMouseMotionListener(new CanvasMouseMotionListener());
        
        // Menu bar
        menuBar = new JMenuBar();
          JMenu fileMenu = new JMenu("File");
            JMenuItem fileSaveMenuItem = new JMenuItem("Save");
            fileMenu.add(fileSaveMenuItem);
            JMenuItem fileLoadMenuItem = new JMenuItem("Load");
            fileMenu.add(fileLoadMenuItem);
            fileMenu.addSeparator();
            JMenuItem fileExitMenuItem = new JMenuItem("Exit");
            fileMenu.add(fileExitMenuItem);
          menuBar.add(fileMenu);
          JMenu helpMenu = new JMenu("Help");
            JMenuItem helpAboutMenuItem = new JMenuItem("About");
            helpMenu.add(helpAboutMenuItem);
          menuBar.add(helpMenu);
        add(menuBar, BorderLayout.PAGE_START);
        
        // Control Panel
        controlPanel = new JPanel();
          controlPanel.setBorder(new TitledBorder(new EtchedBorder(), "Control Panel"));
          controlPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH, CANVAS_INITIAL_HEIGHT));
          // the following two lines put the control panel in a scroll pane (nicer?).      
          JScrollPane controlPanelScrollPane = new JScrollPane(controlPanel);
          controlPanelScrollPane.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH + 30, CANVAS_INITIAL_HEIGHT));
        add(controlPanelScrollPane, BorderLayout.LINE_START);
        
        // Mouse Coordinates panel
        JPanel coordinatesPanel = new JPanel();
          coordinatesPanel.setBorder(new TitledBorder(new EtchedBorder(), "Drawing Position"));
          coordinatesPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 60));
          coordinatesLabel = new JLabel();
          coordinatesPanel.add(coordinatesLabel);
        controlPanel.add(coordinatesPanel);
        
        // Drawing tools panel
        JPanel drawingToolsPanel = new JPanel();
          drawingToolsPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 140));
          drawingToolsPanel.setLayout(new GridLayout(0, 1));
          drawingToolsPanel.setBorder(new TitledBorder(new EtchedBorder(), "Drawing Tools"));


        controlPanel.add(drawingToolsPanel);
        
        // Freehand trace size slider
        JPanel freehandSliderPanel = new JPanel();
          freehandSliderPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 90));
          drawingToolsPanel.setLayout(new GridLayout(0, 1));
          freehandSliderPanel.setBorder(new TitledBorder(new EtchedBorder(), "Freehand Size"));
        controlPanel.add(freehandSliderPanel);

        // Grid Panel
        JPanel gridPanel = new JPanel();
          gridPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 80));
          gridPanel.setLayout(new GridLayout(0, 1));
          gridPanel.setBorder(new TitledBorder(new EtchedBorder(), "Grid"));

          fineCheckBox = new JCheckBox("Fine Checkbox");
          coarseCheckBox = new JCheckBox("Coarse Checkbox");

          gridPanel.add(fineCheckBox);
          gridPanel.add(coarseCheckBox);

          fineCheckBox.addChangeListener(changeListener);
          coarseCheckBox.addChangeListener(changeListener);
        controlPanel.add(gridPanel);
        // Colour Panel
        JPanel colourPanel = new JPanel();
          colourPanel.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 90));
          colourPanel.setBorder(new TitledBorder(new EtchedBorder(), "Colour"));
          colourButton = new JButton();
          colourButton.setPreferredSize(new Dimension(50, 50));
          colourPanel.add(colourButton);
        controlPanel.add(colourPanel);

        // Clear button
        clearButton = new JButton("Clear Canvas");
          clearButton.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 50));
        controlPanel.add(clearButton);

        // Animate button 
        animateButton = new JButton("Animate");
          animateButton.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 50));
        controlPanel.add(animateButton);
        
        // that completes the control panel section

        
        // Message area
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBackground(canvas.getBackground());
        JScrollPane textAreaScrollPane = new JScrollPane(messageArea);
        textAreaScrollPane.setBorder(new TitledBorder(new EtchedBorder(), "Message Area"));
        textAreaScrollPane.setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH + CANVAS_INITIAL_WIDTH, MESSAGE_AREA_HEIGHT));
        add(textAreaScrollPane, BorderLayout.PAGE_END);

        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        pack();
        setVisible(true);
        
    }  // end of the NBDrawApp1 constructor method
    
    // Called by the canvas' paintComponent method
    void draw(Graphics g)
    {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if(fineCheckBox.isSelected()) {
            g.setColor(new Color(0.8F, 0.8F, 0.8F, 0.8F));
            for (int i = 0; i < width; i += 10) {
                g.drawLine(i, 0, i, height);
            }
            for (int h = height; h > 10; h -= 10) {
                g.drawLine(0, h, width, h);
            }

        }

        if(coarseCheckBox.isSelected()){
            g.setColor(new Color(0.6F, 0.6F, 0.6F, 0.6F));
            for (int i = 0; i < width; i += 50) {
                g.drawLine(i, 0, i, height);
            }
            for (int h = height; h > 10; h -= 50) {
                g.drawLine(0, h, width, h);
            }
        }
    } // end draw method   
    ChangeListener changeListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            repaint();
        };
    };

    
    public static void main(String args[])
    {
        NBDrawApp1 NBDrawApp1Instance = new NBDrawApp1();
    } // end main method
    
} // end of NBDrawApp1 class