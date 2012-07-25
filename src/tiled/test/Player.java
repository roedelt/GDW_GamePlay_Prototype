package tiled.test;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;

public class Player {

	private boolean withinHotButtonReach   = false;
	private boolean withinColorBucketReach = false;
	private boolean moveDirection = true; // false = left | true = right
	private boolean isStanding = true;    // false = duck | true = stand
	private Animation[] animations = new Animation[4];
	private Polygon[] boundingBoxes = new Polygon[2];
	private Vector2f position = new Vector2f(320.0f, 240.0f);
	private Color color = new Color(Color.white);
	
	public Player(String spriteSheetRef, Vector2f startingPoint)
	{
		super();
		position.set(startingPoint);
		
		// exact bounding boxes, which won't collide like exacted
//		boundingBoxes[0] = new Polygon(	new float[] {10, 4, 48, 4, 48,64, 10,64} ); // bounding box while standing
//		boundingBoxes[1] = new Polygon(	new float[] {12,16, 64,16, 64,64, 10,64} ); // bounding box while ducking
		
		// standard definition (squares)
		boundingBoxes[0] = new Polygon(	new float[] {0,0, 64,0, 64,64, 0,64} ); // bounding box while standing
		boundingBoxes[1] = new Polygon(	boundingBoxes[0].getPoints() ); // bounding box while ducking
		
//		boundingBoxes[0] = new Polygon(	new float[] {16, 0, 48,0, 48,64, 16,64} ); // bounding box while standing
//		boundingBoxes[1] = new Polygon(	new float[] {16,16,	64,16, 64,64, 16,64} ); // bounding box while ducking
		
		try {
			SpriteSheet sheet = new SpriteSheet(spriteSheetRef, 64, 64);
			for (int i = 0; i < animations.length; ++i)
			{
				animations[i] = new Animation();
				animations[i].setAutoUpdate(false);
			}
			final int frameCount = sheet.getWidth() >> 6; // (sheet.getWidth() / 64)
			for (int frame = 0; frame < frameCount; ++frame)
			{
				animations[0].addFrame(sheet.getSprite(frame, 0), 150);								// walking right
				animations[1].addFrame(sheet.getSprite(frame, 0).getFlippedCopy(true, false), 150); // walking left
				animations[2].addFrame(sheet.getSprite(frame, 1), 150);								// ducking right
				animations[3].addFrame(sheet.getSprite(frame, 1).getFlippedCopy(true, false), 150); // ducking left
			}
		} catch (SlickException e) {
			e.printStackTrace();
			//TODO: run around and scream!
		}
	}
	

	public boolean isWithinHotButtonReach() { return withinHotButtonReach; }
	public void setWithinHotButtonReach(boolean withinHotButtonReach) { this.withinHotButtonReach = withinHotButtonReach; }

	public boolean isWithinColorBucketReach() { return withinColorBucketReach; }
	public void setWithinColorBucketReach(boolean withinColorBucketReach) { this.withinColorBucketReach = withinColorBucketReach; }

	public boolean isMovingLeft(){ return moveDirection == false; }
	public void setMoveDirection(boolean moveRight) { moveDirection = moveRight; }
	
	public boolean isStanding() { return isStanding; }
	public void setStanding(boolean isStanding) { this.isStanding = isStanding; }


	public Vector2f getPosition() { return position; }
	public void setPosition(float x, float y) { position.set(x, y); }
	public void setPosition(Vector2f position) { this.position = position; }
	public void setX(float x) { position.set(x, this.position.y); }
	public void setY(float y) { position.set(this.position.x, y); }
	
	public Polygon getBoundingPolygon()
	{
		int index = 0;
		if (!isStanding) { index = 1; } // wenn geduckt: index = 1 !
		
		float[] array = new float[] {
				position.x + boundingBoxes[index].getX()   , position.y,// + boundingBoxes[index].getY(),
				position.x + boundingBoxes[index].getMaxX(), position.y,// + boundingBoxes[index].getY(),
				position.x + boundingBoxes[index].getMaxX(), position.y + boundingBoxes[index].getMaxY(),
				position.x + boundingBoxes[index].getX()   , position.y + boundingBoxes[index].getMaxY()
		};
		
		Polygon poly = new Polygon(array);
		
//		Polygon poly = new Polygon(boundingBoxes[index].getPoints());
//		poly.setX(position.x + boundingBoxes[index].getWidth());
//		poly.setY(position.y + boundingBoxes[index].getHeight());
//		poly.prune();
		
		return poly;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public void drawAnimation()
	{
		if (isStanding) // false = duck | true = stand
		{
			if (moveDirection) // false = left | true = right
			{
				animations[0].draw(position.x, position.y, color); // walking right
			}
			else
			{
				animations[1].draw(position.x, position.y, color); // walking left
			}
		}
		else
		{
			if (moveDirection) // false = left | true = right
			{
				animations[2].draw(position.x, position.y, color); // ducking right
			}
			else
			{
				animations[3].draw(position.x, position.y, color); // ducking left
			}
		}
	}
	
	public void updateAnimation(long delta)
	{
		/* animation indexes:	0 = walking right
		 *						1 = walking left
		 *						2 = ducking right
		 *						3 = ducking left
		 */
		if (isStanding) // false = duck | true = stand
		{
			if (moveDirection) // false = left | true = right
			{
				animations[0].update(delta); // walking right
			}
			else
			{
				animations[1].update(delta); // walking left
			}
		}
		else
		{
			if (moveDirection) // false = left | true = right
			{
				animations[2].update(delta); // ducking right
			}
			else
			{
				animations[3].update(delta); // ducking left
			}
		}
	}
	
	public void restartAllAnimations()
	{
		for (int i = 0; i < animations.length; ++i)
		{
			animations[i].restart();
		}
	}
}
