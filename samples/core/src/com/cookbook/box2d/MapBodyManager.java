package com.cookbook.box2d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @brief Populates box2D world with static bodies using data from a map object
 * 
 * It uses a JSON formatted materials file to assign properties to the static
 * bodies it creates. To assign a material to a shape add a "material" custom
 * property to the shape in question using your editor of choice (Tiled, Gleed,
 * Tide...). Such file uses the following structure:

@code
[
	{ "name" : "ice", "density" : 1.0, "restitution" : 0.0, "friction" : 0.1 },
	{ "name" : "elastic", "density" : 1.0, "restitution" : 0.8, "friction" : 0.8 }
]
@endcode

 * In case no material property is found, it'll get a default one. 
 *
 */
public class MapBodyManager {
	private Logger logger;
	private World world;
	private float units;
	private Array<Body> bodies = new Array<Body>();
	private ObjectMap<String, FixtureDef> materials = new ObjectMap<String, FixtureDef>();

	/**
	 * @param world box2D world to work with.
	 * @param unitsPerPixel conversion ratio from pixel units to box2D metres.
	 * @param materialsFile json file with specific physics properties to be assigned to newly created bodies.
	 * @param loggingLevel verbosity of the embedded logger.
	 */
	public MapBodyManager(World world, float unitsPerPixel, FileHandle materialsFile, int loggingLevel) {
		logger = new Logger("MapBodyManager", loggingLevel);
		logger.info("initialising");

		this.world = world;
		this.units = unitsPerPixel;


		if (materialsFile != null) {
			loadMaterialsFile(materialsFile);
		}
	}

	/**
	 * @param map will use the "physics" layer of this map to look for shapes in order to create the static bodies.
	 */
	public void createPhysics(Map map) {
		createPhysics(map, "physics");
	}

	/**
	 * @param map map to be used to create the static bodies. 
	 * @param layerName name of the layer that contains the shapes.
	 */
	public void createPhysics(Map map, String layerName) {
		MapLayer layer = map.getLayers().get(layerName);

		if (layer == null) {
			logger.error("layer " + layerName + " does not exist");
			return;
		}

		MapObjects objects = layer.getObjects();

		for(MapObject object : layer.getObjects()) {

			if (object instanceof TextureMapObject){
				continue;
			}

			Shape shape;
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.StaticBody;

			if (object instanceof RectangleMapObject) {
				RectangleMapObject rectangle = (RectangleMapObject)object;
				shape = getRectangle(rectangle);
			}
			else if (object instanceof PolygonMapObject) {
				shape = getPolygon((PolygonMapObject)object);
			}
			else if (object instanceof PolylineMapObject) {
				shape = getPolyline((PolylineMapObject)object);
			}
			else if (object instanceof CircleMapObject) {
				shape = getCircle((CircleMapObject)object);
			}
			else {
				logger.error("non suported shape " + object);
				continue;
			}

			MapProperties properties = object.getProperties();
			String material = properties.get("material", "default", String.class);
			FixtureDef fixtureDef = materials.get(material);

			if (fixtureDef == null) {
				logger.error("material does not exist " + material + " using default");
				fixtureDef = materials.get("default");
			}

			logger.info("Body: " + object.getName() + ", material: " + material);
			
			fixtureDef.shape = shape;

			Body body = world.createBody(bodyDef);
			body.createFixture(fixtureDef);

			bodies.add(body);

			fixtureDef.shape = null;
			shape.dispose();
		}
	}

	/**
	 * Destroys every static body that has been created using the manager.
	 */
	public void destroyPhysics() {
		for (Body body : bodies) {
			world.destroyBody(body);
		}

		bodies.clear();
	}

	private void loadMaterialsFile(FileHandle materialsFile) {
		logger.info("adding default material");

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 0.0f;
		materials.put("default", fixtureDef);

		logger.info("loading materials file");

		try {
			JsonReader reader = new JsonReader();
			JsonValue root = reader.parse(materialsFile);
			JsonIterator materialIt = root.iterator();

			while (materialIt.hasNext()) {
				JsonValue materialValue = materialIt.next();

				if (!materialValue.has("name")) {
					logger.error("material without name");
					continue;
				}

				String name = materialValue.getString("name");

				fixtureDef = new FixtureDef();
				fixtureDef.density = materialValue.getFloat("density", 1.0f);
				fixtureDef.friction = materialValue.getFloat("friction", 1.0f);
				fixtureDef.restitution = materialValue.getFloat("restitution", 0.0f);
				logger.info("adding material " + name);
				materials.put(name, fixtureDef);
			}

		} catch (Exception e) {
			logger.error("error loading " + materialsFile.name() + " " + e.getMessage());
		}
	}

	private Shape getRectangle(RectangleMapObject rectangleObject) {
		Rectangle rectangle = rectangleObject.getRectangle();
		PolygonShape polygon = new PolygonShape();
		Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / units,
		                           (rectangle.y + rectangle.height * 0.5f ) / units);
		polygon.setAsBox(rectangle.width * 0.5f / units,
		                 rectangle.height * 0.5f / units,
		                 size,
		                 0.0f);
		return polygon;
	}

	private Shape getCircle(CircleMapObject circleObject) {
		Circle circle = circleObject.getCircle();
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(circle.radius / units);
		circleShape.setPosition(new Vector2(circle.x / units, circle.y / units));
		return circleShape;
	}

	private Shape getPolygon(PolygonMapObject polygonObject) {
		PolygonShape polygon = new PolygonShape();
		float[] vertices = polygonObject.getPolygon().getTransformedVertices();

		float[] worldVertices = new float[vertices.length];

		for (int i = 0; i < vertices.length; ++i) {
		    worldVertices[i] = vertices[i] / units;
		}

		polygon.set(worldVertices);
		return polygon;
	}

	private Shape getPolyline(PolylineMapObject polylineObject) {
		float[] vertices = polylineObject.getPolyline().getTransformedVertices();
		Vector2[] worldVertices = new Vector2[vertices.length / 2];

		for (int i = 0; i < vertices.length / 2; ++i) {
		    worldVertices[i] = new Vector2();
		    worldVertices[i].x = vertices[i * 2] / units;
		    worldVertices[i].y = vertices[i * 2 + 1] / units;
		}

		ChainShape chain = new ChainShape(); 
		chain.createChain(worldVertices);
		return chain;
	}
}