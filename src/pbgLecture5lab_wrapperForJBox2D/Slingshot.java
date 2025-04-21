package pbgLecture5lab_wrapperForJBox2D;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jbox2d.common.Vec2;

public class Slingshot {
    private static BufferedImage slingshotImg;
    private float posX, posY; // Position in world coordinates
    private float width, height; // Size in world coordinates
    private AngryBird loadedBird; // Reference to the bird that's loaded in the slingshot
    private Vec2 pullbackPosition; // Position where the bird is being pulled back to
    private boolean isStretching = false; // Whether the slingshot is currently being stretched

    // Maximum distance the slingshot can be stretched
    private final float MAX_STRETCH_DISTANCE = 2.0f;

    static {
        try {
            slingshotImg = ImageIO.read(new File("resources/imgs/background/slingshot.png"));
        } catch (IOException e) {
            System.err.println("Unable to load slingshot image: " + e.getMessage());
        }
    }

    public Slingshot(float x, float y, float width, float height) {
        this.posX = x;
        this.posY = y;
        this.width = width;
        this.height = height;
        this.pullbackPosition = new Vec2(x, y);
    }

    public void loadBird(AngryBird bird) {
        this.loadedBird = bird;
        // Position the bird at the slingshot pocket
        bird.getBody().setTransform(new Vec2(posX, posY), 0);
        bird.getBody().setLinearVelocity(new Vec2(0, 0));
        // Make the bird static until launched
        bird.getBody().setType(org.jbox2d.dynamics.BodyType.STATIC);
    }

    public boolean isBirdLoaded() {
        return loadedBird != null;
    }

    public void startStretch(Vec2 mousePos) {
        if (loadedBird != null && !isStretching) {
            isStretching = true;
            updateStretch(mousePos);
        }
    }

    public void updateStretch(Vec2 mousePos) {
        if (isStretching && loadedBird != null) {
            // Calculate direction vector from slingshot to mouse
            Vec2 direction = mousePos.sub(new Vec2(posX, posY));

            // Limit stretch distance
            float distance = direction.length();
            if (distance > MAX_STRETCH_DISTANCE) {
                direction.normalize();
                direction.mulLocal(MAX_STRETCH_DISTANCE);
            }

            // Update pullback position (directly using the direction without negating)
            pullbackPosition = new Vec2(posX + direction.x, posY + direction.y);

            // Move the bird to the pullback position
            loadedBird.getBody().setTransform(pullbackPosition, 0);
        }
    }

    public void release() {
        if (isStretching && loadedBird != null) {
            // Calculate launch vector (from slingshot center to pullback position)
            // This is the opposite of the stretch direction to create the slingshot effect
            Vec2 launchVector = new Vec2(posX - pullbackPosition.x, posY - pullbackPosition.y);

            // Apply a force proportional to the stretch distance
            // Increased force multiplier from 15 to 25 for stronger propulsion relative to
            // higher gravity
            float forceMagnitude = launchVector.length() * 25;
            launchVector.normalize();
            launchVector.mulLocal(forceMagnitude);

            // Make the bird dynamic again
            loadedBird.getBody().setType(org.jbox2d.dynamics.BodyType.DYNAMIC);

            // Apply the impulse to launch the bird
            loadedBird.getBody().setLinearVelocity(launchVector);

            // Reset slingshot state
            isStretching = false;
            loadedBird = null;
        }
    }

    public void draw(Graphics2D g) {
        // Convert world coordinates to screen coordinates
        int screenX = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(posX);
        int screenY = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(posY);

        int screenWidth = (int) BasicPhysicsEngineUsingBox2D.convertWorldLengthToScreenLength(width);
        int screenHeight = (int) BasicPhysicsEngineUsingBox2D.convertWorldLengthToScreenLength(height);

        // Draw the slingshot image
        if (slingshotImg != null) {
            g.drawImage(slingshotImg,
                    screenX - screenWidth / 2, screenY - screenHeight / 2,
                    screenX + screenWidth / 2, screenY + screenHeight / 2,
                    0, 0, slingshotImg.getWidth(), slingshotImg.getHeight(),
                    null);
        } else {
            // Fallback if image fails to load
            g.setColor(new Color(139, 69, 19)); // Brown color
            g.fillRect(screenX - screenWidth / 4, screenY - screenHeight / 2, screenWidth / 2, screenHeight);
            g.fillRect(screenX - screenWidth / 2, screenY - screenHeight / 4, screenWidth, screenHeight / 2);
        }

        // Draw the slingshot band (elastic) when stretching
        if (isStretching && loadedBird != null) {
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(3));

            // Convert pullback position to screen coordinates
            int pullbackScreenX = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(pullbackPosition.x);
            int pullbackScreenY = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(pullbackPosition.y);

            // Draw two bands (one from each fork of the slingshot)
            g.drawLine(screenX - screenWidth / 4, screenY - screenHeight / 4, pullbackScreenX, pullbackScreenY);
            g.drawLine(screenX + screenWidth / 4, screenY - screenHeight / 4, pullbackScreenX, pullbackScreenY);
        }
    }

    public boolean containsPoint(Vec2 point) {
        return (Math.abs(point.x - posX) < width / 2 &&
                Math.abs(point.y - posY) < height / 2);
    }

    public boolean isStretching() {
        return isStretching;
    }
}
