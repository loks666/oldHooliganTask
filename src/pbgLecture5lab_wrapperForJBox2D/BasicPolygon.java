package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class BasicPolygon {
	private static BufferedImage blockImg; // 用于缓存方块的图片

	static {
		try {
			// 使用相对路径加载图片
			blockImg = ImageIO.read(new File("resources/imgs/blocks/wood_block.png"));
		} catch (IOException ignored) {
			System.err.println("无法加载方块图片：" + ignored.getMessage());
		}
	}

	public final float ratioOfScreenScaleToWorldScale;
	private final float linearDragForce, mass;
	public final Color col;
	protected final Body body;
	private final Path2D.Float polygonPath;
	private final float radius;

	public BasicPolygon(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float linearDragForce, int numSides) {
		this(sx, sy, vx, vy, radius, col, mass, linearDragForce, mkRegularPolygon(numSides, radius), numSides);
	}

	public BasicPolygon(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float linearDragForce, Path2D.Float polygonPath, int numSides) {
		World w = BasicPhysicsEngineUsingBox2D.world;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set(sx, sy);
		bodyDef.linearVelocity.set(vx, vy);
		bodyDef.angularDamping = 0.1f;
		this.body = w.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		Vec2[] vertices = verticesOfPath2D(polygonPath, numSides);
		shape.set(vertices, numSides);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = (float) (mass / ((float) numSides) / 2f * (radius * radius) * Math.sin(2 * Math.PI / numSides));
		fixtureDef.friction = 0.1f;
		fixtureDef.restitution = 0.5f;
		body.createFixture(fixtureDef);

		this.linearDragForce = linearDragForce;
		this.mass = mass;
		this.ratioOfScreenScaleToWorldScale = BasicPhysicsEngineUsingBox2D.convertWorldLengthToScreenLength(1);
		this.col = col;
		this.polygonPath = polygonPath;
		this.radius = radius;
	}

	// 添加这个方法来避免报错
	public void notificationOfNewTimestep() {
		// 你可以在这里添加任何与物体的物理状态更新相关的逻辑
		if (linearDragForce > 0) {
			Vec2 dragForce = new Vec2(body.getLinearVelocity());
			dragForce = dragForce.mul(-linearDragForce * mass);
			body.applyForceToCenter(dragForce);
		}
	}

	public void draw(Graphics2D g) {
		Vec2 position = body.getPosition();
		float angle = body.getAngle();

		int centerX = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(position.x);
		int centerY = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(position.y);

		AffineTransform old = g.getTransform();
		g.translate(centerX, centerY);
		g.scale(1.0, -1.0);  // 翻转坐标系
		g.rotate(angle);

		float worldSize = radius * 2;
		float screenSize = BasicPhysicsEngineUsingBox2D.convertWorldLengthToScreenLength(worldSize);
		int half = (int)(screenSize / 2f);  // 图像一半的大小

		if (blockImg != null) {
			g.drawImage(blockImg, -half, -half, half, half, 0, 0, blockImg.getWidth(), blockImg.getHeight(), null);
		} else {
			g.setColor(col);
			g.fillRect(-half, -half, (int) screenSize, (int) screenSize);
		}

		g.setTransform(old);
	}

	public static Vec2[] verticesOfPath2D(Path2D.Float p, int n) {
		Vec2[] result = new Vec2[n];
		float[] values = new float[6];
		PathIterator pi = p.getPathIterator(null);
		int i = 0;
		while (!pi.isDone() && i < n) {
			int type = pi.currentSegment(values);
			if (type == PathIterator.SEG_LINETO) {
				result[i++] = new Vec2(values[0], values[1]);
			}
			pi.next();
		}
		return result;
	}

	public static Path2D.Float mkRegularPolygon(int n, float radius) {
		Path2D.Float p = new Path2D.Float();
		p.moveTo(radius, 0);
		for (int i = 0; i < n; i++) {
			float x = (float) (Math.cos((Math.PI * 2 * i) / n) * radius);
			float y = (float) (Math.sin((Math.PI * 2 * i) / n) * radius);
			p.lineTo(x, y);
		}
		p.closePath();
		return p;
	}

    public Path2D.Float getPolygonPath() {
        return polygonPath;
    }
}
