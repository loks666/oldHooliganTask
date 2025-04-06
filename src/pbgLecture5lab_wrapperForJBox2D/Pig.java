package pbgLecture5lab_wrapperForJBox2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jbox2d.common.Vec2;

public class Pig extends BasicPolygon {
    private static BufferedImage blockImg;  // 木头方块图片
    private boolean isDestroyed = false;   // 标记是否被摧毁
    private boolean isActivated = false;   // 标记是否被激活（开始物理模拟）

    static {
        try {
            String currentDir = System.getProperty("user.dir");
            File imageFile = new File(currentDir, "resources/imgs/blocks/wood_block.png");
            System.out.println("尝试加载图片：" + imageFile.getAbsolutePath());
            blockImg = ImageIO.read(imageFile);
            
            if (blockImg == null) {
                System.err.println("图片加载失败：blockImg 为 null");
            } else {
                System.out.println("成功加载木头方块图片");
            }
        } catch (IOException e) {
            System.err.println("无法加载图片：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public Pig(float sx, float sy, float vx, float vy, float radius) {
        // 使用与黄色方块相同的物理属性
        super(sx, sy, vx, vy, radius, Color.YELLOW, 1.0f, 0.1f, 4);
        // 设置初始状态为静止
        body.setType(org.jbox2d.dynamics.BodyType.STATIC);
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }

    public void activate() {
        if (!isActivated) {
            isActivated = true;
            // 改变物体类型为动态，开始物理模拟
            body.setType(org.jbox2d.dynamics.BodyType.DYNAMIC);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (isDestroyed) {
            return;  // 如果已经被摧毁，就不绘制
        }

        Vec2 position = body.getPosition();
        float angle = body.getAngle();
        
        int centerX = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(position.x);
        int centerY = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(position.y);
        
        int size = (int)(this.radius * 2 * this.ratioOfScreenScaleToWorldScale);
        int half = size / 2;
        
        AffineTransform old = g.getTransform();
        g.translate(centerX, centerY);
        g.rotate(angle);      // 先旋转
        g.scale(1.0, -1.0);  // 再翻转Y轴
        g.rotate(Math.PI);   // 最后旋转180度
        
        // 绘制木头方块
        if (blockImg != null) {
            g.drawImage(blockImg, -half, -half, size, size, null);
        } else {
            System.out.println("图片为空，使用默认绘制");
            super.draw(g);  // 如果图片加载失败，使用默认的多边形绘制
        }
        
        g.setTransform(old);
    }
} 