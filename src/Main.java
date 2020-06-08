/* 
Author: Laura Zhan
File Name: Main.java
Project Name: Building Burgers
Creation Date: May 25, 2020
Modified Date: June 8, 2020
Description: This program is a game where a player makes burgers 
*/
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
	
	//Colours
	static Color white = Helper.WHITE;
	static Color black = Helper.BLACK;
	static Color red = Helper.RED;
	static Color grey = Helper.GRAY;
	static Color lightBlue = Helper.GetColor(135, 212, 245);
	static Color darkPurple = Helper.GetColor(76, 26, 127);
	static Color green = Helper.GetColor(103, 238, 70);
	static Color purple = Helper.GetColor(153, 51, 255);
	static Color peach = Helper.GetColor(245, 194, 128);
	
	//Music and sound effects
	static SoundClip backgrMusic = new SoundClip("/sounds/music/jazz.mp3", true);
	SoundClip addIngrSnd = new SoundClip("/sounds/effects/whoosh.wav", true); 
	SoundClip undoSnd = new SoundClip("/sounds/effects/waterDrop.wav", true);
	SoundClip cannotUndoSnd = new SoundClip("/sounds/effects/click_x.wav", true);
	static SoundClip score0to20Snd = new SoundClip("/sounds/effects/crying.wav", true);
	static SoundClip score80PlusSnd = new SoundClip("/sounds/effects/applause.wav", true);
	static SoundClip score100Snd = new SoundClip("/sounds/effects/trumpet.wav", true);
	
	//Keeps track of user's sound and music settings
	static boolean backMusic = true;
	static boolean soundEffects = true;
	
	static GameRectangle [] container = new GameRectangle [8]; //Box outlines of ingredient buttons
	static GameCircle finishButton = new GameCircle (900, 350, 75, 5, red, red, 1f); //Finish button
	
	//Buttons in settings
	static GameRectangle backMusicButton = new GameRectangle (630, 375, 50, 25, 3, white, 1f); 
	static GameRectangle soundEffButton = new GameRectangle (630, 455, 50, 25, 3, white, 1f);
	static GameRectangle sec25Button = new GameRectangle (630, 625, 25, 25, 3, white, 1f);
	static GameRectangle sec20Button = new GameRectangle (630, 675, 25, 25, 3, white, 1f);
	static GameRectangle sec15Button = new GameRectangle (630, 725, 25, 25, 3, white, 1f);
	static GameRectangle sec10Button = new GameRectangle (630, 775, 25, 25, 3, white, 1f);
	static GameRectangle sec5Button = new GameRectangle (630, 825, 25, 25, 3, white, 1f);
	static float secButtonMarkY = sec15Button.GetTop() + 13; //y coordinate of green dot that indicates which time player chose
	
	//Fonts
	static Font menuFont = new Font("Impact", Font.BOLD, 120);
	static Font smallInstrFont = new Font("Bookman Old Style", Font.PLAIN, 25);
	static Font instructionFont = new Font("Impact", Font.PLAIN, 30);
	static Font navigationFont = new Font("Times New Roman", Font.BOLD, 20);
	static Font orderFont = new Font("Lucida Handwriting", Font.PLAIN, 20);
	static Font cantAddMoreFont = new Font("Impact", Font.PLAIN, 20);
	
	static String cantAddMore = ""; //Stores String of "You can not add more ingredients" when required
 
	//Default screen basic graphics
	static Color screenColour = lightBlue;
	static String menuText = "Menu";
	
	static Vector2F mousePos = Input.GetMousePos(); //Stores mouse position
	
	static boolean alive; //Keeps track of player's alive or dead state
	
	static String [] orderTicketIngr = new String[10]; //Order ticket (list of ingredients)
	static String [] stack = new String[10]; //Keeps track of the player's burger
	static int currentLayer = 0; //Keeps track of current layer of player's burger
	static SpriteSheet [] ingredientImgs = new SpriteSheet[9]; //Ingredient images for player's burger
	static SpriteSheet [] stackImgs = new SpriteSheet[10]; //Stores player's burger's ingredient images
	static SpriteSheet [] buttonImgs = new SpriteSheet[8]; //stores sprite sheets for the ingredient images of 8 buttons
	static SpriteSheet fullGameSS = new SpriteSheet(LoadImage.FromFile("/images/sprites/fullGameSS.png")); //Screenshot of game 
	
	static int score = 0; //Player's score out of 100
	static float setMilisec = 15000; //Stores player's choice of time, in miliseconds
	static int setSec = 15; //Stores player's choice of time in seconds
	static float timer = 15000; //The time in milliseconds
	static Integer seconds = 15; //The time in seconds
	
	public static void main(String[] args) 
	{
		GameContainer gameContainer = new GameContainer(new Main(), gameName, windowWidth, windowHeight, fps);
		gameContainer.Start();
	}

	@Override
	public void LoadContent(GameContainer gc)
	{
		//Not displayed to player, sets first and last elements to bun
		orderTicketIngr[0] = "bun";
		orderTicketIngr[9] = "bun";
		
		//Loads all ingredientImgs
		ingredientImgs[0] = new SpriteSheet(LoadImage.FromFile("/images/sprites/tomatoes.png"));
		ingredientImgs[1] = new SpriteSheet(LoadImage.FromFile("/images/sprites/cheese.png"));
		ingredientImgs[2] = new SpriteSheet(LoadImage.FromFile("/images/sprites/lettuce.png"));
		ingredientImgs[3] = new SpriteSheet(LoadImage.FromFile("/images/sprites/onions.png"));
		ingredientImgs[4] = new SpriteSheet(LoadImage.FromFile("/images/sprites/patty.png"));
		ingredientImgs[5] = new SpriteSheet(LoadImage.FromFile("/images/sprites/bacon.png"));
		ingredientImgs[6] = new SpriteSheet(LoadImage.FromFile("/images/sprites/egg.png"));
		ingredientImgs[7] = new SpriteSheet(LoadImage.FromFile("/images/sprites/bottomBun.png"));
		ingredientImgs[8] = new SpriteSheet(LoadImage.FromFile("/images/sprites/topBun.png"));
		
		//Sets GameRectangles for each container element
		container [0] = new GameRectangle(45, 465, 216, 228, 5, white, 1f);
		container [1] = new GameRectangle(276, 465, 216, 228, 5, white, 1f);
		container [2] = new GameRectangle(507, 465, 216, 228, 5, white, 1f);
		container [3] = new GameRectangle(738, 465, 216, 228, 5, white, 1f);
		container [4] = new GameRectangle(45, 708, 216, 228, 5, white, 1f);
		container [5] = new GameRectangle(276, 708, 216, 228, 5, white, 1f);
		container [6] = new GameRectangle(507, 708, 216, 228, 5, white, 1f);
		container [7] = new GameRectangle(738, 708, 216, 228, 5, white, 1f);
		
		String [] containerIngr = new String [] {"tomatoes", "cheese", "lettuce", "onions", "patty", "bacon", "egg", "bun"}; //Used to load an image for buttonImgs
		int x = 55; //starting x coordinate
		int y = 465; //starting y coordinate
		//Loops through buttonImgs and draws each containerIngr
		for (int i = 0; i < buttonImgs.length; i++)
		{
			String containerPath = null; //Stores image's file location		
			
			if (containerIngr[i] == "patty"){ //Adjusts coordinates for next row
				x = 55;
				y = 708;
			}
			
			containerPath = "/images/sprites/" + containerIngr[i] + ".png";
			buttonImgs[i] = new SpriteSheet(LoadImage.FromFile(containerPath)); //Loads the required ingredient to be drawn 
			buttonImgs[i].destRec = new Rectangle(x, y, (int)(gc.GetWidth() * 0.2), (int)(gc.GetHeight() * 0.2)); //Defines bounding box
			
			int xDistance = 231; //horizontal distance between each buttonImg
			x = x + xDistance; //Updates x coordinate
		}
	}
	
	@Override
	public void Update(GameContainer gc, float deltaTime) 
	{
		//If game state is in Game Play
		if (menuText == "Game Play")
		{ 
			stopScoreSnds(score0to20Snd, score80PlusSnd, score100Snd); //Stops sound effects from previous play
			
			//Timer
			if (seconds > 0) //Checks if seconds is more than 0
			{
				timer = timer - deltaTime; //Calculates the time in milliseconds
				seconds = Math.round(timer/1000); //Converts the time in milliseconds to seconds
			}
			
			if (seconds == 0) //If time runs out, end game
			{
				endGame();
			}
			
			//Checks if left button of mouse clicked
			if (Input.IsMouseButtonReleased(Input.MOUSE_LEFT))
			{
				//If user clicks finishButton, end game
				if (pointCircleColl(finishButton))
				{
					endGame();
				}
				
				//Keeps track of what ingredients user is adding 
				if (currentLayer < 10)
				{
					for (int i = 0; i < container.length; i++)
					{
						if (pointBoxColl(container[i])){
							assignIngrToStack(i);
							currentLayer++;
							
							//Plays sound effect for adding ingredients if sound effects are on in settings
							if (soundEffects == true){
								addIngrSnd.Play();
							}
							break;
						}
					}
				} 
				else
				{
					cantAddMore = "You can not stack more ingredients";
				}
			}
			
			//Changes burger state when user removes a layer
			if (Input.IsKeyReleased(KeyEvent.VK_Z) && currentLayer > 0)
			{
				currentLayer--;
				stack[currentLayer] = null;
				stackImgs[currentLayer] = null;
				cantAddMore = ""; //Turns off "You can not add more ingredients" message
				
				if (soundEffects == true){
				undoSnd.Play();
				}
			}
			//Plays sound effect for when there's no layers to remove
			else if (Input.IsKeyReleased(KeyEvent.VK_Z) && currentLayer == 0)
			{
				if (soundEffects == true){
					cannotUndoSnd.Play();
					}
			}
		}
		//if the state is not Game Play
		else 
		{
			//Returns to menu screen from any other screen and sets screen colour
			if (Input.IsKeyReleased(KeyEvent.VK_D))
			{
				menuText = "Menu";
				screenColour = lightBlue;
			}
			
			//Dictates which screens user can switch to depending on which screen they are currently in and sets screen colour
			if (menuText == "Menu")
			{
				stopScoreSnds(score0to20Snd, score80PlusSnd, score100Snd);
				
				if (Input.IsKeyReleased(KeyEvent.VK_F))
				{
					menuText = "Manual";	
					screenColour = peach;
				}
				else if (Input.IsKeyReleased(KeyEvent.VK_SPACE))
				{
					pressedSpace();
				}
				else if (Input.IsKeyReleased(KeyEvent.VK_S))
				{
					menuText = "Settings";
					screenColour = grey;
				}
			}
			else if (menuText == "Manual")
			{
				if (Input.IsKeyReleased(KeyEvent.VK_W))
				{
					menuText = "Backstory";
					screenColour = darkPurple;
				}
				else if (Input.IsKeyReleased(KeyEvent.VK_E))
				{
					menuText = "Instructions";
					screenColour = black;
				}
			}
			else if (menuText == "Backstory")
			{
				if (Input.IsKeyReleased(KeyEvent.VK_E))
				{
					menuText = "Instructions";
					screenColour = black;
				}
				else if (Input.IsKeyReleased(KeyEvent.VK_F))
				{
					menuText = "Manual";	
					screenColour = peach;
				}
			}
			else if (menuText == "Instructions")
			{
				if (Input.IsKeyReleased(KeyEvent.VK_F))
				{
					menuText = "Manual";	
					screenColour = peach;
				}
				else if (Input.IsKeyReleased(KeyEvent.VK_W))
				{
					menuText = "Backstory";
					screenColour = darkPurple;
				}
			}
			else if (menuText == "Settings")
			{
				//Checks if user clicked on a button in settings, updates setting
				if (Input.IsMouseButtonReleased(Input.MOUSE_LEFT))
				{
					if (pointBoxColl(backMusicButton) && backMusic == true){
						backMusic = false;
					}
					else if (pointBoxColl(backMusicButton) && backMusic == false){
						backMusic = true;
					}
					
					if (pointBoxColl(soundEffButton) && soundEffects == true){
						soundEffects = false;
					}
					else if (pointBoxColl(soundEffButton) && soundEffects == false){
						soundEffects = true;
					}
					
					if (pointBoxColl(sec25Button)){
						setTimer(25000, 25000, 25, 25, sec25Button);
					}
					else if (pointBoxColl(sec20Button)){
						setTimer(20000, 20000, 20, 20, sec20Button);
					}
					else if (pointBoxColl(sec15Button)){
						setTimer(15000, 15000, 15, 15, sec15Button);
					}
					else if (pointBoxColl(sec10Button)){
						setTimer(10000, 10000, 10, 10, sec10Button);
					}
					else if (pointBoxColl(sec5Button)){
						setTimer(5000, 5000, 5, 5, sec5Button);
					}
				}
			}
			else if (menuText == "Score Screen")
			{
				if (Input.IsKeyReleased(KeyEvent.VK_SPACE))
				{
					pressedSpace();
				}
			}
		}
	}

	@Override
	public void Draw(GameContainer gc, Graphics2D gfx) 
	{
		Draw.FillRect(gfx, 0, 0, 1000, 1000, screenColour, 1f); //Draws screen colour
		
		//Draws screen details 
		if (menuText == "Menu")
		{
			drawMenuScr(gfx);
		}
		else if (menuText == "Manual")
		{
			drawManualScr(gfx);
		}
		else if (menuText == "Backstory")
		{
			drawStoryScr(gfx);
		}
		else if (menuText == "Instructions")
		{
			//Draws game screenshot
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
	//Pre: None
	//Post: None
	//Desc: Randomly generates order ticket ingredients
	private static void genIngredients ()
	{
		int rangeLow = 1; 
		int rangeHigh = 8;
		int ingredientNum;
		
		//Generates 8 random ingredients
		for (int i = 1; i <= 8; i++)
		{
			ingredientNum = (int)((rng.nextFloat() * (rangeHigh - rangeLow)) + rangeLow);
			
			//Assigns an ingredient based on ingredientNum
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
	}
	
	//Pre: gfx is needed for Draw.Text, indexNum and yCoord are used to draw the ingredient
	//Post: None
	//Desc: Draws list of orderTicketIngr
	private static void drawOrder (Graphics2D gfx, int indexNum, int yCoord)
	{
		Draw.Text(gfx, orderTicketIngr[indexNum], 50, yCoord, orderFont, white, 1f);
	}
	
	//Pre: box is required to compare its coordinates to mousePos
	//Post: returns true if there's a collision, false otherwise
	//Desc: Detects collision between a GameRectangle and mouse click
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
	
	//Pre: rec and gfx required to draw the GameRectangle
	//Post: None
	//Desc: Draws GameRectangle (outline only)
	private static void drawRecLine (GameRectangle rec, Graphics2D gfx)
	{
		float height = rec.GetBottom() - rec.GetTop();
		float width = rec.GetRight() - rec.GetLeft();
		Draw.Rect(gfx, rec.GetLeft(), rec.GetTop(), width, height, rec.GetBorderWidth(), rec.GetBorderColor(), rec.GetTransparency());
		
	}
	
	//Pre: gfx is required for drawing
	//Post: None
	//Desc: Draws menu screen
	private static void drawMenuScr (Graphics2D gfx)
	{
		Font titleFont = new Font("Copperplate Gothic Bold", Font.BOLD, 150);
		Draw.Text(gfx, "Building", 95, 300, titleFont, white, 1f);
		Draw.Text(gfx, "Burgers", 95, 450, titleFont, white, 1f);
		
		Draw.Text(gfx, "Press F for game manual", 340, 650, instructionFont, grey, 1f);
		Draw.Text(gfx, "Press space bar to play", 345, 700, instructionFont, purple, 1f);
		Draw.Text(gfx, "Press S for game settings", 330, 750, instructionFont, grey, 1f);
		Draw.Text(gfx, "Press D to return to menu", 333, 800, instructionFont, grey, 1f);
	}
	
	//Pre: gfx is required for drawing
	//Post: None
	//Desc: draws manual screen
	private static void drawManualScr (Graphics2D gfx)
	{
		Draw.Text(gfx, menuText, 300, 200, menuFont, white, 1f);
		Draw.Text(gfx, "Press W for the backstory", 340, 450, instructionFont, white, 1f);
		Draw.Text(gfx, "Press E for instructions on how to play", 265, 500, instructionFont, white, 1f);
		Draw.Text(gfx, "Press D to return to menu", 750, 950, navigationFont, purple, 1f);
	}
	
	//Pre: gfx is required for drawing
	//Post: None
	//Desc: draws back story screen
	private static void drawStoryScr (Graphics2D gfx)
	{
		Draw.Text(gfx, menuText, 215, 200, menuFont, white, 1f);
		
		Draw.Text(gfx, "Congratulations! You have just graduated from Havard University with a", 50, 380, smallInstrFont, white, 1f);
		Draw.Text(gfx, "Ph.D. in biophysics. You are confident that you have a bright, rich and ", 50, 410, smallInstrFont, white, 1f);
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
		
		Draw.Text(gfx, "Press D to return to menu", 750, 920, navigationFont, purple, 1f);
		Draw.Text(gfx, "Press F to return to manual", 750, 950, navigationFont, purple, 1f);
		Draw.Text(gfx, "Press E for instructions", 750, 980, navigationFont, purple, 1f);
	}

	//Pre: gfx is required for drawing
	//Post: None
	//Desc: Draws instruction screen
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
		
		//lines labeling parts of the game screenshot
		Draw.Line(gfx, 170, 900, 270, 910, 3, red, 1f);
		Draw.Line(gfx, 160, 495, 230, 520, 3, red, 1f);
		Draw.Line(gfx, 790, 495, 720, 495, 3, red, 1f);
		Draw.Line(gfx, 790, 645, 730, 645, 3, red, 1f);
		
		Draw.Text(gfx, "Press D to return to menu", 750, 920, navigationFont, purple, 1f);
		Draw.Text(gfx, "Press F to return to manual", 750, 950, navigationFont, purple, 1f);
		Draw.Text(gfx, "Press W for backstory", 750, 980, navigationFont, purple, 1f);
	}
	
	//Pre: gfx is required for drawing
	//Post: None
	//Desc: draws settings screen
	private static void drawSettingsScr (Graphics2D gfx)
	{
		Draw.Text(gfx, menuText, 250, 200, menuFont, white, 1f);
		
		//Sound settings text
		Draw.Text(gfx, "Background music", 300, 400, instructionFont, white, 1f);
		Draw.Text(gfx, "Sound effects", 300, 480, instructionFont, white, 1f);
		
		//Timer settings text
		Draw.Text(gfx, "Timer", 300, 600, instructionFont, lightBlue, 1f);
		Draw.Text(gfx, "25 seconds", 300, 650, instructionFont, white, 1f);
		Draw.Text(gfx, "20 seconds", 300, 700, instructionFont, white, 1f);
		Draw.Text(gfx, "15 seconds", 300, 750, instructionFont, white, 1f);
		Draw.Text(gfx, "10 seconds", 300, 800, instructionFont, white, 1f);
		Draw.Text(gfx, "5 seconds", 310, 850, instructionFont, white, 1f);
		
		Draw.FillEllipse(gfx, 642, secButtonMarkY, 10, 10, green, 1f); //Draws green dot indicator for timer settings
		
		Draw.Text(gfx, "Press D to return to menu", 750, 950, navigationFont, white, 1f);
		
		//Draws button outlines
		drawRecLine(backMusicButton, gfx); 
		drawRecLine(soundEffButton, gfx);
		drawRecLine(sec25Button, gfx);
		drawRecLine(sec20Button, gfx);
		drawRecLine(sec15Button, gfx);
		drawRecLine(sec10Button, gfx);
		drawRecLine(sec5Button, gfx);
		
		Font settingButtonFont = new Font("Impact", Font.PLAIN, 25);
		//Draws on or off on sound buttons
		if (backMusic == true){
			Draw.Text(gfx, "on", 643, 397, settingButtonFont, green, 1f);
		}
		else{
			Draw.Text(gfx, "off", 642, 398, settingButtonFont, white, 1f);
		}
		
		if (soundEffects == true){
			Draw.Text(gfx, "on", 643, 477, settingButtonFont, green, 1f);
		}
		else{
			Draw.Text(gfx, "off", 642, 478, settingButtonFont, white, 1f);
		}
	}
	
	//Pre: miliSec and seconds2 are what timer and seconds will become, setMiliseconds and setSeconds are what setMilisec and setSec will become
	//Post: None
	//Desc: Updates timer, setMilisec, seconds, and setSec based on player's changes to the timer setting
	private static void setTimer(float miliSec, float setMiliseconds, Integer seconds2, int setSeconds, GameRectangle secButton)
	{
		timer = miliSec;
		setMilisec = setMiliseconds;
		seconds = seconds2;
		setSec = setSeconds;
		
		secButtonMarkY = secButton.GetTop() + 13; //updates y coordinate (of green dot that indicates which time player chose) to new secButton
	}

	//Pre: gfx is required for drawing, gc is required for defining bounding box for SpriteSheets
	//Post: None
	//Desc: draws game screen
	private static void drawGameScr (Graphics2D gfx, GameContainer gc)
	{ 
		//Tells user about undo option if they have at least one ingredient 
		if (stack[0] != null)
		{
			Draw.Text(gfx, "Press Z to undo", 50, 360, smallInstrFont, red, 1f);
		}
		
		//Lets user know they can't add more ingredients (when they reach limit)
		Draw.Text(gfx, cantAddMore, 700, 150, cantAddMoreFont, red, 1f);
		
		//Draws finish button
		finishButton.Draw(gfx);
		Draw.Text(gfx, "FINISH", 860, 360, instructionFont, white, 1f);
		
		Draw.Text(gfx, "Remaining time: " + Integer.toString(seconds), 730, 50, instructionFont, white, 1f); //Displays remaining time in seconds
		
		//Draws order ticket list
		Draw.Text(gfx, "Order Ticket", 45, 60, instructionFont, lightBlue, 1f);
		drawOrder(gfx, 1, 100);
		drawOrder(gfx, 2, 125);
		drawOrder(gfx, 3, 150);
		drawOrder(gfx, 4, 175);
		drawOrder(gfx, 5, 200);
		drawOrder(gfx, 6, 225);
		drawOrder(gfx, 7, 250);
		drawOrder(gfx, 8, 275);
		
		//Draws user's burger
		for (int i = 0; i < stackImgs.length; i++)
		{
			if (stackImgs[i] != null){
				int y = 270 - 30 * i; //subtracts 30 from y coordinate of ingredient each time
				stackImgs[i].destRec = new Rectangle(380, y, (int)(gc.GetWidth() * 0.2), (int)(gc.GetHeight() * 0.2)); //Defines bounding box
				Draw.Sprite(gfx, stackImgs[i]); //Draws ingredient 
			}
		}

		Draw.Rect(gfx, 30, 450, 940, 500, 5, white, 1f); //Draws overall outline of ingredient buttons
		
		//Draws ingredient images for the buttons
		for (int i = 0; i < buttonImgs.length; i++)
		{
			Draw.Sprite(gfx, buttonImgs[i]); 
		}
		
		//Draws each ingredient button outline
		for (int i = 0; i < 8; i++)
		{
			GameRectangle rec = container[i];
			drawRecLine(rec, gfx);
		}
	}
	
	//Pre: gfx is required for drawing
	//Post: None
	//Desc: Draws score screen
	private static void drawScoreScr (Graphics2D gfx)
	{
		Draw.Text(gfx, "Score", 350, 200, menuFont, white, 1f);
		Draw.Text(gfx, "Your score:", 380, 450, instructionFont, white, 1f);
		Draw.Text(gfx, Integer.toString(score), 530, 450, menuFont, white, 1f);
		
		Draw.Text(gfx, "Press D to return to menu", 750, 950, navigationFont, white, 1f);
		Draw.Text(gfx, "Press space bar to replay", 345, 650, instructionFont, lightBlue, 1f);
	}

	//Pre: circle is required for its coordinates
	//Post: returns true if there's a collision, false otherwise
	//Desc: detecs collision between circle and mouse click
	private static boolean pointCircleColl (GameCircle circle)
	{
		//Calculates the distance squared using pythagorean theorem between the point and the circle's centre
		double distanceSqr = Math.pow(mousePos.x - circle.GetCentre().x, 2) + Math.pow(mousePos.y - circle.GetCentre().y, 2);
		
		//If that distance is within range of the radius then there is a collision
		if (distanceSqr <= Math.pow(circle.GetRad(), 2))	
		{
			return true;
		}
		
		return false;
	}


	//Pre: None
	//Post: None
	//Desc: Sets up game when user presses space bar
	private static void pressedSpace()
	{
		menuText = "Game Play";
		genIngredients();
		screenColour = black;
		alive = true;
		
		//plays background music if user has the setting turned on
		if (backMusic == true){
			backgrMusic.Play();
		}
	}

	//Pre: ingredientNum is the ingredient user clicked on
	//Post: None
	//Desc: Assigns an ingredient to player's burger based 
	private static void assignIngrToStack(int ingredientNum)
	{
		if (ingredientNum == 0){
			stack[currentLayer] = "tomatoes";
		}
		else if (ingredientNum == 1){
			stack[currentLayer] = "cheese";
		}
		else if (ingredientNum == 2){
			stack[currentLayer] = "lettuce";
		}
		else if (ingredientNum == 3){
			stack[currentLayer] = "onions";
		}
		else if (ingredientNum == 4){
			stack[currentLayer] = "patty";
		}
		else if (ingredientNum == 5){
			stack[currentLayer] = "bacon";
		}
		else if (ingredientNum == 6){
			stack[currentLayer] = "egg";
		}
		else if (ingredientNum == 7){
			stack[currentLayer] = "bun";
		}
		
		// Assigns player's burger an ingredient image 
		if (ingredientNum == 7){
			if (currentLayer == 0){ //Only the first layer can have a bottom bun
				stackImgs[currentLayer] = ingredientImgs[7];
			}
			else{
				stackImgs[currentLayer] = ingredientImgs[8]; //every other layer can have a top bun 
			}
		}
		else{
			stackImgs[currentLayer] = ingredientImgs[ingredientNum];
		}
	}
	
	//Pre: one two and three are sound effects to be stopped
	//Post: None
	//Desc: Stops sound effects
	private static void stopScoreSnds (SoundClip one, SoundClip two, SoundClip three)
	{
		one.Stop();
		two.Stop();
		three.Stop();
	}

	//Pre: None
	//Post: None
	//Desc: Brings state out of Game Play and into Score Screen
	private static void endGame()
	{
		menuText = "Score Screen";
		screenColour = purple;
		alive = false;
		backgrMusic.Stop();
		
		calcScore();
		resetGame();
	}

	//Pre: None
	//Post: None
	//Desc: calculates player's score
	private static void calcScore ()
	{
		double subtotal = 0; //Stores player's score out of 10
		
		//Checks for matching ingredients in player and order ticket's burger
		for (int i = 0; i < 10; i++)
		{
			if (orderTicketIngr[i] == stack[i])
			{
				subtotal++;
			}
		}
		
		subtotal = (subtotal / 10) * 100; //Converts score to out of 100
		score = (int)subtotal; //Rounded score out of 100
		
		//Plays sound effects depending on score if sound effects turned on in settings
		if (soundEffects == true)
		{
			if (score == 100){
				score100Snd.Play();
			}
			if (score <= 20){
				score0to20Snd.Play();
			}
			else if (score >= 80){
				score80PlusSnd.Play();
			}
		}
	}
	
	//Pre: None
	//Post: None
	//Desc: Resets game settings
	private static void resetGame ()
	{
		timer = setMilisec;
		seconds = setSec;
		currentLayer = 0;
		cantAddMore = "";
		
		for (int i = 0; i < 10; i++)
		{
			stack[i] = null;
			stackImgs[i] = null;
		}
	}
}
