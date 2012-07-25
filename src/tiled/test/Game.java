package tiled.test;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

import tiled.test.blocks.Block;
import tiled.test.blocks.BlockMap;
import tiled.test.camera.Camera;
import tiled.test.entities.ColorBucket;
import tiled.test.entities.HotButton;
import tiled.test.entities.MoveableObject;
import tiled.test.entities.RotatableObject;

public class Game extends BasicGame {
 
	private Player playerOne;
	@SuppressWarnings("unused")	private BlockMap map;
	private Camera camera;
	private Image background;
	private final static int SCREEN_WIDTH  = 800;
	private final static int SCREEN_HEIGHT = 600;
	
	private boolean disregardCollisions = false;
	
	float grav = 0;
 
	public Game() {
		super("one class barebone game");
	}
	
	public void init(GameContainer container) throws SlickException {
		container.setVSync(true);
		container.setShowFPS(false);
		
		// load tiled map
		//map = new BlockMap("data/level01.tmx");
//		map = new BlockMap("data/testKarte.tmx");
		map = new BlockMap("data/Karte_proto.tmx");
		
		// create background image
//		background = new Image("data/area02_bkg0.jpg");
		
		// create PlayerEntity
		playerOne = new Player("data/teddy_anim.png", BlockMap.getPlayerStart());
		
		// get an array of all visible map layers
		camera = new Camera(container, BlockMap.tmap, BlockMap.getVisibleLayers());
	}
 
	public void update(GameContainer container, int delta) throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE))
		{
			container.exit();
		}
		if (container.getInput().isKeyPressed(Input.KEY_F2))
		{
			disregardCollisions = !disregardCollisions;
		}
		
		Polygon playerPoly = playerOne.getBoundingPolygon();
		
		// check if there's a button within the players reach
		playerOne.setWithinHotButtonReach(false);
		for (HotButton button : BlockMap.hotButtonList)
		{
			if (playerPoly.intersects(button.getShape()) || playerPoly.contains(button.getShape()))
			{
				playerOne.setWithinHotButtonReach(true);
				break;
			}
		}
		
		// check if there's a button within the players reach
		playerOne.setWithinColorBucketReach(false);
		for (ColorBucket bucket : BlockMap.colorBucketList)
		{
			if (playerPoly.intersects(bucket.getShape()) || playerPoly.contains(bucket.getShape()))
			{
				playerOne.setWithinColorBucketReach(true);
				break;
			}
		}
		
		// rotate objects
		for (RotatableObject rotO : BlockMap.rotatableObjectList)
		{
			rotO.startRotation();
			rotO.update(delta);
		}
		
		// move objects
		for (MoveableObject movO : BlockMap.moveableObjectList)
		{
			movO.update(delta);
		}
		
		// gravity simulation
		final float buf = playerOne.getPosition().y;
		final float tempY = buf + delta * grav;
		playerPoly.setY(tempY);
		grav += 0.00225f;
		if (entityCollisionWith(playerPoly))
		{
			playerPoly.setY(buf);
			grav = 0.0f;
		} else {
			playerOne.setY(tempY);
		}
		
		/* Input Handling */
		if (handleInput(container.getInput(), delta))
		{
			playerOne.updateAnimation(delta);
		}
		else
		{
			playerOne.restartAllAnimations();
		}
		
		//lock the camera on the player by default (player should be centered by the camera)
		//after calculating the positions of all entities
		camera.centerOn(playerPoly.getX(), playerPoly.getY());
	}
	
	private boolean handleInput(Input input, int delta) throws SlickException
	{
		boolean playerHasMoved = false;
		playerOne.setStanding(true);
		
		float playerX = playerOne.getPosition().x;
		float playerY = playerOne.getPosition().y;
		Polygon playerPoly = playerOne.getBoundingPolygon();
		
		if (input.isKeyDown(Input.KEY_LEFT))
		{
			playerOne.setMoveDirection(false);
			playerX -= 2;
			playerPoly.setX(playerX);
			if (entityCollisionWith(playerPoly) && !disregardCollisions){
				playerX += 2;
				playerPoly.setX(playerX);
			} else {
				playerHasMoved = true;
				playerOne.setPosition(playerX, playerY);
			}
		}
		if (input.isKeyDown(Input.KEY_RIGHT))
		{
			playerOne.setMoveDirection(true);
			playerX += 2;
			playerPoly.setX(playerX);
			if (entityCollisionWith(playerPoly) && !disregardCollisions){
				playerX -= 2;
				playerPoly.setX(playerX);
			} else {
				playerHasMoved = true;
				playerOne.setPosition(playerX, playerY);
			}
		}
		if (input.isKeyDown(Input.KEY_UP))
		{
			playerY -= 2;
			playerPoly.setY(playerY);
			if (entityCollisionWith(playerPoly) && !disregardCollisions){
				playerY += 2;
				playerPoly.setY(playerY);
				grav += 0.005f; // collision with ceiling, add gravity (and get a headache..)
			} else {
				playerHasMoved = true;
				playerOne.setPosition(playerX, playerY);
			}
		}
		if (input.isKeyDown(Input.KEY_DOWN))
		{
			playerOne.setStanding(false);
			playerY += 2;
			playerPoly.setY(playerY);
			if (entityCollisionWith(playerPoly) && !disregardCollisions){
				playerY -= 2;
				playerPoly.setY(playerY);
			} else {
				playerHasMoved = true;
				playerOne.setPosition(playerX, playerY);
			}
		}
		
		if (input.isKeyDown(Input.KEY_RETURN) || input.isKeyDown(Input.KEY_ENTER))
		{
			if (playerOne.isWithinColorBucketReach())
			{
				for (ColorBucket bucket : BlockMap.colorBucketList)
				{
					if (playerPoly.intersects(bucket.getShape()) || playerPoly.contains(bucket.getShape()) )
					{
						playerOne.setColor(bucket.getColor());
					}
				}
			}
		}
		
		playerOne.setPosition(playerX, playerY);
		
		return playerHasMoved;
	}
	
	public boolean entityCollisionWith(Polygon polygon) throws SlickException {
		for (Block entity : BlockMap.collisionBlockList)
		{
			if (polygon.intersects(entity.poly))
			{
				return true;
			} 
		}
		for (RotatableObject rotO : BlockMap.rotatableObjectList)
		{
			if(polygon.intersects(rotO.getShape()))
			{
				return true;
			}
		}
		return false;
	}
	
	public void render(GameContainer container, Graphics g)  {
		
		// draw background image
		//g.fillRect(0.0f, 0.0f, container.getWidth(), container.getHeight());
		//background.draw(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		//in the render()-method
		camera.drawMap();

		// draw information overlay
		if (playerOne.isWithinHotButtonReach())
		{
			final String str = new String("there's a button!");
			g.drawString(str, ((container.getWidth() >> 1) - (g.getFont().getWidth(str) >> 1)), 10);
		}
		if (playerOne.isWithinColorBucketReach())
		{
			final String str = new String("there's a ColorBucket!!");
			g.drawString(str, ((container.getWidth() >> 1) - (g.getFont().getWidth(str) >> 1)), 10);
		}
		if (disregardCollisions)
		{
			final String str = new String("NoClip mode");
			g.drawString(str, ((container.getWidth()) - (g.getFont().getWidth(str))), 10);
		}

		// re-translate everything, so it will be in its real position
		camera.translateGraphics();
		
		g.setColor(Color.orange);
		g.setLineWidth(1.0f);
		g.draw(playerOne.getBoundingPolygon());

		for (HotButton button : BlockMap.hotButtonList)
		{
			button.draw(g);
		}
		
		for (ColorBucket bucket : BlockMap.colorBucketList)
		{
			bucket.draw(g);
		}
		
		for (RotatableObject rotO : BlockMap.rotatableObjectList)
		{
			rotO.draw(g);
		}
		
		for (MoveableObject movO : BlockMap.moveableObjectList)
		{
			movO.draw(g);
		}
		
		// render player animation
		playerOne.drawAnimation();
		
		// reset graphics device
		g.setColor(Color.white);
		g.resetLineWidth();
		g.resetTransform();
		g.resetFont();
	}
 
	public static void main(String[] argv) throws SlickException {
		AppGameContainer container = 
			new AppGameContainer(new Game(), SCREEN_WIDTH, SCREEN_HEIGHT, false);
		container.start();
	}
}