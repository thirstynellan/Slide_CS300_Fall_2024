package edu.byuh.cis.cs300.slidefall2024;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Represents a single X or O on the grid.
 * It is the graphical analog to the Player enum.
 */
public class GuiToken {
    private Player player;
    private RectF bounds;
    private PointF velocity;
    private PointF destination;
    private float tolerance;
    private Bitmap image;

    /**
     * Create a new GuiToken object
     * @param p The Player (X or O) who created the token
     * @param parent which button was tapped to create the token
     * @param res the Resources object (used for loading image)
     */
    public GuiToken(Player p, GuiButton parent, Resources res) {
        this.bounds = new RectF(parent.getBounds());
        velocity = new PointF();
        destination = new PointF();
        tolerance = bounds.height()/10f;
        player = p;
        if (player == Player.X) {
            image = BitmapFactory.decodeResource(res, R.drawable.player_x);
        } else {
            image = BitmapFactory.decodeResource(res, R.drawable.player_o);
        }
        image = Bitmap.createScaledBitmap(image, (int)bounds.width(), (int)bounds.height(), true);
        if (parent.isTopButton()) {
            moveDown();
        } else {
            moveRight();
        }
    }

    /**
     * Draw the token at the correct location, using the correct
     * image (X or O)
     * @param c The Canvas object supplied by onDraw
     */
    public void draw(Canvas c) {
        c.drawBitmap(image, bounds.left, bounds.top, null);
    }

    /**
     * Move the token by its current velocity.
     * Stop when it reaches its destination location.
     */
    public void move() {
        if (velocity.x != 0 || velocity.y != 0) {
            float dx = destination.x - bounds.left;
            float dy = destination.y - bounds.top;
            if (PointF.length(dx, dy) < tolerance) {
                bounds.offsetTo(destination.x, destination.y);
                velocity.set(0,0);
            } else {
                bounds.offset(velocity.x, velocity.y);
            }
        }
    }

    /**
     * Helper method for tokens created by the top row of buttons
     */
    public void moveDown() {
        setGoal(bounds.left, bounds.top+bounds.height());
    }

    /**
     * Helper method for tokens created by the left column of buttons
     */
    public void moveRight() {
        setGoal(bounds.left+bounds.width(), bounds.top);
    }

    /**
     * Is animation currently happening?
     * @return true if the token is currently moving (i.e. has a non-zero velocity); false otherwise.
     */
    public boolean isMoving() {
        return (velocity.x > 0 || velocity.y > 0);
    }

    /**
     * Assign a destination location to the token
     * @param x the X coordinate where the token should stop
     * @param y the X coordinate where the token should stop
     */
    private void setGoal(float x, float y) {
        destination.set(x,y);
        float dx = destination.x - bounds.left;
        float dy = destination.y - bounds.top;
        velocity.x = dx/11f;
        velocity.y = dy/11f;
    }
}
