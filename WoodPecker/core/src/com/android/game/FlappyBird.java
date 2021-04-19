package com.android.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture[] bird;
	private Sprite birdSprite;

	private Texture background;
	private Texture canoTopo;
	private Texture canoBaixo;
	private Texture gameOver;
	private float framesTransition = 0; //Bird animation
	private Random randNum;
	private BitmapFont fonte;
	private BitmapFont restart;
	private Circle birdHitbox;
	private Rectangle topDuctHitbox;
	private Rectangle lowerDuctHitbox;
	//private ShapeRenderer shape; //You can use this to see the hitbox of the objects
	private Boolean controlFly = false;


	//Config variables
	private float widthDevice;
	private float heightDevice;
	private int stateGame = 0; // if stateGame = 0 : game no started, if stateGame = 1: game started, if stateGame = 2: gameover
	private int points = 0;
	private boolean check = false;

	private int initialVerticalPosition;
	private int dropVelocity = 0;
	private float movDuctHorizontalPos;
	private float movBackgroundHorizontalPos;
	private float spaceBetweenDucts;
	private float deltatime;
	private float randHeightsBetweenDucts;

	//Camera
	private OrthographicCamera cam;
	private Viewport viewport;
	private final float VIRTUAL_HEIGHT = 1024;
	private final float VIRTUAL_WIDTH = 768;


	@Override
	public void create () {
		batch = new SpriteBatch();

		birdHitbox = new Circle();
		//shape = new ShapeRenderer();

		randNum = new Random();
		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(6);

		restart = new BitmapFont();
		restart.setColor(Color.WHITE);
		restart.getData().setScale(3);

		bird = new Texture[3];
		bird[0] = new Texture("picapau1.png");
		bird[1] = new Texture("picapau2.png");
		bird[2] = new Texture("picapau4.png");

		background = new Texture("fundo3.png");
		canoBaixo = new Texture("tronco2.png");
		canoTopo = new Texture("tronco1.png");

		gameOver = new Texture("game_over.png");

		//--Recupera a altura real do dispositivo--
		//widthDevice = Gdx.graphics.getWidth();
		//heightDevice = Gdx.graphics.getHeight();

		widthDevice = VIRTUAL_WIDTH;
		heightDevice = VIRTUAL_HEIGHT;

		initialVerticalPosition = (int) (heightDevice/2); //control the heigth of the bird on screen

		movDuctHorizontalPos = widthDevice;
		spaceBetweenDucts = 350;

		//Configuring camera
		cam = new OrthographicCamera();
		viewport = new StretchViewport(VIRTUAL_WIDTH,VIRTUAL_HEIGHT,cam);
		cam.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2,0);

		//Cofig bird Sprite
		birdSprite = new Sprite();
		birdSprite.setPosition(120,initialVerticalPosition);
		birdSprite.setSize(bird[(int) framesTransition].getWidth(),bird[(int) framesTransition].getHeight());
		birdSprite.setRotation(0);

	}

	@Override
	public void render () {

		cam.update();
		//Clear cache frames
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BITS);

		deltatime =  Gdx.graphics.getDeltaTime();

		//Bird animation
		if(controlFly == false) {
			framesTransition += deltatime * 10;  // this line could be framesTransition += 0.1;
			Gdx.app.log("wtf",String.valueOf(framesTransition));
		}
		else framesTransition -= deltatime * 10;


		if (framesTransition >= 3 && controlFly == false){
			controlFly = true;
			framesTransition = (float) 2.9;
		}
		else if (framesTransition < 0 && controlFly == true){
			controlFly = false;
			framesTransition = 0;
		}

		//check if the game starts
		if(stateGame == 0){
			if (Gdx.input.justTouched()){
				stateGame = 1;
			}
		}
		else{
			//dreacrease the heigth of the bird
			dropVelocity++;

			//check if the user click, and add negative fall heigth
			if (Gdx.input.justTouched()) dropVelocity = -15;

			//check if the bird get out of the vertical screen
			if (initialVerticalPosition > 0 || dropVelocity < 0)
				initialVerticalPosition = initialVerticalPosition  -  dropVelocity;

			if (stateGame == 1){ //Starts game

				//move ducs to left
				movDuctHorizontalPos-= deltatime * 350;
				movBackgroundHorizontalPos -= deltatime * 50;


				if (movBackgroundHorizontalPos < -background.getWidth()/2){
					movBackgroundHorizontalPos = 0;
				}

				//generate randon ducts heights to the screen
				if (movDuctHorizontalPos < -canoBaixo.getWidth()){
					movDuctHorizontalPos = widthDevice;
					randHeightsBetweenDucts = randNum.nextInt(500) - 250;
					check = false;
				}


				if (movDuctHorizontalPos < 120 && check == false){
					points++;
					check = true;
				}
			}
			else{ //Game over
				if (Gdx.input.justTouched()){
					movDuctHorizontalPos = widthDevice;
					initialVerticalPosition = (int) (heightDevice/2);
					dropVelocity = 0;
					points = 0;
					stateGame = 0;
				}
			}
		}


		//Configure cam
		batch.setProjectionMatrix(cam.combined);


		batch.begin();

		//Gdx.app.log("wtf", String.valueOf(widthDevice));
		batch.draw(background,movBackgroundHorizontalPos,0,widthDevice * 4,heightDevice);
		batch.draw(canoTopo,movDuctHorizontalPos,heightDevice/2 + spaceBetweenDucts/2 + randHeightsBetweenDucts);
		batch.draw(canoBaixo,movDuctHorizontalPos,heightDevice/2 - canoBaixo.getHeight() - spaceBetweenDucts/2 + randHeightsBetweenDucts);

		birdSprite.setRegion(bird[(int)framesTransition]);
		birdSprite.setPosition(120,initialVerticalPosition);
		birdSprite.draw(batch);

		//controling the angle of the bird
		if (dropVelocity < 0) birdSprite.setRotation(-dropVelocity);
		else birdSprite.setRotation(0);

		fonte.draw(batch,String.valueOf(points),widthDevice/2 - fonte.getXHeight()/2,  heightDevice - heightDevice/8);

		// draw gameover screen
		if (stateGame == 2){
			batch.draw(gameOver,widthDevice/2 - gameOver.getWidth()/2,heightDevice/2 - gameOver.getHeight()/2);
			restart.draw(batch,"Clique para recomeçar!",widthDevice/2 - 220,heightDevice/2 - heightDevice/16);
		}

		batch.end();

		//criando as hitbox/shapes
		birdHitbox.set(120 + bird[0].getWidth()/2,initialVerticalPosition + bird[0].getHeight()/2,bird[0].getWidth()/2 - bird[0].getWidth()/7);
		topDuctHitbox = new Rectangle(movDuctHorizontalPos,heightDevice/2 + spaceBetweenDucts/2 + randHeightsBetweenDucts,canoTopo.getWidth(),canoTopo.getHeight());
		lowerDuctHitbox = new Rectangle(movDuctHorizontalPos,heightDevice/2 - canoBaixo.getHeight() - spaceBetweenDucts/2 + randHeightsBetweenDucts,canoBaixo.getWidth(),canoBaixo.getHeight());

		//Drawing shapes/hitbox to help see the colision
		/*
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.circle(birdHitbox.x,birdHitbox.y,birdHitbox.radius);
		shape.rect(topDuctHitbox.x,topDuctHitbox.y,topDuctHitbox.width,topDuctHitbox.height);
		shape.rect(lowerDuctHitbox.x,lowerDuctHitbox.y,lowerDuctHitbox.width,lowerDuctHitbox.height);
		shape.setColor(Color.RED);
		shape.end();
		*/

		//Colision test
		if (Intersector.overlaps(birdHitbox,lowerDuctHitbox) || Intersector.overlaps(birdHitbox,topDuctHitbox) || initialVerticalPosition <= 0 || initialVerticalPosition >= heightDevice ){
			stateGame = 2;
		}

		//Alternative way to draw the bird : Using Spritebatch
		//batch.draw(bird[(int)framesTransition],120,initialVerticalPosition);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height);
	}

	//@Override
	//public void dispose () {
	//	batch.dispose();
	//	img.dispose();
	//}
}
