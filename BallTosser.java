// going to be lazy about imports in these examples...
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
   A program to demonstrate a simple animation of a ball that is launched in
   a given direction, and then is subject to a gravitational pull as it
   bounces off the sides of the window.

   This version introduces a synchronization lock to ensure thread 
   safety on modifications of the list of animated objects.

   @author Jim Teresco
   @version Spring 2020
*/

public class BallTosser extends MouseAdapter implements Runnable {

    // multiplier to convert the press/release distances to initial
    // speeds in the x and y directions
    public static final double SLING_FACTOR = 0.25;
    
    // list of FallingGravityBall objects currently on the screen
    private java.util.List<BouncingGravityBall> list;
    
    private JPanel panel;

    // press/drag points for launching, and if we are dragging
    private boolean dragging;
    private Point pressPoint;
    private Point dragPoint;

    // an object to serve as the lock for thread safety of our list access
    private Object lock = new Object();
    
    /**
       The run method to set up the graphical user interface
    */
    @Override
    public void run() {
	
	// set up the GUI "look and feel" which should match
	// the OS on which we are running
	JFrame.setDefaultLookAndFeelDecorated(true);
	
	// create a JFrame in which we will build our very
	// tiny GUI, and give the window a name
	JFrame frame = new JFrame("BallTosser");
	frame.setPreferredSize(new Dimension(500,500));
	
	// tell the JFrame that when someone closes the
	// window, the application should terminate
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	// JPanel with a paintComponent method
	panel = new JPanel() {
		@Override
		public void paintComponent(Graphics g) {
		    
		    // first, we should call the paintComponent method we are
		    // overriding in JPanel
		    super.paintComponent(g);

		    // if we are currently dragging, draw a sling line
		    if (dragging) {
			g.drawLine(pressPoint.x, pressPoint.y,
				   dragPoint.x, dragPoint.y);
		    }
				   
		    // redraw each ball at its current position,
		    // remove the ones that are done along the way
		    int i = 0;

		    // since we will be modifying the list, we will
		    // lock access in case a mouseReleased is going
		    // to happen at the same time
		    synchronized (lock) {
			while (i < list.size()) {
			    BouncingGravityBall b = list.get(i);
			    if (b.done()) {
				list.remove(i);
			    }
			    else {
				b.paint(g);
				i++;
			    }
			}
		    }
		}
	    };
	frame.add(panel);
	panel.addMouseListener(this);
	panel.addMouseMotionListener(this);

	// construct the list
	list = new ArrayList<BouncingGravityBall>();
	
	// display the window we've created
	frame.pack();
	frame.setVisible(true);
    }

    /**
       Mouse press event handler to set up to create a new
       BouncingGravityBall on subsequent release.

       @param e mouse event info
    */
    @Override
    public void mousePressed(MouseEvent e) {

	pressPoint = e.getPoint();
	panel.repaint();
    }
    /**
       Mouse drag event handler to create remember the current point
       for sling line drawing.

       @param e mouse event info
    */
    @Override
    public void mouseDragged(MouseEvent e) {

	dragPoint = e.getPoint();
	dragging = true;
	panel.repaint();
    }

    /**
       Mouse release event handler to create a new BouncingGravityBall
       centered at the release point, initial velocities depending on 
       distance from press point.

       @param e mouse event info
    */
    @Override
    public void mouseReleased(MouseEvent e) {

	BouncingGravityBall newBall =
	    new BouncingGravityBall(e.getPoint(),
				    SLING_FACTOR*(pressPoint.x - e.getPoint().x),
				    SLING_FACTOR*(pressPoint.y - e.getPoint().y),
				    panel);

	// lock access to the list in case paintComponent is using it
	// concurrently
	synchronized (lock) {
	    list.add(newBall);
	}

	newBall.start();
	dragging = false;
	panel.repaint();
    }

    public static void main(String args[]) {

	// The main method is responsible for creating a thread (more
	// about those later) that will construct and show the graphical
	// user interface.
	javax.swing.SwingUtilities.invokeLater(new BallTosser());
    }
}
   
