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
	
	private static GameRectangle [] container = new GameRectangle [8];
	private static GameRectangle finishButton = new GameRectangle (738, 400, 200, 150, 1, green, red, 1f);
	
	private static Font menuFont = new Font("Impact", Font.BOLD, 120);
	private static Font instructionFont = new Font("Impact", Font.PLAIN, 30);
	private static Font orderFont = new Font("Lucida Handwriting", Font.PLAIN, 20);

	private static Color screenColour = lightBlue;
	private static String menuText = "Menu";
	
	private static Vector2F mousePos = Input.GetMousePos();
	
	private static boolean alive;
	private static int timesGen = 0;
	
	private static String [] orderTicketIngr = new String[9];
	private static String [] stack = new String[9];
	private static int currentLayer = 0;
	
	public static void main(String[] args) 
	{
		GameContainer gameContainer = new GameContainer(new Main(), gameName, windowWidth, windowHeight, fps);
		gameContainer.Start();
	}

	@Override
	public void LoadContent(GameContainer gc)
	{
		//System.out.println("\n\norderTicketIngr: " + orderTicketIngr[0] + " " + orderTicketIngr[1] + " " + orderTicketIngr[2] + " " + orderTicketIngr[3] + " " + orderTicketIngr[4] + " " + orderTicketIngr[5] + " " + orderTicketIngr[6] + " " + orderTicketIngr[7]);
		orderTicketIngr[0] = "bun";
		orderTicketIngr[8] = "bun";
		
		container [0] = new GameRectangle(45, 465, 216, 228, 5, white, 1f);
		container [1] = new GameRectangle(276, 465, 216, 228, 5, white, 1f);
		container [2] = new GameRectangle(507, 465, 216, 228, 5, white, 1f);
		container [3] = new GameRectangle(738, 465, 216, 228, 5, white, 1f);
		container [4] = new GameRectangle(45, 708, 216, 228, 5, white, 1f);
		container [5] = new GameRectangle(276, 708, 216, 228, 5, white, 1f);
		container [6] = new GameRectangle(507, 708, 216, 228, 5, white, 1f);
		container [7] = new GameRectangle(738, 708, 216, 228, 5, white, 1f);
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
		if (alive == true && timesGen == 0)
		{
			genIngredients();
			timesGen = 1;
		}
		
		//Checks mouse click position
		if (alive == true && Input.IsMouseButtonReleased(Input.MOUSE_LEFT))
		{
			if (currentLayer < 9)
			{
				if (clickIngredient(container[0])){
					stack[currentLayer] = "tomatoes";
				}
				else if (clickIngredient(container[1])){
					stack[currentLayer] = "cheese";
				}
				else if (clickIngredient(container[2])){
					stack[currentLayer] = "lettuce";
				}
				else if (clickIngredient(container[3])){
					stack[currentLayer] = "onions";
				}
				else if (clickIngredient(container[4])){
					stack[currentLayer] = "beef patty";
				}
				else if (clickIngredient(container[5])){
					stack[currentLayer] = "bacon";
				}
				else if (clickIngredient(container[6])){
					stack[currentLayer] = "egg";
				}
				else if (clickIngredient(container[7])){
					stack[currentLayer] = "bun";
				}
				System.out.println(stack[currentLayer]);
				System.out.println(currentLayer);
				currentLayer++;
				System.out.println(currentLayer);
			}
			else
			{
				System.out.println("You can not stack more ingredients");
			}
		}
		
		if (Input.IsKeyReleased(KeyEvent.VK_Z) && currentLayer > 0)
		{
			currentLayer--;
			System.out.println(currentLayer);
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
			Draw.Text(gfx, "Menu", 340, 520, menuFont, white, 1f);
			
			Draw.Text(gfx, "Press I for instructions", 333, 700, instructionFont, white, 1f);
			Draw.Text(gfx, "Press G for game play", 345, 750, instructionFont, white, 1f);
			Draw.Text(gfx, "Press space bar to return to menu", 270, 800, instructionFont, white, 1f);
		}
		else if (menuText == "Instructions")
		{
			Draw.Text(gfx, "Instructions", 150, 200, menuFont, white, 1f);
			
			Draw.Text(gfx, "Are you ready to take on the challenge?", 200, 400, instructionFont, white, 1f);
		}
		else if (menuText == "Game Play")
		{
			finishButton.Draw(gfx);
			
			drawOrder(gfx, 1, 100);
			drawOrder(gfx, 2, 125);
			drawOrder(gfx, 3, 150);
			drawOrder(gfx, 4, 175);
			drawOrder(gfx, 5, 200);
			drawOrder(gfx, 6, 225);
			drawOrder(gfx, 7, 250);
			
			Draw.Rect(gfx, 30, 450, 940, 500, 5, red, 1f);
			
			for (int i = 0; i < 8; i++)
			{
				container[i].Draw(gfx);
			}
			
		}
	}
	
	static Random rng = new Random();
	private static void genIngredients ()
	{
		int rangeLow = 1; 
		int rangeHigh = 8;
		int ingredientNum;
		
		for (int i = 1; i <= 7; i++)
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
		System.out.println("ingredients: " + orderTicketIngr[0] + " " + orderTicketIngr[1] + " " + orderTicketIngr[2] + " " + orderTicketIngr[3] + " " + orderTicketIngr[4] + " " + orderTicketIngr[5] + " " + orderTicketIngr[6] + " " + orderTicketIngr[7] + " " + orderTicketIngr[8]);
	}
	
	private static void drawOrder (Graphics2D gfx, int indexNum, int yCoord)
	{
		Draw.Text(gfx, orderTicketIngr[indexNum], 50, yCoord, orderFont, white, 1f);
	}
	
	private static boolean clickIngredient (GameRectangle box)
	{
		if (mousePos.x >= box.GetLeft() && mousePos.x <= box.GetRight() &&	//Left/Right Walls 
			mousePos.y >= box.GetTop() && mousePos.y <= box.GetBottom())	//Top/Bottom Walls
		{
			//int [] rect = new int [] {(int)box.GetLeft(), (int)box.GetTop()};
			
			return true;
		}
		else
		{
			return false;
		}
	}
}
