import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;

import com.engine.core.*;
import com.engine.core.gfx.*;

import java.util.Random;

public class Main extends AbstractGame
{
	//Required Basic Game Functional Data
	private static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	private static int screenWidth = device.getDisplayMode().getWidth();
	private static int screenHeight = device.getDisplayMode().getHeight();
	
	//Required Basic Game Visual data used in main below
	private static String gameName = "Building Burgers";
	private static int windowWidth = 1000;	//For fullscreen mode set these next two to screenWidth and screenHeight
	private static int windowHeight = 1000;
	private static int fps = 30;
	
	private static Color white = Helper.WHITE;
	private static Color black = Helper.BLACK;
	private static Color red = Helper.RED;
	private static Color lightBlue = Helper.GetColor(135, 212, 245);
	private static Color darkRed = Helper.GetColor(183, 22, 22);
	private static Color green = Helper.GetColor(103, 238, 70);
	private static Color purple = Helper.GetColor(153, 51, 255);
	
	private static GameRectangle [] container = new GameRectangle [8];
	private static GameCircle finishButton = new GameCircle (900, 350, 75, 5, red, red, 1f);
	//private static boolean scoreScreen = false;
	
	private static Font menuFont = new Font("Impact", Font.BOLD, 120);
	private static Font instructionFont = new Font("Impact", Font.PLAIN, 30);
	private static Font orderFont = new Font("Lucida Handwriting", Font.PLAIN, 20);
	
	private static SpriteSheet tomatoImg;
	private static SpriteSheet cheeseImg;
	private static SpriteSheet lettuceImg;
	private static SpriteSheet onionImg;
	private static SpriteSheet pattyImg;
	private static SpriteSheet baconImg;
	private static SpriteSheet eggImg;
	private static SpriteSheet bottomBunImg;
	private static SpriteSheet topBunImg;
	
	private static int [] stackCoord = new int [] {350, 240};
 
	private static Color screenColour = lightBlue;
	private static String menuText = "Menu";
	
	private static Vector2F mousePos = Input.GetMousePos();
	
	private static boolean alive;
	private static int updateTracker = 0;
	
	private static String [] orderTicketIngr = new String[10];
	private static String [] stack = new String[10];
	private static int currentLayer = 0;
	
	private static int score = 0;
	private static float timer = 100000; //The time in milliseconds
	private static Integer seconds = 100; //The time in seconds
	
	public static void main(String[] args) 
	{
		GameContainer gameContainer = new GameContainer(new Main(), gameName, windowWidth, windowHeight, fps);
		gameContainer.Start();
	}

	@Override
	public void LoadContent(GameContainer gc)
	{
		tomatoImg = new SpriteSheet(LoadImage.FromFile("/images/sprites/tomato.png")); 
		cheeseImg = new SpriteSheet(LoadImage.FromFile("/images/sprites/cheese.png"));
		lettuceImg = new SpriteSheet(LoadImage.FromFile("/images/sprites/lettuce.png"));
		onionImg = new SpriteSheet(LoadImage.FromFile("/images/sprites/onion.png"));
		pattyImg = new SpriteSheet(LoadImage.FromFile("/images/sprites/patty.png"));
		baconImg = new SpriteSheet(LoadImage.FromFile("/images/sprites/bacon.png"));
		eggImg = new SpriteSheet(LoadImage.FromFile("/images/sprites/egg.png"));
		bottomBunImg = new SpriteSheet(LoadImage.FromFile("/images/sprites/bottomBun.png"));
		topBunImg = new SpriteSheet(LoadImage.FromFile("/images/sprites/topBun.png"));
		
		tomatoImg.destRec = new Rectangle(stackCoord[0], stackCoord[1], (int)(gc.GetWidth() * 0.25), (int)(gc.GetHeight() * 0.25));
		cheeseImg.destRec = new Rectangle(stackCoord[0], stackCoord[1], (int)(gc.GetWidth() * 0.25), (int)(gc.GetHeight() * 0.25));
		lettuceImg.destRec = new Rectangle(stackCoord[0], stackCoord[1], (int)(gc.GetWidth() * 0.25), (int)(gc.GetHeight() * 0.25));
		onionImg.destRec = new Rectangle(stackCoord[0], stackCoord[1], (int)(gc.GetWidth() * 0.25), (int)(gc.GetHeight() * 0.25));
		pattyImg.destRec = new Rectangle(stackCoord[0], stackCoord[1], (int)(gc.GetWidth() * 0.25), (int)(gc.GetHeight() * 0.25));
		baconImg.destRec = new Rectangle(stackCoord[0], stackCoord[1], (int)(gc.GetWidth() * 0.25), (int)(gc.GetHeight() * 0.25));
		eggImg.destRec = new Rectangle(stackCoord[0], stackCoord[1], (int)(gc.GetWidth() * 0.25), (int)(gc.GetHeight() * 0.25));
		bottomBunImg.destRec = new Rectangle(stackCoord[0], stackCoord[1], (int)(gc.GetWidth() * 0.25), (int)(gc.GetHeight() * 0.25));
		topBunImg.destRec = new Rectangle(stackCoord[0], stackCoord[1], (int)(gc.GetWidth() * 0.25), (int)(gc.GetHeight() * 0.25));
		
		
		//System.out.println("\n\norderTicketIngr: " + orderTicketIngr[0] + " " + orderTicketIngr[1] + " " + orderTicketIngr[2] + " " + orderTicketIngr[3] + " " + orderTicketIngr[4] + " " + orderTicketIngr[5] + " " + orderTicketIngr[6] + " " + orderTicketIngr[7]);
		orderTicketIngr[0] = "bun";
		orderTicketIngr[9] = "bun";
		
		container [0] = new GameRectangle(45, 465, 216, 228, 5, green, black, 1f);
		container [1] = new GameRectangle(276, 465, 216, 228, 5, white, black, 1f);
		container [2] = new GameRectangle(507, 465, 216, 228, 5, white, black, 1f);
		container [3] = new GameRectangle(738, 465, 216, 228, 5, white, black, 1f);
		container [4] = new GameRectangle(45, 708, 216, 228, 5, lightBlue, black, 1f);
		container [5] = new GameRectangle(276, 708, 216, 228, 5, white, black, 1f);
		container [6] = new GameRectangle(507, 708, 216, 228, 5, white, black, 1f);
		container [7] = new GameRectangle(738, 708, 216, 228, 5, red, black, 1f);
	}
	
	@Override
	public void Update(GameContainer gc, float deltaTime) 
	{
		if (Input.IsKeyReleased(KeyEvent.VK_I) && alive == false)
		{
			screenColour = darkRed;
			menuText = "Instructions";
		}
		else if (Input.IsKeyReleased(KeyEvent.VK_G))
		{
			menuText = "Game Play";
			screenColour = black;
			alive = true;
		}
		else if (Input.IsKeyReleased(KeyEvent.VK_SPACE)&& alive == false)
		{
			screenColour = lightBlue;
			menuText = "Menu";
		}
		
		//Generates ingredients when user starts game
		if (alive == true && updateTracker == 0)
		{
			genIngredients();
			updateTracker = 1;
		}
		
		if (seconds > 0 && alive == true) //Checks if seconds is more than 0
		{
			timer = timer - deltaTime; //Calculates the time in milliseconds
			seconds = Math.round(timer/1000); //Converts the time in milliseconds to seconds
		}
		
		if (seconds == 0 && updateTracker == 1)
		{
			alive = false;
			menuText = "Score Screen";
			screenColour = purple;
			
			calcScore();
			resetGame();
		}
		
		//Checks mouse click position
		if (alive == true && Input.IsMouseButtonReleased(Input.MOUSE_LEFT))
		{
			
			if (pointCircleColl(finishButton) || seconds == 0)
			{
				alive = false;
				menuText = "Score Screen";
				screenColour = purple;
				
				calcScore();
				resetGame();
			}
			
			if (currentLayer < 10 && !(pointCircleColl(finishButton)))
			{
				if (pointBoxColl(container[0])){
					stack[currentLayer] = "tomatoes";
				}
				else if (pointBoxColl(container[1])){
					stack[currentLayer] = "cheese";
				}
				else if (pointBoxColl(container[2])){
					stack[currentLayer] = "lettuce";
				}
				else if (pointBoxColl(container[3])){
					stack[currentLayer] = "onions";
				}
				else if (pointBoxColl(container[4])){
					stack[currentLayer] = "beef patty";
				}
				else if (pointBoxColl(container[5])){
					stack[currentLayer] = "bacon";
				}
				else if (pointBoxColl(container[6])){
					stack[currentLayer] = "egg";
				}
				else if (pointBoxColl(container[7])){
					stack[currentLayer] = "bun";
				}
				else 
				{
					currentLayer--;
				}
				System.out.println("current layer: " + currentLayer);
				System.out.println("stack[currentLayer]: " + stack[currentLayer]);
				currentLayer++;
			}
			else if (currentLayer >= 10 && alive == true) 
			{
				System.out.println("You can not stack more ingredients");
			}
		}
		
		if (Input.IsKeyReleased(KeyEvent.VK_Z) && currentLayer > 0)
		{
			currentLayer--;
			stack[currentLayer] = "";
			System.out.println(currentLayer);
			System.out.println(stack[currentLayer]);
		}
		else if (Input.IsKeyReleased(KeyEvent.VK_Z) && currentLayer == 0)
		{
			System.out.println("You can not remove an ingredient that doesn't exist");
		}
	}

	@Override
	public void Draw(GameContainer gc, Graphics2D gfx) 
	{
		Draw.FillRect(gfx, 0, 0, 1000, 1000, screenColour, 1f);
		
		if (menuText == "Menu")
		{
			Draw.Text(gfx, menuText, 340, 520, menuFont, white, 1f);
			
			Draw.Text(gfx, "Press I for instructions", 333, 700, instructionFont, white, 1f);
			Draw.Text(gfx, "Press G for game play", 345, 750, instructionFont, white, 1f);
			Draw.Text(gfx, "Press space bar to return to menu", 270, 800, instructionFont, white, 1f);
		}
		else if (menuText == "Instructions")
		{
			Draw.Text(gfx, menuText, 150, 200, menuFont, white, 1f);
			
			Draw.Text(gfx, "Are you ready to take on the challenge?", 200, 400, instructionFont, white, 1f);
		}
		else if (menuText == "Game Play")
		{
			finishButton.Draw(gfx);
			Draw.Text(gfx, "FINISH", 860, 360, instructionFont, white, 1f);
			
			drawOrder(gfx, 1, 100);
			drawOrder(gfx, 2, 125);
			drawOrder(gfx, 3, 150);
			drawOrder(gfx, 4, 175);
			drawOrder(gfx, 5, 200);
			drawOrder(gfx, 6, 225);
			drawOrder(gfx, 7, 250);
			drawOrder(gfx, 8, 275);
			
			Draw.Rect(gfx, 30, 450, 940, 500, 5, red, 1f);
			Draw.Text(gfx, "Remaining time: " + Integer.toString(seconds), 730, 50, instructionFont, white, 1f); //Displays remaining time in seconds
			
			Draw.Sprite(gfx, tomatoImg);
			
			for (int i = 0; i < 8; i++)
			{
				GameRectangle rec = container[i];
				drawRecLine(rec, gfx);
			}
			
		}
		else if (menuText == "Score Screen")
		{
			Draw.Text(gfx, "Score", 150, 200, menuFont, white, 1f);
			Draw.Text(gfx, "Your score:", 150, 450, instructionFont, white, 1f);
			Draw.Text(gfx, Integer.toString(score), 300, 450, menuFont, white, 1f);
		}
	}
	
	static Random rng = new Random();
	private static void genIngredients ()
	{
		int rangeLow = 1; 
		int rangeHigh = 8;
		int ingredientNum;
		
		for (int i = 1; i <= 8; i++)
		{
			ingredientNum = (int)((rng.nextFloat() * (rangeHigh - rangeLow)) + rangeLow);
			
			if (ingredientNum == 1)
			{
				orderTicketIngr[i] = "tomatoes";
			}
			else if (ingredientNum == 2)
			{
				orderTicketIngr[i] = "cheese";
			}
			else if (ingredientNum == 3)
			{
				orderTicketIngr[i] = "lettuce";
			}
			else if (ingredientNum == 4)
			{
				orderTicketIngr[i] = "onions";
			}
			else if (ingredientNum == 5)
			{
				orderTicketIngr[i] = "beef patty";
			}
			else if (ingredientNum == 6)
			{
				orderTicketIngr[i] = "bacon";
			}
			else if (ingredientNum == 7)
			{
				orderTicketIngr[i] = "egg";
			}
		}
		System.out.println("ingredients: " + orderTicketIngr[0] + " " + orderTicketIngr[1] + " " + orderTicketIngr[2] + " " + orderTicketIngr[3] + " " + orderTicketIngr[4] + " " + orderTicketIngr[5] + " " + orderTicketIngr[6] + " " + orderTicketIngr[7] + " " + orderTicketIngr[8]+ " " + orderTicketIngr[9]);
	}
	
	private static void drawOrder (Graphics2D gfx, int indexNum, int yCoord)
	{
		Draw.Text(gfx, orderTicketIngr[indexNum], 50, yCoord, orderFont, white, 1f);
	}
	
	private static boolean pointBoxColl (GameRectangle box)
	{
		if (mousePos.x >= box.GetLeft() && mousePos.x <= box.GetRight() &&	//Left/Right Walls 
			mousePos.y >= box.GetTop() && mousePos.y <= box.GetBottom())	//Top/Bottom Walls
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private static void drawRecLine (GameRectangle rec, Graphics2D gfx)
	{
		float height = rec.GetBottom() - rec.GetTop();
		float width = rec.GetRight() - rec.GetLeft();
		Draw.Rect(gfx, rec.GetLeft(), rec.GetTop(), width, height, rec.GetBorderWidth(), rec.GetBorderColor(), rec.GetTransparency());
		
	}
	
	private static boolean pointCircleColl (GameCircle circle)
	{
		//Calculate the distance squared using pythagorean theorem between the point and
		//the circle's centre
		double distanceSqr = Math.pow(mousePos.x - circle.GetCentre().x, 2) + Math.pow(mousePos.y - circle.GetCentre().y, 2);
		
		//If that distance is within range of the radius then there is a collision
		if (distanceSqr <= Math.pow(circle.GetRad(), 2))	//Uses the shortcut method to avoid square roots
		{
			return true;
		}
		
		return false;
	}
	
	private static void calcScore ()
	{
		double subtotal = 0;
		
		for (int i = 0; i < 10; i++)
		{
			if (orderTicketIngr[i] == stack[i])
			{
				subtotal++;
			}
		}
		
		subtotal = (subtotal / 10) * 100;
		score = (int)subtotal;
		System.out.println("score: " + score);
	}
	
	private static void resetGame ()
	{
		timer = 100000;
		seconds = 100;
		currentLayer = 0;
		updateTracker = 0;
		for (int i = 0; i < 10; i++)
		{
			stack[i] = null;
		}
	}
}
