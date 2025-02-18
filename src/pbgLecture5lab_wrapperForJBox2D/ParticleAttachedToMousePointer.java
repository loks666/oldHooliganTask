package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.common.Vec2;

public class ParticleAttachedToMousePointer extends BasicParticle {
	public ParticleAttachedToMousePointer(float sx, float sy, float vx, float vy, float radius, float mass) {
		super(sx, sy, vx, vy, radius, Color.CYAN, mass, 0.0f);
		body.setType(BodyType.KINEMATIC); // This means we control the motion of this particle directly
	}

	@Override
	public void notificationOfNewTimestep() {
		body.setTransform(new Vec2(BasicMouseListener.getWorldCoordinatesOfMousePointer()), 0.0f);
	}
}
