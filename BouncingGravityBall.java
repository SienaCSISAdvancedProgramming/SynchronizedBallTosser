
// going to be lazy about imports in these examples...
import java.awt.*;
import javax.swing.*;

/**
 * The BouncingGravityBall class is responsible for managing the life
 * of one ball that starts out with a given x and y speed, moves
 * subject to acceleration due to gravity, until it falls off the
 * bottom of the window.
 */
public class BouncingGravityBall extends Thread {

	// ball size
	public static final int SIZE = 50;

	// delay time between frames of animation (ms)
	public static final int DELAY_TIME = 33;

	// what is slow enough to consider to be stopped?
	public static final double ALMOST_STOPPED = 0.4;

	// what to add to ySpeed to simulate gravity?
	public static final double GRAVITY = 0.3;

	// how much momentum to lose on a bounce
	public static final double DAMPING = 0.9;

	// pixels to move each iteration
	private double xSpeed, ySpeed;

	// latest location of the ball
	private double upperLeftX, upperLeftY;

	// max allowed coordinates of the upper left corner
	private int xMax, yMax;

	// have we stopped bouncing?
	private boolean done;

	// who do we live in so we can repaint?
	private JComponent container;

	/**
	 * Construct a new BouncingGravityBall object.
	 * 
	 * @param startCenter the initial point at which the center of the
	 *                    ball should be drawn
	 * @param xSpeed      initial x speed, pixels per second
	 * @param ySpeed      initial y speed, pixels per second
	 * @param container   the Swing component in which this ball is being
	 *                    drawn to allow it to call that component's repaint method
	 */
	public BouncingGravityBall(Point startCenter,
			double xSpeed, double ySpeed,
			JComponent container) {

		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		upperLeftX = startCenter.x - SIZE / 2;
		upperLeftY = startCenter.y - SIZE / 2;
		this.yMax = container.getHeight() - SIZE;
		this.xMax = container.getWidth() - SIZE;
		this.container = container;
	}

	/**
	 * Draw the ball at its current location.
	 * 
	 * @param g the Graphics object on which the ball should be drawn
	 */
	public void paint(Graphics g) {

		g.fillOval((int) upperLeftX, (int) upperLeftY, SIZE, SIZE);
	}

	/**
	 * This object's run method, which manages the life of the ball as it
	 * bounces around the screen.
	 */
	@Override
	public void run() {

		while (!done) {

			try {
				sleep(DELAY_TIME);
			} catch (InterruptedException e) {
			}

			// every iteration, update the coordinates
			// by a pixel
			upperLeftX += xSpeed;
			upperLeftY += ySpeed;

			boolean bounced = false;
			// bounce off the walls
			if (upperLeftX < 0) {
				upperLeftX = 0;
				bounced = true;
				xSpeed = -xSpeed;
			}

			if (upperLeftX > xMax) {
				upperLeftX = xMax;
				bounced = true;
				xSpeed = -xSpeed;
			}

			if (upperLeftY < 0) {
				upperLeftY = 0;
				bounced = true;
				ySpeed = -ySpeed;
			}

			if (upperLeftY > yMax) {
				upperLeftY = yMax;
				bounced = true;
				ySpeed = -ySpeed;
			}

			// if we bounced, we're going to dampen speed in both dimensions
			if (bounced) {
				xSpeed *= DAMPING;
				ySpeed *= DAMPING;
			}

			// if we've almost stopped moving, let's say we're done
			done = (upperLeftY == yMax &&
					Math.abs(ySpeed) < ALMOST_STOPPED &&
					Math.abs(xSpeed) < ALMOST_STOPPED);

			// gravity factor also
			ySpeed += GRAVITY;

			container.repaint();
		}
	}

	/**
	 * Return whether the ball has completed its fall to the bottom.
	 * 
	 * @return whether the ball has completed its fall to the bottom
	 */
	public boolean done() {

		return done;
	}
}
