package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.dynamics.*;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;

public class BasicParticle {
	public final int SCREEN_RADIUS;

	private final float linearDragForce, mass;
	public final Color col;
	protected final Body body;

	public BasicParticle(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float linearDragForce) {
		World w = BasicPhysicsEngineUsingBox2D.world; // Box2D object
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
		int x = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(body.getPosition().x);
		int y = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(body.getPosition().y);
		g.setColor(col);
		g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);
	}

	public void notificationOfNewTimestep() {
		if (linearDragForce > 0) {
			Vec2 dragForce = new Vec2(body.getLinearVelocity());
			dragForce = dragForce.mul(-linearDragForce * mass);
			body.applyForceToCenter(dragForce);
		}
	}
}
