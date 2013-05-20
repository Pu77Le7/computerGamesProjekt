package com.me.mygdxgame;

import java.util.Iterator;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class computergames implements ApplicationListener {
  Texture playerImage;
	Texture enemyImage;
	Texture backgroundImage;
	Sound start;
	Music main;
	Music dead;
	OrthographicCamera camera;
	SpriteBatch batch;
	Rectangle player;
	Array<Rectangle> enemiesTop;
	Array<Rectangle> enemiesBot;
	Array<Rectangle> enemiesLeft;
	Array<Rectangle> enemiesRight;
	long lastSpawnTime;
	int spawnPos = 1;
	long scoringTime;
	int score = 0;
	BitmapFont scoreText;
	boolean active = true;
	
	
	@Override
	public void create() {
		
		//Visual
		playerImage = new Texture(Gdx.files.internal("data/player.png"));
		enemyImage = new Texture(Gdx.files.internal("data/enemy.png"));
		backgroundImage = new Texture(Gdx.files.internal("data/background.png"));
		
		//Audio
		start = Gdx.audio.newSound(Gdx.files.internal("data/start.mp3"));
		main = Gdx.audio.newMusic(Gdx.files.internal("data/music.mp3"));
		dead = Gdx.audio.newMusic(Gdx.files.internal("data/dead.mp3"));
		
		main.setLooping(true);
		main.play();
		
		//Camera & SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 600);
		batch = new SpriteBatch();
		
		//Coordination
		player = new Rectangle();
		player.x = 800 / 2 - 12 / 2;
		player.y = 600 / 2 - 12 / 2;
		player.width = 12;
		player.height = 12;
		
		//Enemies
		enemiesTop = new Array<Rectangle>();
		enemiesBot = new Array<Rectangle>();
		enemiesLeft = new Array<Rectangle>();
		enemiesRight = new Array<Rectangle>();
		spawnEnemiesTop();
		
		//Score
		scoringTime = TimeUtils.millis();
		scoreText = new BitmapFont(Gdx.files.internal("data/arial-15.fnt"), Gdx.files.internal("data/arial-15.png"), false);
		
	}
	@Override
	public void dispose() {
		playerImage.dispose();
		enemyImage.dispose();
		main.dispose();
		batch.dispose();
	}
	@Override
	public void render() {
		//Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		scoreText.setColor(0.0f, 0.0f, 1.0f, 1.0f);
		//Batch
		batch.begin();
		batch.draw(backgroundImage, 0, 0);
		scoreText.draw(batch, "Score: " + score, 720, 580); //Move Score to left
		batch.draw(playerImage, player.x, player.y);
		for (Rectangle enemy: enemiesTop) {
			batch.draw(enemyImage, enemy.x, enemy.y);
		}
		for (Rectangle enemy: enemiesBot) {
			batch.draw(enemyImage, enemy.x, enemy.y);
		}
		for (Rectangle enemy: enemiesLeft) {
			batch.draw(enemyImage, enemy.x, enemy.y);
		}
		for (Rectangle enemy: enemiesRight) {
			batch.draw(enemyImage, enemy.x, enemy.y);
		}
		batch.end();
		//When to spawn Enemies
		if (TimeUtils.millis() - lastSpawnTime > 100) {
			
			spawnPos = 1 + (int) (Math.random() * 4);
			switch(spawnPos){
			case 1: spawnEnemiesTop();
			break;
			case 2: spawnEnemiesBot();
			break;
			case 3: spawnEnemiesLeft();
			break;
			case 4: spawnEnemiesRight();
			break;
			}
		}
		//Enemies movement
		Iterator<Rectangle> iterTop = enemiesTop.iterator();
		while (iterTop.hasNext()) {
			Rectangle enemy = iterTop.next();
			enemy.y -= 300 * Gdx.graphics.getDeltaTime();
			/*if (score % 1000 == 0) {
				enemy.y -= 50 * Gdx.graphics.getDeltaTime();;
			}*/
			if (enemy.y + 12 < 0) {
				iterTop.remove();
			}
			if (enemy.overlaps(player)) {
				iterTop.remove();
				gameOver();
			}
		}
		Iterator<Rectangle> iterBot = enemiesBot.iterator();
		while (iterBot.hasNext()) {
			Rectangle enemy = iterBot.next();
			enemy.y += 300 * Gdx.graphics.getDeltaTime();
			/*if (score % 1000 == 0) {
				enemy.y += 50 * Gdx.graphics.getDeltaTime();;
			}*/
			if (enemy.y + 12 > 600) {
				iterBot.remove();
			}
			if (enemy.overlaps(player)) {
				iterBot.remove();
				gameOver();
			}
		}
		Iterator<Rectangle> iterLeft = enemiesLeft.iterator();
		while (iterLeft.hasNext()) {
			Rectangle enemy = iterLeft.next();
			enemy.x += 300 * Gdx.graphics.getDeltaTime();
			/*if (score % 1000 == 0) {
				enemy.x += 50 * Gdx.graphics.getDeltaTime();;
			}*/
			if (enemy.x + 12 > 800) {
				iterLeft.remove();
			}
			if (enemy.overlaps(player)) {
				iterLeft.remove();
				gameOver();
			}
		}
		Iterator<Rectangle> iterRight = enemiesRight.iterator();
		while (iterRight.hasNext()) {
			Rectangle enemy = iterRight.next();
			enemy.x -= 300 * Gdx.graphics.getDeltaTime();
			/*if (score % 1000 == 0) {								//change the speed
				enemy.x -= 50 * Gdx.graphics.getDeltaTime();
				
				
				//create a violet cube with special qualities
			}*/
			if (enemy.x < 0) {
				iterRight.remove();
			}
			if (enemy.overlaps(player)) {
				iterRight.remove();
				gameOver();
			}
		}
		//Scoring
		if (TimeUtils.millis() - scoringTime > 1000) {
			if (active != false){
				score += 50;
			}
			scoringTime = TimeUtils.millis();
		}
		//Controls
		if (active != false){
			if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				player.x -= 400 * Gdx.graphics.getDeltaTime();
			}
			if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				player.x += 400 * Gdx.graphics.getDeltaTime();
			}
			if (Gdx.input.isKeyPressed(Keys.UP)) {
				player.y += 400 * Gdx.graphics.getDeltaTime();
			}
			if (Gdx.input.isKeyPressed(Keys.DOWN)) {
				player.y -= 400 * Gdx.graphics.getDeltaTime();
			}
			if (player.x < 0) {
				player.x = 0;
			}
			if (player.x > 800 - 12) {
				player.x = 800 - 12;
			}
			if (player.y < 0) {
				player.y = 0;
			}
			if (player.y > 600 - 12) {
				player.y = 600 - 12;
			}
		}
	}
	private void spawnEnemiesTop(){
		Rectangle enemy = new Rectangle();
		enemy.x = MathUtils.random(0, 800 - 12);
		enemy.y = 600;
		enemy.width = 12;
		enemy.height = 12;
		enemiesTop.add(enemy);
		lastSpawnTime = TimeUtils.millis();
	}
	private void spawnEnemiesBot(){
		Rectangle enemy = new Rectangle();
		enemy.x = MathUtils.random(0, 800 - 12);
		enemy.y = 0 - 12;
		enemy.width = 12;
		enemy.height = 12;
		enemiesBot.add(enemy);
		lastSpawnTime = TimeUtils.millis();
	}
	private void spawnEnemiesLeft(){
		Rectangle enemy = new Rectangle();
		enemy.x = 0 - 12;
		enemy.y = MathUtils.random(0, 600);
		enemy.width = 12;
		enemy.height = 12;
		enemiesLeft.add(enemy);
		lastSpawnTime = TimeUtils.millis();
	}
	private void spawnEnemiesRight(){
		Rectangle enemy = new Rectangle();
		enemy.x = 800;
		enemy.y = MathUtils.random(0, 600);
		enemy.width = 12;
		enemy.height = 12;
		enemiesRight.add(enemy);
		lastSpawnTime = TimeUtils.millis();
	}
	private void gameOver(){
		main.stop();
		active = false;
		//Hit sound missing
		dead.play();

	}
	@Override
	public void resize(int width, int height) {
	}
	@Override
	public void pause() {
	}
	@Override
	public void resume() {
	}
}
