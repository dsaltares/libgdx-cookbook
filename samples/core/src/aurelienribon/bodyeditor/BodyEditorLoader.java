package aurelienribon.bodyeditor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads the collision fixtures defined with the Physics Body Editor
 * application. You only need to give it a body and the corresponding fixture
 * name, and it will attach these fixtures to your body.
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class BodyEditorLoader {

	// Model
	private final Model model;

	// Reusable stuff
	private final List<Vector2> vectorPool = new ArrayList<Vector2>();
	private final PolygonShape polygonShape = new PolygonShape();
	private final CircleShape circleShape = new CircleShape();
	private final Vector2 vec = new Vector2();

	// -------------------------------------------------------------------------
	// Ctors
	// -------------------------------------------------------------------------

	public BodyEditorLoader(FileHandle file) {
		if (file == null) throw new NullPointerException("file is null");
		model = readJson(file.readString());
	}

	public BodyEditorLoader(String str) {
		if (str == null) throw new NullPointerException("str is null");
		model = readJson(str);
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Creates and applies the fixtures defined in the editor. The name
	 * parameter is used to retrieve the right fixture from the loaded file.
	 * <br/><br/>
	 *
	 * The body reference point (the red cross in the tool) is by default
	 * located at the bottom left corner of the image. This reference point
	 * will be put right over the BodyDef position point. Therefore, you should
	 * place this reference point carefully to let you place your body in your
	 * world easily with its BodyDef.position point. Note that to draw an image
	 * at the position of your body, you will need to know this reference point
	 * (see {@link #getOrigin(java.lang.String, float)}.
	 * <br/><br/>
	 *
	 * Also, saved shapes are normalized. As shown in the tool, the width of
	 * the image is considered to be always 1 meter. Thus, you need to provide
	 * a scale factor so the polygons get resized according to your needs (not
	 * every body is 1 meter large in your game, I guess).
	 *
	 * @param body The Box2d body you want to attach the fixture to.
	 * @param name The name of the fixture you want to load.
	 * @param fd The fixture parameters to apply to the created body fixture.
	 * @param scale The desired scale of the body. The default width is 1.
	 */
	public void attachFixture(Body body, String name, FixtureDef fd, float scale) {
		RigidBodyModel rbModel = model.rigidBodies.get(name);
		if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

		Vector2 origin = vec.set(rbModel.origin).scl(scale);

		for (int i=0, n=rbModel.polygons.size(); i<n; i++) {
			PolygonModel polygon = rbModel.polygons.get(i);
			Vector2[] vertices = polygon.buffer;

			for (int ii=0, nn=vertices.length; ii<nn; ii++) {
				vertices[ii] = newVec().set(polygon.vertices.get(ii)).scl(scale);
				vertices[ii].sub(origin);
			}

			polygonShape.set(vertices);
			fd.shape = polygonShape;
			body.createFixture(fd);

			for (int ii=0, nn=vertices.length; ii<nn; ii++) {
				free(vertices[ii]);
			}
		}

		for (int i=0, n=rbModel.circles.size(); i<n; i++) {
			CircleModel circle = rbModel.circles.get(i);
			Vector2 center = newVec().set(circle.center).scl(scale);
			float radius = circle.radius * scale;

			circleShape.setPosition(center);
			circleShape.setRadius(radius);
			fd.shape = circleShape;
			body.createFixture(fd);

			free(center);
		}
	}

	/**
	 * Gets the image path attached to the given name.
	 */
	public String getImagePath(String name) {
		RigidBodyModel rbModel = model.rigidBodies.get(name);
		if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

		return rbModel.imagePath;
	}

	/**
	 * Gets the origin point attached to the given name. Since the point is
	 * normalized in [0,1] coordinates, it needs to be scaled to your body
	 * size. Warning: this method returns the same Vector2 object each time, so
	 * copy it if you need it for later use.
	 */
	public Vector2 getOrigin(String name, float scale) {
		RigidBodyModel rbModel = model.rigidBodies.get(name);
		if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

		return vec.set(rbModel.origin).scl(scale);
	}

	/**
	 * <b>For advanced users only.</b> Lets you access the internal model of
	 * this loader and modify it. Be aware that any modification is permanent
	 * and that you should really know what you are doing.
	 */
	public Model getInternalModel() {
		return model;
	}

	// -------------------------------------------------------------------------
	// Json Models
	// -------------------------------------------------------------------------

	public static class Model {
		public final Map<String, RigidBodyModel> rigidBodies = new HashMap<String, RigidBodyModel>();
	}

	public static class RigidBodyModel {
		public String name;
		public String imagePath;
		public final Vector2 origin = new Vector2();
		public final List<PolygonModel> polygons = new ArrayList<PolygonModel>();
		public final List<CircleModel> circles = new ArrayList<CircleModel>();
	}

	public static class PolygonModel {
		public final List<Vector2> vertices = new ArrayList<Vector2>();
		private Vector2[] buffer; // used to avoid allocation in attachFixture()
	}

	public static class CircleModel {
		public final Vector2 center = new Vector2();
		public float radius;
	}

	// -------------------------------------------------------------------------
	// Json reading process
	// -------------------------------------------------------------------------

	private Model readJson(String str) {
		Model m = new Model();
		JsonValue root = new JsonReader().parse(str);
		JsonValue bodies = root.get("rigidBodies");
		
		for (JsonValue body = bodies.child(); body != null; body = body.next()) {
			RigidBodyModel rbModel = readRigidBody(body);
			m.rigidBodies.put(rbModel.name, rbModel);
		}

		return m;
	}

	private RigidBodyModel readRigidBody (JsonValue bodyElem) {
		RigidBodyModel rbModel = new RigidBodyModel();
		rbModel.name = bodyElem.getString("name");
		rbModel.imagePath = bodyElem.getString("imagePath");

		JsonValue origin = bodyElem.get("origin");

		rbModel.origin.x = origin.getFloat("x");
		rbModel.origin.y = origin.getFloat("y");

		// polygons
		JsonValue polygons = bodyElem.get("polygons");

		for (JsonValue vertices = polygons.child(); vertices != null; vertices = vertices.next()) {
			PolygonModel polygon = new PolygonModel();
			rbModel.polygons.add(polygon);

			for (JsonValue vertex = vertices.child(); vertex != null; vertex = vertex.next()) {
				polygon.vertices.add(new Vector2(vertex.getFloat("x"), vertex.getFloat("y")));
			}

			polygon.buffer = new Vector2[polygon.vertices.size()];
		}

		// circles
		JsonValue circles = bodyElem.get("circles");

		for (JsonValue circle = circles.child(); circle != null; circle = circle.next()) {
			CircleModel c = new CircleModel();
			rbModel.circles.add(c);

			c.center.x = circle.getFloat("cx");
			c.center.y = circle.getFloat("cy");
			c.radius = circle.getFloat("r");
		}

		return rbModel;
	}
	
	private RigidBodyModel readRigidBody(OrderedMap<String,?> bodyElem) {
		RigidBodyModel rbModel = new RigidBodyModel();
		rbModel.name = (String) bodyElem.get("name");
		rbModel.imagePath = (String) bodyElem.get("imagePath");

		OrderedMap<String,?> originElem = (OrderedMap<String,?>) bodyElem.get("origin");
		rbModel.origin.x = (Float) originElem.get("x");
		rbModel.origin.y = (Float) originElem.get("y");

		// polygons

		Array<?> polygonsElem = (Array<?>) bodyElem.get("polygons");

		for (int i=0; i<polygonsElem.size; i++) {
			PolygonModel polygon = new PolygonModel();
			rbModel.polygons.add(polygon);

			Array<?> verticesElem = (Array<?>) polygonsElem.get(i);
			for (int ii=0; ii<verticesElem.size; ii++) {
				OrderedMap<String,?> vertexElem = (OrderedMap<String,?>) verticesElem.get(ii);
				float x = (Float) vertexElem.get("x");
				float y = (Float) vertexElem.get("y");
				polygon.vertices.add(new Vector2(x, y));
			}

			polygon.buffer = new Vector2[polygon.vertices.size()];
		}

		// circles

		Array<?> circlesElem = (Array<?>) bodyElem.get("circles");

		for (int i=0; i<circlesElem.size; i++) {
			CircleModel circle = new CircleModel();
			rbModel.circles.add(circle);

			OrderedMap<String,?> circleElem = (OrderedMap<String,?>) circlesElem.get(i);
			circle.center.x = (Float) circleElem.get("cx");
			circle.center.y = (Float) circleElem.get("cy");
			circle.radius = (Float) circleElem.get("r");
		}

		return rbModel;
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private Vector2 newVec() {
		return vectorPool.isEmpty() ? new Vector2() : vectorPool.remove(0);
	}

	private void free(Vector2 v) {
		vectorPool.add(v);
	}
}
