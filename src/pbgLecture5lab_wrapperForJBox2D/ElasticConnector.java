package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Graphics2D;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJointDef;

public class ElasticConnector {
	private final BasicParticle particle1;
	private final BasicParticle particle2;
	private final float naturalLength;
	private final float springConstant;
	private final float vibrationalDampingConstant;
	private final boolean canGoSlack;
	private final Color col;
	private final Float hookesLawTruncation;

	public ElasticConnector(BasicParticle p1, BasicParticle p2, float naturalLength, float springConstant, float vibrationalDampingConstant,
							boolean canGoSlack, Color col, float hookesLawTruncation) {
		this.particle1 = p1;
		this.particle2 = p2;
		this.naturalLength = naturalLength;
		this.springConstant = springConstant;
		this.vibrationalDampingConstant = vibrationalDampingConstant;
		this.canGoSlack = canGoSlack;
		this.hookesLawTruncation = hookesLawTruncation;  // Use Float here
		this.col = col;

		DistanceJointDef jd = new DistanceJointDef();
		jd.initialize(p1.body, p2.body, p1.body.getPosition(), p2.body.getPosition());
		BasicPhysicsEngineUsingBox2D.world.createJoint(jd); // Add the joint to the world
	}

	public void draw(Graphics2D g) {
		int x1 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particle1.body.getPosition().x);
		int y1 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particle1.body.getPosition().y);
		int x2 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particle2.body.getPosition().x);
		int y2 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particle2.body.getPosition().y);
		g.setColor(col);
		g.drawLine(x1, y1, x2, y2);
	}

	public void applyTensionForceToBothParticles() {
		float tension = calculateTension();
		Vec2 p12 = new Vec2(particle2.body.getPosition().x - particle1.body.getPosition().x, particle2.body.getPosition().y - particle1.body.getPosition().y);
		p12.normalize(); // Normalize the vector

		Vec2 forceOnP1 = p12.mul(tension);
		particle1.body.applyForceToCenter(forceOnP1);

		Vec2 forceOnP2 = p12.mul(-tension); // Apply force in opposite direction
		particle2.body.applyForceToCenter(forceOnP2);
	}

	public float calculateTension() {
		float dist = particle1.body.getPosition().sub(particle2.body.getPosition()).length();
		if (dist < naturalLength && canGoSlack) return 0;

		float extensionRatio = (dist - naturalLength) / naturalLength;
		if (hookesLawTruncation != null) {
			if (extensionRatio > hookesLawTruncation) extensionRatio = hookesLawTruncation.floatValue();
			if (extensionRatio < -hookesLawTruncation) extensionRatio = -hookesLawTruncation.floatValue();
		}

		float tensionDueToHookesLaw = extensionRatio * springConstant;
		float tensionDueToVibrationalDamping = vibrationalDampingConstant * rateOfChangeOfExtension();
		return tensionDueToHookesLaw + tensionDueToVibrationalDamping;
	}

	public float rateOfChangeOfExtension() {
		Vec2 v12 = new Vec2(particle2.body.getPosition().x - particle1.body.getPosition().x,
				particle2.body.getPosition().y - particle1.body.getPosition().y);
		v12.normalize(); // Normalize the vector

		// Ensure both vectors are properly created for dot product
		Vec2 relativeVelocity = new Vec2(particle2.body.getLinearVelocity().x - particle1.body.getLinearVelocity().x,
				particle2.body.getLinearVelocity().y - particle1.body.getLinearVelocity().y);

		// Now the dot product can be correctly computed
		return Vec2.dot(relativeVelocity, v12);  // Corrected: passing two Vec2 objects
	}

}
