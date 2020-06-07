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
	private static Color grey = Helper.GRAY;
	private static Color lightBlue = Helper.GetColor(135, 212, 245);
	private static Color darkBlue = Helper.GetColor(11, 70, 146);
	private static Color darkRed = Helper.GetColor(183, 22, 22);
	private static Color green = Helper.GetColor(103, 238, 70);
	private static Color purple = Helper.GetColor(153, 51, 255);
	
	static SoundClip backgrMusic = new SoundClip("/sounds/music/jazz.mp3", true);
	SoundClip addIngrSnd = new SoundClip("/sounds/effects/whoosh.wav", true); 
	SoundClip undoSnd = new SoundClip("/sounds/effects/beep.wav", true);
	SoundClip cannotUndoSnd = new SoundClip("/sounds/effects/click_x.wav", true);
	static boolean backMusic = true;
	
	private static GameRectangle [] container = new GameRectangle [8];
	private static GameCircle finishButton = new GameCircle (900, 350, 75, 5, red, red, 1f);
	private static GameRectangle backMusicButton = new GameRectangle (630, 375, 50, 25, 3, white, 1f);
	
	private static Font menuFont = new Font("Impact", Font.BOLD, 120);
	private static Font smallInstrFont = new Font("Bookman Old Style", Font.PLAIN, 25);
	private static Font instructionFont = new Font("Impact", Font.PLAIN, 30);
	private static Font returnToMenuFont = new Font("Times New Roman", Font.BOLD, 20);
	private static Font orderFont = new Font("Lucida Handwriting", Font.PLAIN, 20);
 
	private static Color screenColour = lightBlue;
	private static String menuText = "Menu";
	
	private static Vector2F mousePos = Input.GetMousePos();
	
	private static boolean alive;
	private static int updateTracker = 0;
	
	private static String [] orderTicketIngr = new String[10];
	private static String [] stack = new String[10];
	private static int currentLayer = 0;
	private static SpriteSheet [] buttonImgs = new SpriteSheet[8]; //stores sprite sheets for the ingredient images of 8 buttons
	private static SpriteSheet fullGameSS = new SpriteSheet(LoadImage.FromFile("/images/sprites/fullGameSS.png")); 
	
	private static int score = 0;
	private static float timer = 16000; //The time in milliseconds
	private static Integer seconds = 16; //The time in seconds
	
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
		orderTicketIngr[9] = "bun";
		
		container [0] = new GameRectangle(45, 465, 216, 228, 5, white, 1f);
		container [1] = new GameRectangle(276, 465, 216, 228, 5, white, 1f);
		container [2] = new GameRectangle(507, 465, 216, 228, 5, white, 1f);
		container [3] = new GameRectangle(738, 465, 216, 228, 5, white, 1f);
		container [4] = new GameRectangle(45, 708, 216, 228, 5, white, 1f);
		container [5] = new GameRectangle(276, 708, 216, 228, 5, white, 1f);
		container [6] = new GameRectangle(507, 708, 216, 228, 5, white, 1f);
		container [7] = new GameRectangle(738, 708, 216, 228, 5, white, 1f);
		
		String [] containerIngr = new String [] {"tomatoes", "cheese", "lettuce", "onions", "patty", "bacon", "egg", "bun"};
		int x = 55; //starting x coordinate
		int y = 465; //starting y coordinate
		//Loops through buttonImgs and draws each containerIngr
		for (int i = 0; i < buttonImgs.length; i++)
		{
			String containerPath = null;		
			
			if (containerIngr[i] == "patty"){ //Adjusts coordinates for next row
				x = 55;
				y = 708;
			}
			
			containerPath = "/images/sprites/" + containerIngr[i] + ".png";
			buttonImgs[i] = new SpriteSheet(LoadImage.FromFile("/images/sprites/" + containerIngr[i] + ".png")); //Loads the required ingredient to be drawn (path)
			buttonImgs[i].destRec = new Rectangle(x, y, (int)(gc.GetWidth() * 0.2), (int)(gc.GetHeight() * 0.2)); //Defines bounding box
			
			int xDistance = 231;
			x = x + xDistance; //Updates x coordinate
		}
	}
	
	@Override
	public void Update(GameContainer gc, float deltaTime) 
	{
		if (Input.IsKeyReleased(KeyEvent.VK_F) && alive == false)
		{
			menuText = "Manual";
			screenColour = darkRed;		
		}
		else if (Input.IsKeyReleased(KeyEvent.VK_W) && (menuText == "Manual" || menuText == "Instructions") && alive == false)
		{
			menuText = "Backstory";
			screenColour = darkBlue;
		}
		else if (Input.IsKeyReleased(KeyEvent.VK_E) && (menuText == "Manual" || menuText == "Backstory") && alive == false)
		{
			menuText = "Instructions";
			screenColour = black;
		}
		else if (Input.IsKeyReleased(KeyEvent.VK_D) && alive == false)
		{
			menuText = "Menu";
			screenColour = lightBlue;
		}
		else if (Input.IsKeyReleased(KeyEvent.VK_SPACE)&& alive == false)
		{
			menuText = "Game Play";
			screenColour = black;
			alive = true;
			
			if (backMusic == true){
				backgrMusic.Play();
			}
		}
		else if (Input.IsKeyReleased(KeyEvent.VK_S)&& alive == false)
		{
			menuText = "Settings";
			screenColour = grey;
			if (pointBoxColl(backMusicButton) == true && backMusic == true){
				backMusic = false;
				System.out.println("false");
			}
			else if (pointBoxColl(backMusicButton) == true && backMusic == false){
				backMusic = true;
				System.out.println("true");
			}
		}
		
		if (menuText == "Settings")
		{
			
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
			menuText = "Score Screen";
			screenColour = purple;
			backgrMusic.Stop();
			alive = false;
			
			calcScore();
			resetGame();
		}
		
		//Checks mouse click position
		if (alive == true && Input.IsMouseButtonReleased(Input.MOUSE_LEFT))
		{
			
			if (pointCircleColl(finishButton) || seconds == 0)
			{
				menuText = "Score Screen";
				screenColour = purple;
				backgrMusic.Stop();
				alive = false;
				
				calcScore();
				resetGame();
			}
			
			if (currentLayer < 10)
			{
				if (pointBoxColl(container[0])){
					stack[currentLayer] = "tomatoes";
					addIngrSnd.Play();
				}
				else if (pointBoxColl(container[1])){
					stack[currentLayer] = "cheese";
					addIngrSnd.Play();
				}
				else if (pointBoxColl(container[2])){
					stack[currentLayer] = "lettuce";
					addIngrSnd.Play();
				}
				else if (pointBoxColl(container[3])){
					stack[currentLayer] = "onions";
					addIngrSnd.Play();
				}
				else if (pointBoxColl(container[4])){
					stack[currentLayer] = "patty";
					addIngrSnd.Play();
				}
				else if (pointBoxColl(container[5])){
					stack[currentLayer] = "bacon";
					addIngrSnd.Play();
				}
				else if (pointBoxColl(container[6])){
					stack[currentLayer] = "egg";
					addIngrSnd.Play();
				}
				else if (pointBoxColl(container[7])){
					stack[currentLayer] = "bun";
					addIngrSnd.Play();
				}
				else 
				{
					currentLayer--;
				}
				//System.out.println("current layer: " + currentLayer);
				//System.out.println("stack[currentLayer]: " + stack[currentLayer]);
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
			stack[currentLayer] = null;
			System.out.println(currentLayer);
			System.out.println(stack[currentLayer]);
			undoSnd.Play();
		}
		else if (Input.IsKeyReleased(KeyEvent.VK_Z) && currentLayer == 0)
		{
			cannotUndoSnd.Play();
		}
	}

	@Override
	public void Draw(GameContainer gc, Graphics2D gfx) 
	{
		Draw.FillRect(gfx, 0, 0, 1000, 1000, screenColour, 1f);
		
		if (menuText == "Menu")
		{
			drawMenuScr(gfx);
		}
		else if (menuText == "Manual")
		{
			drawManualScr(gfx);
		}
		else if (menuText == "Backstory"){
			drawStoryScr(gfx);
		}
		else if (menuText == "Instructions"){
			
			fullGameSS.destRec = new Rectangle(230, 480, (int)(gc.GetWidth() * 0.5), (int)(gc.GetHeight() * 0.5)); //Defines bounding box
			Draw.Sprite(gfx, fullGameSS); 
			
			drawInstrScr(gfx);
		}
		else if (menuText == "Settings")
		{
			drawSettingsScr(gfx);
		}
		else if (menuText == "Game Play")
		{
			drawGameScr(gfx, gc);
		}
		else if (menuText == "Score Screen")
		{
			drawScoreScr(gfx);
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
				orderTicketIngr[i] = "patty";
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
	
	private static void drawMenuScr (Graphics2D gfx)
	{
		Font titleFont = new Font("Copperplate Gothic Bold", Font.BOLD, 150);
		Draw.Text(gfx, "Building", 95, 300, titleFont, white, 1f);
		Draw.Text(gfx, "Burgers", 95, 450, titleFont, white, 1f);
		
		Draw.Text(gfx, "Press F for game manual", 340, 650, instructionFont, grey, 1f);
		Draw.Text(gfx, "Press space bar for game play", 300, 700, instructionFont, purple, 1f);
		Draw.Text(gfx, "Press S for game settings", 330, 750, instructionFont, grey, 1f);
		Draw.Text(gfx, "Press D to return to menu", 333, 800, instructionFont, purple, 1f);
	}
	
	private static void drawManualScr (Graphics2D gfx)
	{
		Draw.Text(gfx, menuText, 300, 200, menuFont, white, 1f);
		Draw.Text(gfx, "Press W for the backstory", 340, 450, instructionFont, white, 1f);
		Draw.Text(gfx, "Press E for instructions on how to play", 265, 500, instructionFont, white, 1f);
		Draw.Text(gfx, "Press D to return to menu", 750, 950, returnToMenuFont, purple, 1f);
	}
	
	private static void drawStoryScr (Graphics2D gfx)
	{
		Draw.Text(gfx, menuText, 215, 200, menuFont, white, 1f);
		
		Draw.Text(gfx, "Congratulations! You have just graduated from Havard Universtiy with a", 50, 380, smallInstrFont, white, 1f);
		Draw.Text(gfx, "Ph.D in biophysics. You are confident that you have a bright, rich and ", 50, 410, smallInstrFont, white, 1f);
		Draw.Text(gfx, "luxurious future job ahead of you. You apply to all sorts of advanced, ", 50, 440, smallInstrFont, white, 1f);
		Draw.Text(gfx, "high-paying jobs. The next thing you know? BAM! You get politely rejected ", 50, 470, smallInstrFont, white, 1f);
		Draw.Text(gfx, "by all of them. After all those years of stress in school, only to come ", 50, 500, smallInstrFont, white, 1f);
		Draw.Text(gfx, "to this? \"Forget it!\" you think. You finally decide to get a job that ", 50, 530, smallInstrFont, white, 1f);
		Draw.Text(gfx, "you'd actually, truly enjoy doing. And so you apply to a local ", 50, 560, smallInstrFont, white, 1f);
		Draw.Text(gfx, "restaurant called Building Burgers. You are immediately accepted and ", 50, 590, smallInstrFont, white, 1f);
		Draw.Text(gfx, "warmly welcomed into the job. Get ready! For working conditions are ", 50, 620, smallInstrFont, white, 1f);
		Draw.Text(gfx, "not that great but you really need to pay off your student loans.", 50, 650, smallInstrFont, white, 1f);
		
		Draw.Text(gfx, "As the sun rises above the city skyscrapers, signalling the start to ", 50, 730, smallInstrFont, white, 1f);
		Draw.Text(gfx, "your first day, you know that you are ready. Because this is what", 50, 760, smallInstrFont, white, 1f);
		Draw.Text(gfx, "everything in your life has been leading up to. ", 50, 790, smallInstrFont, white, 1f);
		
		Draw.Text(gfx, "Press D to return to menu", 750, 950, returnToMenuFont, purple, 1f);
	}
	
	private static void drawInstrScr (Graphics2D gfx)
	{
		Font labelFont = new Font ("Times New Roman", Font.PLAIN, 20);
		Draw.Text(gfx, menuText, 155, 150, menuFont, white, 1f);
		
		Draw.Text(gfx, "At the start of a game, you will see a list of ingredients to be read from top ", 50, 250, smallInstrFont, white, 1f);
		Draw.Text(gfx, "to bottom. Your goal is to build a burger with ingredients that match the", 50, 280, smallInstrFont, white, 1f);
		Draw.Text(gfx, "order's. Use your time wisely, as you have limited time to finish. ", 50, 310, smallInstrFont, white, 1f);
		Draw.Text(gfx, "To add an ingredient, click on a box. To remove an ingredient, ", 50, 340, smallInstrFont, white, 1f);
		Draw.Text(gfx, "press Z on your keyboard. When you are done, press the finish button. ", 50, 370, smallInstrFont, white, 1f);
		Draw.Text(gfx, "Remember to add a bun to start and end!", 50, 400, smallInstrFont, lightBlue, 1f);
		
		Draw.Text(gfx, "Click on this box", 20, 900, labelFont, red, 1f);
		Draw.Text(gfx, "to add a patty", 20, 920, labelFont, red, 1f);
		Draw.Text(gfx, "View order here", 20, 500, labelFont, red, 1f);
		Draw.Text(gfx, "Time left (in seconds)", 800, 500, labelFont, red, 1f);
		Draw.Text(gfx, "Click here to finish", 800, 650, labelFont, red, 1f);
		
		Draw.Line(gfx, 170, 900, 270, 910, 3, red, 1f);
		Draw.Line(gfx, 160, 495, 230, 520, 3, red, 1f);
		Draw.Line(gfx, 790, 495, 720, 495, 3, red, 1f);
		Draw.Line(gfx, 790, 645, 730, 645, 3, red, 1f);
		
		Draw.Text(gfx, "Press D to return to menu", 750, 950, returnToMenuFont, purple, 1f);
	}
	
	private static void drawSettingsScr (Graphics2D gfx)
	{
		Draw.Text(gfx, menuText, 250, 200, menuFont, white, 1f);
		Draw.Text(gfx, "Background music", 300, 400, instructionFont, white, 1f);
		Draw.Text(gfx, "Press D to return to menu", 750, 950, returnToMenuFont, red, 1f);
		drawRecLine(backMusicButton, gfx); 
	}
	
	private static void drawGameScr (Graphics2D gfx, GameContainer gc)
	{ 
		if (stack[0] != null)
		{
			Draw.Text(gfx, "Press Z to undo", 50, 360, smallInstrFont, red, 1f);
		}
		
		finishButton.Draw(gfx);
		Draw.Text(gfx, "FINISH", 860, 360, instructionFont, white, 1f);
		
		Draw.Text(gfx, "Remaining time: " + Integer.toString(seconds), 730, 50, instructionFont, white, 1f); //Displays remaining time in seconds
		
		Draw.Text(gfx, "Order Ticket", 45, 60, instructionFont, lightBlue, 1f);
		drawOrder(gfx, 1, 100);
		drawOrder(gfx, 2, 125);
		drawOrder(gfx, 3, 150);
		drawOrder(gfx, 4, 175);
		drawOrder(gfx, 5, 200);
		drawOrder(gfx, 6, 225);
		drawOrder(gfx, 7, 250);
		drawOrder(gfx, 8, 275);
		
		boolean hasBottomBun = false; //checks if burger has a bottom bun
		//Draws stack 
		for (int i = 0; i < stack.length; i++)
		{
			if (stack[i] != null) //checks if stack with element i is not empty
			{
				String path = null;
				if (stack[i] == "bun") 
				{
					if (hasBottomBun == true){ //if there is a bottomBun
						path = "/images/sprites/topBun.png";
					}
					else{ //if there isn't a bottomBun
						path = "/images/sprites/bottomBun.png";
						hasBottomBun = true;							
					}
				}
				else
				{
					path = "/images/sprites/" + stack[i] + ".png"; //Loads every ingredient except for topBun and bottomBun from path
				}
				
				int y = 270 - 30*i; //subtracts 30 from y coordinate of ingredient each time
				
				SpriteSheet stackSlot = new SpriteSheet(LoadImage.FromFile(path)); //Loads the required ingredient to be drawn (path)
				stackSlot.destRec = new Rectangle(380, y, (int)(gc.GetWidth() * 0.2), (int)(gc.GetHeight() * 0.2)); //Defines bounding box
				Draw.Sprite(gfx, stackSlot); //Draws ingredient 
			}
		}
		
		//Draws ingredient images for the buttons
		for (int i = 0; i < buttonImgs.length; i++)
		{
			Draw.Sprite(gfx, buttonImgs[i]); 
		}

		Draw.Rect(gfx, 30, 450, 940, 500, 5, white, 1f);
		for (int i = 0; i < 8; i++)
		{
			GameRectangle rec = container[i];
			drawRecLine(rec, gfx);
		}
	}
	
	private static void drawScoreScr (Graphics2D gfx)
	{
		Draw.Text(gfx, "Score", 350, 200, menuFont, white, 1f);
		Draw.Text(gfx, "Your score:", 380, 600, instructionFont, white, 1f);
		Draw.Text(gfx, Integer.toString(score), 530, 600, menuFont, white, 1f);
		
		Draw.Text(gfx, "Press D to return to menu", 750, 950, returnToMenuFont, white, 1f);
		Draw.Text(gfx, "Press space bar to replay", 345, 800, instructionFont, lightBlue, 1f);
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
		timer = 16000;
		seconds = 16;
		currentLayer = 0;
		updateTracker = 0;
		for (int i = 0; i < 10; i++)
		{
			stack[i] = null;
		}
	}
}
