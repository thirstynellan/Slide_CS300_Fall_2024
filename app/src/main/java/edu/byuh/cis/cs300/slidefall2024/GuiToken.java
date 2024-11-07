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
public class GuiToken implements TickListener {
    private Player player;
    private RectF bounds;
    private PointF velocity;
    private Bitmap image;
    private GridPosition gp;
    private static int movers = 0;
    private int stepCounter;
    private final int STEPS = 11;
    private boolean falling;

    public class GridPosition {
        public char row;
        public char col;
    }

    /**
     * Create a new GuiToken object
     * @param p The Player (X or O) who created the token
     * @param parent which button was tapped to create the token
     * @param res the Resources object (used for loading image)
     */
    public GuiToken(Player p, GuiButton parent, Resources res) {
        gp = new GridPosition();
        if (parent.isTopButton()) {
            gp.row = 'A' - 1;
            gp.col = parent.getLabel();
        } else {
            gp.row = parent.getLabel();
            gp.col = '1' - 1;
        }

        this.bounds = new RectF(parent.getBounds());
        velocity = new PointF();
        falling = false;
        player = p;
        if (player == Player.X) {
            image = BitmapFactory.decodeResource(res, R.drawable.player_x);
        } else {
            image = BitmapFactory.decodeResource(res, R.drawable.player_o);
        }
        image = Bitmap.createScaledBitmap(image, (int)bounds.width(), (int)bounds.height(), true);
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
        if (falling) {
            velocity.y *= 2;
        } else {
            if (velocity.x != 0 || velocity.y != 0) {
                if (stepCounter >= STEPS) {
                    velocity.set(0, 0);
                    movers--;
                    if (fellOff()) {
                        velocity.set(0, 1);
                        falling = true;
                    }
                } else {
                    stepCounter++;
                }
            }
        }
        bounds.offset(velocity.x, velocity.y);
    }

    private boolean fellOff() {
        return (gp.col > '5' || gp.row > 'E');
    }

    public boolean isInvisible(int h) {
        return (bounds.top > h);
    }


    /**
     * Helper method for tokens created by the top row of buttons
     */
    public void startMovingDown() {
        startMoving(0, bounds.width()/STEPS);
        gp.row++;
    }

    /**
     * Helper method for tokens created by the left column of buttons
     */
    public void startMovingRight() {
        startMoving(bounds.width()/STEPS, 0);
        gp.col++;
    }

    private void startMoving(float vx, float vy) {
        velocity.set(vx, vy);
        movers++;
        stepCounter = 0;
    }

    /**
     * Is animation currently happening?
     * @return true if the token is currently moving (i.e. has a non-zero velocity); false otherwise.
     */
    public boolean isMoving() {
        return (velocity.x > 0 || velocity.y > 0);
    }

    public static boolean anyMovers() {
        return movers > 0;
    }

    @Override
    public void onTick() {
        move();
    }

    public boolean matches(char row, char col) {
        return (gp.row == row && gp.col == col);
    }
}
