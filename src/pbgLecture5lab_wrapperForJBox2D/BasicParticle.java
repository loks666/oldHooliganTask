package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class BasicParticle {
	private static BufferedImage birdImg; // 静态变量，用于所有粒子共享这张鸟的图

	static {
		try {
			// 使用相对路径加载图片
			birdImg = ImageIO.read(new File("resources/imgs/birds/red_bird.png"));
		} catch (IOException e) {
			System.err.println("无法加载小鸟图片：" + e.getMessage());
		}
	}

	public final int SCREEN_RADIUS;
	private final float linearDragForce, mass;
	public final Color col;
	protected final Body body;

	public BasicParticle(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float linearDragForce) {
		World w = BasicPhysicsEngineUsingBox2D.world;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set(sx, sy);
		bodyDef.linearVelocity.set(vx, vy);
		this.body = w.createBody(bodyDef);

		CircleShape circleShape = new CircleShape();
		circleShape.m_radius = radius;
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = mass / (float) (Math.PI * radius * radius);
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 1.0f;

		body.createFixture(fixtureDef);
		this.linearDragForce = linearDragForce;
		this.mass = mass;
		this.SCREEN_RADIUS = (int) Math.max(BasicPhysicsEngineUsingBox2D.convertWorldLengthToScreenLength(radius), 1);
		this.col = col;
	}

	public void draw(Graphics2D g) {
		// 计算该物体在屏幕上的位置
		int x = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(body.getPosition().x);
		int y = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(body.getPosition().y);
		int r = SCREEN_RADIUS; // 圆形的半径

		if (birdImg != null) {
			// 用图片替代圆形绘制
			g.drawImage(birdImg,
					x - r, y - r,     // 左上角
					x + r, y + r,     // 右下角
					0, 0, birdImg.getWidth(), birdImg.getHeight(),
					null);
		} else {
			// 如果图片未加载成功，则绘制一个绿色的圆形
			g.setColor(col);
			g.fillOval(x - r, y - r, 2 * r, 2 * r);
		}
	}

	public void notificationOfNewTimestep() {
		if (linearDragForce > 0) {
			Vec2 dragForce = new Vec2(body.getLinearVelocity());
			dragForce = dragForce.mul(-linearDragForce * mass);
			body.applyForceToCenter(dragForce);
		}
	}

	public Body getBody() {
		return body;
	}
}
