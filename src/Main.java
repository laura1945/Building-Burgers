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
	private static Color blue = Helper.BLUE;
	private static Color lightBlue = Helper.GetColor(135, 212, 245);
	private static Color darkRed = Helper.GetColor(183, 22, 22);
	private static Color green = Helper.GetColor(103, 238, 70);
	
	private static Font menuFont = new Font("Impact", Font.BOLD, 120);
	private static Font instructionFont = new Font("Impact", Font.PLAIN, 30);
	private static Font orderFont = new Font("Lucida Handwriting", Font.PLAIN, 20);

	private static Color screenColour = lightBlue;
	private static String menuText = "Menu";
	//private static int [] menuTxtCoord = new int[]{340, 520};
	
	private static boolean alive;
	private static int timesGen = 0;
	
	private static String [] orderTicketIngr = new String[8];
	
	public static void main(String[] args) 
	{
		GameContainer gameContainer = new GameContainer(new Main(), gameName, windowWidth, windowHeight, fps);
		gameContainer.Start();
	}

	@Override
	public void LoadContent(GameContainer gc)
	{
		//System.out.println("\n\norderTicketIngr: " + orderTicketIngr[0] + " " + orderTicketIngr[1] + " " + orderTicketIngr[2] + " " + orderTicketIngr[3] + " " + orderTicketIngr[4] + " " + orderTicketIngr[5] + " " + orderTicketIngr[6] + " " + orderTicketIngr[7]);
		
	}
	
	@Override
	public void Update(GameContainer gc, float deltaTime) 
	{
		if (Input.IsKeyReleased(KeyEvent.VK_I) && alive == false)
		{
			screenColour = darkRed;
			menuText = "Instructions";
			//menuTxtCoord[0] = 150;
			//menuTxtCoord[1] = 200;
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
			//menuTxtCoord[0] = 340;
			//menuTxtCoord[1] = 520;
		}
		
		if (alive == true && timesGen == 0)
		{
			genIngredients();
			timesGen = 1;
		}

	}

	@Override
	public void Draw(GameContainer gc, Graphics2D gfx) 
	{
		Draw.FillRect(gfx, 0, 0, 1000, 1000, screenColour, 1f);
		
		if (!(menuText == "Game Play"))
		{
			//Draw.Text(gfx, menuText, menuTxtCoord[0], menuTxtCoord[1], menuFont, white, 1f);
		}
		
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
			drawOrder(gfx, 0, 100);
			drawOrder(gfx, 1, 125);
			drawOrder(gfx, 2, 150);
			drawOrder(gfx, 3, 175);
			drawOrder(gfx, 4, 200);
			drawOrder(gfx, 5, 225);
			drawOrder(gfx, 6, 250);
			drawOrder(gfx, 7, 275);
			
			Draw.Rect(gfx, 30, 600, 940, 300, 5, white, 1f);
			
		}
	}
	
	static Random rng = new Random();
	private static void genIngredients ()
	{
		int rangeLow = 1; 
		int rangeHigh = 8;
		int ingredientNum;
		
		for (int i = 0; i < 8; i++)
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
				orderTicketIngr[i] = "hot sauce";
			}
			else if (ingredientNum == 8)
			{
				orderTicketIngr[i] = "sour cream";
			}
		}
		System.out.println("ingredients: " + orderTicketIngr[0] + " " + orderTicketIngr[1] + " " + orderTicketIngr[2] + " " + orderTicketIngr[3] + " " + orderTicketIngr[4] + " " + orderTicketIngr[5] + " " + orderTicketIngr[6] + " " + orderTicketIngr[7]);
	}
	
	private static void drawOrder (Graphics2D gfx, int indexNum, int yCoord)
	{
		Draw.Text(gfx, orderTicketIngr[indexNum], 50, yCoord, orderFont, white, 1f);
	}

}
