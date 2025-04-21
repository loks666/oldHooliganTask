package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.MouseJoint;

public class BasicPhysicsEngineUsingBox2D {
    /*
     * Author: Michael Fairbank
     * Creation Date: 2016-02-05 (JBox2d version)
     * Significant changes applied:
     * - Slingshot implementation added for Angry Birds game
     */

    // frame dimensions
    public static final int SCREEN_HEIGHT = 680;
    public static final int SCREEN_WIDTH = 640;
    public static final Dimension FRAME_SIZE = new Dimension(
            SCREEN_WIDTH, SCREEN_HEIGHT);
    public static final float WORLD_WIDTH = 10;// metres
    public static final float WORLD_HEIGHT = SCREEN_HEIGHT * (WORLD_WIDTH / SCREEN_WIDTH);// meters - keeps world
                                                                                          // dimensions in same aspect
                                                                                          // ratio as screen dimensions,
                                                                                          // so that circles get
                                                                                          // transformed into circles as
                                                                                          // opposed to ovals
    public static final float GRAVITY = 20.0f; // Increased from 9.8f for more realistic feeling
    public static final boolean ALLOW_MOUSE_POINTER_TO_DRAG_BODIES_ON_SCREEN = true;// There's a load of code in basic
                                                                                    // mouse listener to process this,
                                                                                    // if you set it to true

    public static World world; // Box2D container for all bodies and barriers

    // sleep time between two drawn frames in milliseconds
    public static final int DELAY = 20;
    public static final int NUM_EULER_UPDATES_PER_SCREEN_REFRESH = 10;
    // estimate for time between two frames in seconds
    public static final float DELTA_T = DELAY / 1000.0f;

    public static float convertWorldLengthToScreenLength(float worldLength) {
        return (worldLength / WORLD_WIDTH * SCREEN_WIDTH);
    }

    // 这里修复了你的转换方法：
    public static int convertWorldXtoScreenX(float worldX) {
        return (int) (worldX * SCREEN_WIDTH / WORLD_WIDTH);
    }

    public static int convertWorldYtoScreenY(float worldY) {
        return (int) (SCREEN_HEIGHT - worldY * SCREEN_HEIGHT / WORLD_HEIGHT);
    }

    public static float convertScreenXtoWorldX(int screenX) {
        return (float) screenX * WORLD_WIDTH / SCREEN_WIDTH;
    }

    public static float convertScreenYtoWorldY(int screenY) {
        return (float) ((SCREEN_HEIGHT - screenY) * WORLD_HEIGHT / SCREEN_HEIGHT);
    }

    public List<BasicParticle> particles;
    public List<BasicPolygon> polygons;
    public List<AnchoredBarrier> barriers;
    public List<ElasticConnector> connectors;
    public static MouseJoint mouseJointDef;

    // Slingshot reference
    private Slingshot slingshot;

    public enum LayoutMode {
        CONVEX_ARENA, CONCAVE_ARENA, CONVEX_ARENA_WITH_CURVE, PINBALL_ARENA, RECTANGLE, SNOOKER_TABLE
    }

    public BasicPhysicsEngineUsingBox2D() {
        world = new World(new Vec2(0, -GRAVITY)); // 创建 Box2D 容器
        world.setContinuousPhysics(true);

        particles = new ArrayList<>();
        polygons = new ArrayList<>();
        barriers = new ArrayList<>();
        connectors = new ArrayList<>();

        // 设置小球参数
        float linearDragForce = .02f; // 空气阻力
        float r = .3f; // 小球半径
        // 假设线段在以下位置
        float groundX = WORLD_WIDTH / 2; // 水平居中
        float groundY = WORLD_HEIGHT / 3; // 地面线段的位置
        float blockWidth = 0.5f; // 积木块宽度，根据需求调整

        // Create slingshot and position it to the left of the scene
        slingshot = new Slingshot(1.5f, groundY + 1.0f, 0.8f, 1.2f);

        // Set the slingshot reference in the mouse listener
        BasicMouseListener.setSlingshot(slingshot);

        createRectangleBlocks();

        // Create the bird but don't add it to the world yet - we'll load it into the
        // slingshot
        createBirdAndLoadSlingshot();

        LayoutMode layout = LayoutMode.CONVEX_ARENA;

        int model = 0;
        if (model == 1) {
            // spaceship flying under gravity
            particles.add(new ControllableSpaceShip(3 * r + WORLD_WIDTH / 2 + 1, WORLD_HEIGHT / 2 - 2, 0f, 2f, r, true,
                    2 * 4));
        } else if (model == 2) {
            // spaceship flying with dangling pendulum
            float springConstant = 1000000, springDampingConstant = 1000;
            float hookesLawTruncation = 0.2f;
            particles.add(new ControllableSpaceShip(3 * r + WORLD_WIDTH / 2 + 1, WORLD_HEIGHT / 2 - 2, 0f, 2f, r, true,
                    2 * 4));
            particles.add(new BasicParticle(3 * r + WORLD_WIDTH / 2 + 1, WORLD_HEIGHT / 2 - 4, -3f, 9.7f, r, Color.BLUE,
                    2 * 4, linearDragForce));
            connectors.add(new ElasticConnector(particles.get(0), particles.get(1), r * 6, springConstant,
                    springDampingConstant, false, Color.WHITE, hookesLawTruncation));
        } else if (model == 3) {
            // Simple pendulum attached under mouse pointer
            linearDragForce = .5f;
            float springConstant = 10000, springDampingConstant = 10;
            Float hookesLawTruncation = 0.2F;
            boolean canGoSlack = false;
            particles.add(new ParticleAttachedToMousePointer(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0, 0, r, 10000));
            particles.add(new BasicParticle(WORLD_WIDTH / 2, WORLD_HEIGHT / 2 - 2, 0, 0, r, Color.BLUE, 2 * 4,
                    linearDragForce));
            connectors.add(new ElasticConnector(particles.get(0), particles.get(1), r * 10, springConstant,
                    springDampingConstant, canGoSlack, Color.WHITE, hookesLawTruncation));
        } else if (model == 4) {
            // 4 link chain
            linearDragForce = 1;
            float springConstant = 1000000, springDampingConstant = 1000;
            Float hookesLawTruncation = 0.2F;// 0.2;//null;//0.2;
            particles.add(new ParticleAttachedToMousePointer(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0, 0, r, 10000));
            particles.add(new BasicParticle(WORLD_WIDTH / 2, WORLD_HEIGHT / 2 - 2, 0, 0, r / 2, Color.BLUE, 2 * 4,
                    linearDragForce));
            connectors.add(new ElasticConnector(particles.get(0), particles.get(1), r * 6, springConstant,
                    springDampingConstant, false, Color.WHITE, hookesLawTruncation));
            particles.add(new BasicParticle(WORLD_WIDTH / 2, WORLD_HEIGHT / 2 - 4, 0, 0, r / 2, Color.BLUE, 2 * 4,
                    linearDragForce));
            connectors.add(new ElasticConnector(particles.get(1), particles.get(2), r * 6, springConstant,
                    springDampingConstant, false, Color.WHITE, hookesLawTruncation));
            particles.add(new BasicParticle(WORLD_WIDTH / 2, WORLD_HEIGHT / 2 - 6, 0, 0, r / 2, Color.BLUE, 2 * 4,
                    linearDragForce));
            connectors.add(new ElasticConnector(particles.get(2), particles.get(3), r * 6, springConstant,
                    springDampingConstant, false, Color.WHITE, hookesLawTruncation));
            particles.add(new BasicParticle(WORLD_WIDTH / 2, WORLD_HEIGHT / 2 - 7, 0, 0, r / 2, Color.BLUE, 2 * 4,
                    linearDragForce));
            connectors.add(new ElasticConnector(particles.get(3), particles.get(4), r * 6, springConstant,
                    springDampingConstant, false, Color.WHITE, hookesLawTruncation));
        } else if (model == 5) {
            // rectangle box
            linearDragForce = .1f;
            float springConstant = 1000000, springDampingConstant = 1000;
            float hookesLawTruncation = 0.2f;
            // particles.add(new
            // ParticleAttachedToMousePointer(WORLD_WIDTH/2,WORLD_HEIGHT/2,0,0, r/2, true,
            // 10000));
            particles.add(new BasicParticle(WORLD_WIDTH / 2, WORLD_HEIGHT / 2 - 1, 24, 34, r / 2, Color.BLUE, 2 * 4,
                    linearDragForce));
            particles.add(new BasicParticle(WORLD_WIDTH / 2 + 0.1f, WORLD_HEIGHT / 2 - 2, 0f, 0f, r / 2, Color.BLUE,
                    2 * 4, linearDragForce));
            connectors.add(new ElasticConnector(particles.get(0), particles.get(1), r * 6, springConstant,
                    springDampingConstant, false, Color.WHITE, hookesLawTruncation));
            particles.add(new BasicParticle(WORLD_WIDTH / 2 + 0.1f, WORLD_HEIGHT / 2 - 4, -14, 14, r / 2, Color.BLUE,
                    2 * 4, linearDragForce));
            connectors.add(new ElasticConnector(particles.get(1), particles.get(2), r * 6, springConstant,
                    springDampingConstant, false, Color.WHITE, hookesLawTruncation));
            particles.add(new BasicParticle(WORLD_WIDTH / 2, WORLD_HEIGHT / 2 - 6, 0, 0, r / 2, Color.BLUE, 2 * 4,
                    linearDragForce));
            connectors.add(new ElasticConnector(particles.get(2), particles.get(3), r * 6, springConstant,
                    springDampingConstant, false, Color.WHITE, hookesLawTruncation));
            connectors.add(new ElasticConnector(particles.get(3), particles.get(0), r * 6, springConstant,
                    springDampingConstant, false, Color.WHITE, hookesLawTruncation));
            connectors.add(new ElasticConnector(particles.get(2), particles.get(0), (float) (r * 6 * Math.sqrt(6)),
                    springConstant, springDampingConstant, false, Color.GRAY, hookesLawTruncation));
            connectors.add(new ElasticConnector(particles.get(1), particles.get(3), (float) (r * 6 * Math.sqrt(6)),
                    springConstant, springDampingConstant, false, Color.GRAY, hookesLawTruncation));
        }

        if (model == 6) {
            Random x = new Random(3);
            for (int i = 0; i < 40; i++) {
                particles.add(new BasicParticle((0.5f + 0.3f * (x.nextFloat() - .5f)) * WORLD_HEIGHT,
                        (0.5f + 0.3f * (x.nextFloat() - .5f)) * WORLD_WIDTH, 0f, 0f, r / 2,
                        new Color(x.nextFloat(), x.nextFloat(), x.nextFloat()), .2f, linearDragForce));
            }
        }

        // particles.add(new BasicParticle(r,r,5,12, r,false, Color.GRAY,
        // includeInbuiltCollisionDetection));

        barriers = new ArrayList<>();

        switch (layout) {
            case RECTANGLE: {
                // rectangle walls:
                // anticlockwise listing
                // These would be better created as a JBox2D "chain" type object for efficiency
                // and potentially better collision detection at joints.
                barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
                break;
            }
            case CONVEX_ARENA: {
                barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT / 3, WORLD_WIDTH / 8, WORLD_HEIGHT / 3,
                        Color.WHITE));
                // 确保第二条线段的起点与第一条线段有间距，且两条线段平行
                barriers.add(
                        new AnchoredBarrier_StraightLine(WORLD_WIDTH / 3, groundY, WORLD_WIDTH, groundY, Color.WHITE));
                break;
            }
            case CONCAVE_ARENA: {
                // These would be better created as a JBox2D "chain" type object for efficiency
                // and potentially better collision detection at joints.
                barriers.add(new AnchoredBarrier_StraightLine(0f, WORLD_HEIGHT / 3, WORLD_WIDTH / 2, 0, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH / 2, 0, WORLD_WIDTH, WORLD_HEIGHT / 3,
                        Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT / 3, WORLD_WIDTH, WORLD_HEIGHT,
                        Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(0f, WORLD_HEIGHT, 0, WORLD_HEIGHT / 3, Color.WHITE));
                float width = WORLD_HEIGHT / 20;
                barriers.add(new AnchoredBarrier_StraightLine(0f, WORLD_HEIGHT * 2 / 3, WORLD_WIDTH / 2,
                        WORLD_HEIGHT * 1 / 2, Color.WHITE));
                barriers.add(new AnchoredBarrier_Point(WORLD_WIDTH / 2, WORLD_HEIGHT * 1 / 2));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH / 2, WORLD_HEIGHT * 1 / 2, WORLD_WIDTH / 2,
                        WORLD_HEIGHT * 1 / 2 - width, Color.WHITE));
                barriers.add(new AnchoredBarrier_Point(WORLD_WIDTH / 2, WORLD_HEIGHT * 1 / 2 - width));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH / 2, WORLD_HEIGHT * 1 / 2 - width, 0,
                        WORLD_HEIGHT * 2 / 3 - width, Color.WHITE));
                break;
            }
            case CONVEX_ARENA_WITH_CURVE: {
                // These would be better created as a JBox2D "chain" type object for efficiency
                // and potentially better collision detection at joints.
                barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT / 3, WORLD_WIDTH / 2, 0, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH / 2, 0, WORLD_WIDTH, WORLD_HEIGHT / 3,
                        Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT / 3, WORLD_WIDTH, WORLD_HEIGHT,
                        Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
                barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH / 2, WORLD_HEIGHT - WORLD_WIDTH / 2, WORLD_WIDTH / 2,
                        0.0f, 180.0f, Color.WHITE));
                break;
            }
            case PINBALL_ARENA: {
                // These would be better created as a JBox2D "chain" type object for efficiency
                // and potentially better collision detection at joints.
                // simple pinball board
                barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
                barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH / 2, WORLD_HEIGHT - WORLD_WIDTH / 2, WORLD_WIDTH / 2,
                        0.0f, 200.0f, Color.WHITE));
                barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH / 2, WORLD_HEIGHT * 3 / 4, WORLD_WIDTH / 15, -0.0f,
                        360.0f, Color.WHITE));
                barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH * 1 / 3, WORLD_HEIGHT * 1 / 2, WORLD_WIDTH / 15,
                        -0.0f, 360.0f, Color.WHITE));
                barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH * 2 / 3, WORLD_HEIGHT * 1 / 2, WORLD_WIDTH / 15,
                        -0.0f, 360.0f, Color.WHITE));
                break;
            }
            case SNOOKER_TABLE: {
                // These would be better created as a JBox2D "chain" type object for efficiency
                // and potentially better collision detection at joints.
                float snookerTableHeight = WORLD_HEIGHT;
                float pocketSize = 0.4f;
                float cushionDepth = 0.3f;
                float cushionLength = snookerTableHeight / 2 - pocketSize - cushionDepth;
                float snookerTableWidth = cushionLength + cushionDepth * 2 + pocketSize * 2;

                createCushion(barriers, snookerTableWidth - cushionDepth / 2, snookerTableHeight * 0.25f, 0,
                        cushionLength, cushionDepth);
                createCushion(barriers, snookerTableWidth - cushionDepth / 2, snookerTableHeight * 0.75f, 0,
                        cushionLength, cushionDepth);
                createCushion(barriers, snookerTableWidth / 2, snookerTableHeight - cushionDepth / 2, Math.PI / 2,
                        cushionLength, cushionDepth);
                createCushion(barriers, cushionDepth / 2, snookerTableHeight * 0.25f, Math.PI, cushionLength,
                        cushionDepth);
                createCushion(barriers, cushionDepth / 2, snookerTableHeight * 0.75f, Math.PI, cushionLength,
                        cushionDepth);
                createCushion(barriers, snookerTableWidth / 2, cushionDepth / 2, Math.PI * 3 / 2, cushionLength,
                        cushionDepth);

                break;
            }
        }
    }

    // 创建金字塔积木
    public void createPyramid(int layers, float blockWidth, float groundX, float groundY) {
        Color[] colors = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA };
        float epsilon = 0.01f; // 防止重叠误差

        for (int i = 0; i < layers; i++) {
            // 计算当前层的方块数量 (底层最多)
            int blocksInThisLayer = layers - i;

            // 每一层方块中心的垂直位置
            float centerY = groundY + blockWidth / 2 + (blockWidth + epsilon) * i;

            // 每层第一个方块的X坐标（严格居中）
            float startX = groundX - ((blocksInThisLayer - 1) * (blockWidth + epsilon)) / 2;

            for (int j = 0; j < blocksInThisLayer; j++) {
                float blockX = startX + j * (blockWidth + epsilon);

                // 创建方块（角度应确保为0）
                polygons.add(new BasicPolygon(
                        blockX, centerY,
                        0, 0,
                        blockWidth, colors[(i + j) % colors.length],
                        1.0f, 0.1f, 4));
            }

            // 每一层的垂直位置更新（从地面往上叠）
            groundY += blockWidth + epsilon;
        }
    }

    private void createRectangleBlocks() {
        float blockWidth = 0.8f; // 方块的尺寸
        float blockHeight = 0.8f;
        int rows = 3;
        int cols = 3; // 改为3列
        float startX = 8.0f; // 起始位置
        float startY = 5.0f; // 高度
        float gap = 0.3f; // 间距

        float groundY = startY - rows * (blockHeight + gap) - gap; // 地面的Y坐标
        float leftX = startX - gap; // 左边界的X坐标
        float rightX = startX + cols * (blockWidth + gap) + gap; // 右边界的X坐标

        // 只创建地面
        barriers.add(new AnchoredBarrier_StraightLine(leftX, groundY, rightX, groundY, Color.WHITE));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                float centerX = startX + j * (blockWidth + gap);
                float centerY = startY - i * (blockHeight + gap);

                // 创建小猪
                polygons.add(new Pig(centerX, centerY, 0, 0, blockWidth / 2));
            }
        }
    }

    public void createBirdAndLoadSlingshot() {
        float ballRadius = 0.3f; // 小球的半径
        AngryBird bird = new AngryBird(0, 0, 0, 0, ballRadius);
        particles.add(bird);
        slingshot.loadBird(bird);
    }

    private void createCushion(List<AnchoredBarrier> barriers, float centrex, float centrey, double orientation,
            float cushionLength, float cushionDepth) {
        // on entry, we require centrex,centrey to be the centre of the rectangle that
        // contains the cushion.
        Color col = Color.WHITE;
        Vec2 p1 = new Vec2(cushionDepth / 2, -cushionLength / 2 - cushionDepth / 2);
        Vec2 p2 = new Vec2(-cushionDepth / 2, -cushionLength / 2);
        Vec2 p3 = new Vec2(-cushionDepth / 2, +cushionLength / 2);
        Vec2 p4 = new Vec2(cushionDepth / 2, cushionLength / 2 + cushionDepth / 2);
        p1 = rotateVec(p1, orientation);
        p2 = rotateVec(p2, orientation);
        p3 = rotateVec(p3, orientation);
        p4 = rotateVec(p4, orientation);
        // we are being careful here to list edges in an anticlockwise manner, so that
        // normals point inwards!
        // barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p1.x),
        // (float) (centrey + p1.y), (float) (centrex + p2.x), (float) (centrey + p2.y),
        // col));
        // barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p2.x),
        // (float) (centrey + p2.y), (float) (centrex + p3.x), (float) (centrey + p3.y),
        // col));
        // barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p3.x),
        // (float) (centrey + p3.y), (float) (centrex + p4.x), (float) (centrey + p4.y),
        // col));
        // oops this will have concave corners so will need to fix that some time!
    }

    private static Vec2 rotateVec(Vec2 v, double angle) {
        // I couldn't find a rotate function in Vec2 so had to write own temporary one
        // here, just for the sake of
        // cushion rotation for snooker table...
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float nx = v.x * cos - v.y * sin;
        float ny = v.x * sin + v.y * cos;
        return new Vec2(nx, ny);
    }

    public static void main(String[] args) throws Exception {
        final BasicPhysicsEngineUsingBox2D game = new BasicPhysicsEngineUsingBox2D();
        final BasicView view = new BasicView(game);
        JEasyFrame frame = new JEasyFrame(view, "Angry Birds with Slingshot");
        frame.addKeyListener(new BasicKeyListener());

        // Create a single mouse listener instance to handle all mouse events
        BasicMouseListener mouseListener = new BasicMouseListener();
        view.addMouseMotionListener(mouseListener);
        view.addMouseListener(mouseListener); // Add mouse listener for press/release events

        game.startThread(view);
    }

    private void startThread(final BasicView view) throws InterruptedException {
        final BasicPhysicsEngineUsingBox2D game = this;
        while (true) {
            game.update();
            view.repaint();
            Toolkit.getDefaultToolkit().sync();
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
            }
        }
    }

    public void update() {
        int VELOCITY_ITERATIONS = NUM_EULER_UPDATES_PER_SCREEN_REFRESH;
        int POSITION_ITERATIONS = NUM_EULER_UPDATES_PER_SCREEN_REFRESH;
        for (BasicParticle p : particles) {
            // give the objects an opportunity to add any bespoke forces, e.g. drag forces
            p.notificationOfNewTimestep();

            // 检查小鸟和小猪的碰撞
            if (p instanceof AngryBird) {
                checkBirdPigCollision((AngryBird) p);
            }
        }
        for (BasicPolygon p : polygons) {
            // give the objects an opportunity to add any bespoke forces, e.g. drag forces
            p.notificationOfNewTimestep();
        }
        world.step(DELTA_T, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }

    private void checkBirdPigCollision(AngryBird bird) {
        Vec2 birdPos = bird.getBody().getPosition();
        boolean hasCollision = false;
        Pig collidedPig = null;

        for (BasicPolygon polygon : polygons) {
            if (polygon instanceof Pig) {
                Pig pig = (Pig) polygon;
                if (!pig.isDestroyed()) {
                    Vec2 pigPos = pig.getBody().getPosition();
                    float distance = pigPos.sub(birdPos).length();

                    // 如果距离小于两者半径之和，说明发生碰撞
                    if (distance < 2.0f) { // 增大碰撞检测的阈值
                        hasCollision = true;
                        collidedPig = pig;
                        break;
                    }
                }
            }
        }

        // 如果发生了碰撞，激活被撞击的方块及其周围的方块
        if (hasCollision && collidedPig != null) {
            Vec2 collidedPos = collidedPig.getBody().getPosition();

            for (BasicPolygon polygon : polygons) {
                if (polygon instanceof Pig) {
                    Pig pig = (Pig) polygon;
                    if (!pig.isDestroyed()) {
                        Vec2 pigPos = pig.getBody().getPosition();
                        float distance = pigPos.sub(collidedPos).length();

                        // 激活被撞击的方块和附近的方块
                        if (distance < 3.0f) { // 设置连锁反应的范围
                            pig.activate();
                        }
                    }
                }
            }
            System.out.println("小鸟击中了方块，开始连锁反应！");
        }
    }

    // Method to get the slingshot
    public Slingshot getSlingshot() {
        return slingshot;
    }
}
