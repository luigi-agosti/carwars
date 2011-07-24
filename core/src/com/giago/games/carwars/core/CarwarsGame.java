/**
 * Copyright 2011 The ForPlay Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.giago.games.carwars.core;

import static forplay.core.ForPlay.assetManager;
import static forplay.core.ForPlay.graphics;
import static forplay.core.ForPlay.pointer;

import java.util.ArrayList;
import java.util.List;

import forplay.core.Game;
import forplay.core.GroupLayer;
import forplay.core.Image;
import forplay.core.ImageLayer;
import forplay.core.Pointer;
import forplay.core.ResourceCallback;
import forplay.core.Surface;
import forplay.core.SurfaceLayer;

public class CarwarsGame implements Game, Pointer.Listener {
  
  private SurfaceLayer gameLayer;
  private GroupLayer carLayer;
  private List<EnemyCar> enemies;
  private Car car;
  private Track track;
  private float currentCenter;
  private int cycles = 0;
  private GameListener gameListener;
  private boolean onPause = false;
  private int score = 0;
  private int previousCycleScore = 0;
  private static final int WIDTH = 480;
  private static final int HEIGHT = 800;
  
  @Override
  public void init() {
    createSurface();
    initCarLayer();
    setControls();
    preloadAssets();
    addMouseTouchListener();
    drawCar();
    initTrack();
  }
  
  public void onPause() {
    onPause = true;
  }

  public void onResume() {
    onPause = false;
  }
  
  public void reset() {
    score = 0;
    init();
    onResume();
  }

  private void initTrack() {
    track = new Track(assetManager());
  }

  private void createSurface() {
    graphics().setSize(HEIGHT, WIDTH);
    gameLayer = graphics().createSurfaceLayer(graphics().width(), graphics().height());
    graphics().rootLayer().add(gameLayer);
  }

  private void drawCar() {
    car = new Car(carLayer, graphics());
    car.setGameListener(gameListener);
    populateEnemies();
  }

//  private void setBackground() {
//    Image bgImage = assetManager().getImage("images/bg.png");
//    ImageLayer bgLayer = graphics().createImageLayer(bgImage);
//    graphics().rootLayer().add(bgLayer);
//  }

  private void populateEnemies() {
    enemies = new ArrayList<EnemyCar>();
    EnemyCar e = new EnemyCar(carLayer, graphics(), -100, 50);
    enemies.add(e);
  }

  private void initCarLayer() {
    carLayer = graphics().createGroupLayer();
    graphics().rootLayer().add(carLayer);
  }

  private void preloadAssets() {
    assetManager().getImage(Car.IMAGE);
  }

  private void addMouseTouchListener() {
    pointer().setListener(this);
  }

  @Override
  public void paint(float alpha) {
    if(onPause) {
      return;
    }
    Surface surface = gameLayer.surface();
    surface.clear();
    track.paint(surface, alpha);
    currentCenter = track.getCenter();
  }

  @Override
  public void update(float delta) {
    if(onPause) {
      return;
    }
    for (EnemyCar e : enemies) {
      e.update(currentCenter);
      checkCollisionsWithEnemies(e);
    }
    increaseEnemies();
    car.update(touchVectorX, touchVectorY);
    checkCollisionsWith(track);
    cycles++;
    if(cycles == previousCycleScore + 100) {
      previousCycleScore = cycles;
      increaseScore(10);
    }
  }

  private void increaseScore(int delta) {
    this.score += delta;
    gameListener.onScoreChange(score);
  }

  private void increaseEnemies() {
    EnemyCar e;
    if(cycles > 500 && enemies.size() == 1) {
      e = new EnemyCar(carLayer, graphics(), 0, -50);
      e.setVelocity(3);
      enemies.add(e);
    } else if(cycles > 1000 && enemies.size() == 2) {
      e = new EnemyCar(carLayer, graphics(), 100, -100);
      e.setVelocity(2);
      enemies.add(e);
    } else if(cycles > 1500 && enemies.size() == 3) {
      e = new EnemyCar(carLayer, graphics(), 150, 100);
      e.setVelocity(-1);
      enemies.add(e);
    } else if(cycles > 2000 && enemies.size() == 4) {
      e = new EnemyCar(carLayer, graphics(), 0, 150);
      e.setVelocity(-2);
      enemies.add(e);
    } else if(cycles > 2500 && enemies.size() == 5) {
      e = new EnemyCar(carLayer, graphics(), 0, -150);
      e.setVelocity(1);
      enemies.add(e);
    }
  }

  private void checkCollisionsWith(Track track2) {
    
    
    //TODO stop
  }

  private void checkCollisionsWithEnemies(EnemyCar e) {
    if(car.isInCollision(e.x(), e.y(), e.w(), e.h())) {
      car.explode();
    }
    
    //TODO stop
  }

  @Override
  public int updateRate() {
    return 25;
  }
  
  
  
  /* =========================================================
   *  Positioning system
   * ======================================================== */
  
  private float touchVectorX, touchVectorY;
  private float touchCenterX, touchCenterY;
  private static final String CONTROL = "images/control.png";
  private Image control;
  private ImageLayer controlLayer;
  
  private void setControls() {
    touchCenterX = graphics().screenWidth() - 100;
    touchCenterY = graphics().screenHeight() -100;
    
    control = assetManager().getImage(CONTROL);
    controlLayer = graphics().createImageLayer(control);
    control.addCallback(new ResourceCallback<Image>() {
      @Override
      public void done(Image image) {
        controlLayer.setOrigin(image.width() / 2f, image.height() / 2f);
        controlLayer.setTranslation(touchCenterX, touchCenterY);
        controlLayer.setAlpha(0.3f);
        carLayer.add(controlLayer);
      }

      @Override
      public void error(Throwable err) {
        // TODO Auto-generated method stub
        
      }
    });
  }
  
  @Override
  public void onPointerDrag(float x, float y) {
    touchMove(x, y);
  }

  @Override
  public void onPointerEnd(float x, float y) {
    touchVectorX = 0; 
    touchVectorY = 0;
  }

  @Override
  public void onPointerStart(float x, float y) {
    touchMove(x, y);
  }
  
  private void touchMove(float x, float y) {
    float deltaX = x - touchCenterX;
    if(Math.abs(deltaX) > 100) {
      return;
    }
    float deltaY = y - touchCenterY;
    if(Math.abs(deltaY) > 100) {
      return;
    }
    touchVectorX = deltaX * 1.0f / (touchCenterX/20);
    touchVectorY = deltaY * 1.0f / (touchCenterY/20);
  }

  public void setGameListener(GameListener gameListener) {
    this.gameListener = gameListener;
  }
  
}
