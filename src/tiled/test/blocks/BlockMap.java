package tiled.test.blocks;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import tiled.test.entities.ColorBucket;
import tiled.test.entities.HotButton;
import tiled.test.entities.MoveableObject;
import tiled.test.entities.MovementDirection;
import tiled.test.entities.RotatableObject;
 
public class BlockMap {
	public static TiledMap tmap;
	public static int mapWidth;
	public static int mapHeight;
	private final int square[] = {1,1,15,1,15,15,1,15}; //square shaped tile
	private static Integer visibleLayers[]; // array, which layers should be rendered
	public static ArrayList<Block> collisionBlockList;
	public static ArrayList<HotButton> hotButtonList;
	public static ArrayList<ColorBucket> colorBucketList;
	public static ArrayList<RotatableObject> rotatableObjectList;
	public static ArrayList<MoveableObject> moveableObjectList;
	private static Vector2f playerStartVector;
	
	public static Vector2f getPlayerStart()
	{
		return playerStartVector;
	}
	
	public static Integer[] getVisibleLayers()
	{
		return visibleLayers.clone();
	}
	
	public BlockMap(String ref) throws SlickException {
		// ArrayList initializations
		collisionBlockList  = new ArrayList<Block>();
		hotButtonList       = new ArrayList<HotButton>();
		colorBucketList     = new ArrayList<ColorBucket>();
		rotatableObjectList = new ArrayList<RotatableObject>();
		moveableObjectList  = new ArrayList<MoveableObject>();
		
		tmap            = new TiledMap(ref, "data");
		mapWidth        = tmap.getWidth() * tmap.getTileWidth();
		mapHeight       = tmap.getHeight() * tmap.getTileHeight();
		
		// create an ArrayList of all visible layers
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		for (int i = 0; i < tmap.getLayerCount(); ++i)
		{
			if ("false".equals(tmap.getLayerProperty(i, "invisible", "false")))
			{
				tempList.add(i);
			}
		}
		// cast this ArrayList to an Array of Integer
		tempList.trimToSize();
		visibleLayers = new Integer[tempList.size()];
		tempList.toArray(visibleLayers);
		tempList.clear();
 
		// check for the layer named "Collision" (case-sensitive) and create collision geometry
		final int layerIndex = tmap.getLayerIndex("Collision");
		for (int x = 0; x < tmap.getWidth(); ++x) {
			for (int y = 0; y < tmap.getHeight(); ++y) {
				int tileID = tmap.getTileId(x, y, layerIndex); // layerIndex == collision layer
				if (tileID == 1) {
					collisionBlockList.add( new Block(x * 16, y * 16, square, "square") );
				}
			}
		}
		
		for (int groups = 0; groups < tmap.getObjectGroupCount(); ++groups)
		{
			for (int count = 0; count < tmap.getObjectCount(groups); ++count)
			{
				final String type = tmap.getObjectType(groups, count);
				// if/else für alle Objekttypen
				if (type.equals("PlayerStart"))
				{
					playerStartVector = new Vector2f(
							tmap.getObjectX(groups, count),
							tmap.getObjectY(groups, count) //+ tmap.getObjectHeight(groups, count)
							);
				}
				else if (type.equals("HotButton"))
				{
					final int x     = tmap.getObjectX(groups, count)-1;
					final int y     = tmap.getObjectY(groups, count)-1;
					final int x_max = x + tmap.getObjectWidth(groups, count)-1;
					final int y_max = y + tmap.getObjectHeight(groups, count)-1;
					
					final float box[] = {x,y, x_max,y, x_max,y_max, x,y_max};
					final Polygon p = new Polygon(box);
					
					final Color color = new Color(Color.decode(tmap.getObjectProperty(groups, count, "color", "0x00000")));
					final String entityRef = tmap.getObjectProperty(groups, count, "triggeredEntity", "");
					hotButtonList.add(new HotButton(p, color, entityRef));
				}
				else if (type.equals("ColorBucket"))
				{
					final int x     = tmap.getObjectX(groups, count)-1;
					final int y     = tmap.getObjectY(groups, count)-1;
					final int x_max = x + tmap.getObjectWidth(groups, count)-1;
					final int y_max = y + tmap.getObjectHeight(groups, count)-1;
					
					final float box[] = {x,y, x_max,y, x_max,y_max, x,y_max};
					final Polygon p = new Polygon(box);
					
					final Color color = new Color(Color.decode(tmap.getObjectProperty(groups, count, "color", "0x00000"))); 
					colorBucketList.add(new ColorBucket(p, color));
				}
				else if (type.equals("MoveableObject"))
				{
					final int x     = tmap.getObjectX(groups, count)-1;
					final int y     = tmap.getObjectY(groups, count)-1;
					final int x_max = x + tmap.getObjectWidth(groups, count)-1;
					final int y_max = y + tmap.getObjectHeight(groups, count)-1;
					
					final float box[] = {x,y, x_max,y, x_max,y_max, x,y_max};
					final Polygon p = new Polygon(box);
					
					final Vector2f minPosition = new Vector2f(16 * Float.parseFloat(tmap.getObjectProperty(groups, count, "minPositionX", "0")),
															  16 * Float.parseFloat(tmap.getObjectProperty(groups, count, "minPositionY", "0")));
					final Vector2f maxPosition = new Vector2f(16 * Float.parseFloat(tmap.getObjectProperty(groups, count, "maxPositionX", "0")),
							  								  16 * Float.parseFloat(tmap.getObjectProperty(groups, count, "maxPositionY", "0")));
					final float acceleration = Float.parseFloat(tmap.getObjectProperty(groups, count, "acceleration", "0"));
					final MovementDirection initialDirection = MovementDirection.valueOf(tmap.getObjectProperty(groups, count, "initialDirection", "NONE"));
					moveableObjectList.add(new MoveableObject(p, minPosition, maxPosition, acceleration, initialDirection));
				}
				else if (type.equals("RotatableObject"))
				{
					final int x     = tmap.getObjectX(groups, count)-1;
					final int y     = tmap.getObjectY(groups, count)-1;
					final int x_max = x + tmap.getObjectWidth(groups, count)-1;
					final int y_max = y + tmap.getObjectHeight(groups, count)-1;
					
					final float box[] = {x,y, x_max,y, x_max,y_max, x,y_max};
					final Polygon p = new Polygon(box);
					
					final Color color = new Color(Color.decode(tmap.getObjectProperty(groups, count, "color", "0x00000")));
					
					//final float pivotX = x_max * Float.parseFloat(tmap.getObjectProperty(groups, count, "pivotX", "0"));
					//final float pivotY = y_max * Float.parseFloat(tmap.getObjectProperty(groups, count, "pivotY", "0"));
					final float pivotX = Float.parseFloat(tmap.getObjectProperty(groups, count, "pivotX", "0"));
					final float pivotY = Float.parseFloat(tmap.getObjectProperty(groups, count, "pivotY", "0"));
					final float acceleration = Float.parseFloat(tmap.getObjectProperty(groups, count, "acceleration", "0"));
					final MovementDirection direction = MovementDirection.valueOf(tmap.getObjectProperty(groups, count, "direction", "NONE"));
					final String entityRef = tmap.getObjectProperty(groups, count, "triggeredBy", "");
					rotatableObjectList.add(new RotatableObject(p, color, pivotX, pivotY, acceleration, direction, entityRef));
				}
				else if (true)
				{
					
				}
			}
		}
	}
}