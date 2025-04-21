package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class AngryBird extends BasicParticle {
    private static BufferedImage birdImg;

    static {
        try {
            birdImg = ImageIO.read(new File("resources/imgs/birds/red_bird.png"));
        } catch (IOException e) {
            System.err.println("无法加载红色小鸟图片：" + e.getMessage());
        }
    }

    public AngryBird(float sx, float sy, float vx, float vy, float radius) {
        super(sx, sy, vx * 1.5f, vy * 1.5f, radius, Color.RED, 5.0f, 0.05f);
    }

    @Override
    public void draw(Graphics2D g) {
        int x = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(body.getPosition().x);
        int y = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(body.getPosition().y);
        int r = SCREEN_RADIUS;

        if (birdImg != null) {
            g.drawImage(birdImg,
                    x - r, y - r, // 左上角
                    x + r, y + r, // 右下角
                    0, 0,
                    birdImg.getWidth(), birdImg.getHeight(),
                    null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(x - r, y - r, 2 * r, 2 * r);
        }
    }
}
