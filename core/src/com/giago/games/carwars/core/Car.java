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

import static forplay.core.ForPlay.*;

import forplay.core.Graphics;
import forplay.core.GroupLayer;
import forplay.core.Image;
import forplay.core.ImageLayer;
import forplay.core.ResourceCallback;

public class Car {
  public static final String IMAGE = "images/car.png";
  public static final String IMAGE1 = "images/car1.png";
  public static final String IMAGE2 = "images/car2.png";
  public static final String IMAGE3 = "images/car3.png";
  public static final String IMAGE4 = "images/car4.png";
  
  private ImageLayer layer;
  //private ImageLayer explosion;
  private float x;
  private float y;
  private int limitX;
  private int limitY;
  private boolean explode = false;
  private GameListener gameListener;
  private Image car;
  private Image carExplosion1;
  private Image carExplosion2;
  private Image carExplosion3;
  private Image carExplosion4;

  public Car(final GroupLayer groupLayer, final Graphics graphics) {
    limitX = graphics.width();
    limitY = graphics.height();
    this.x = limitX/2;
    this.y = limitY/2;
    car = assetManager().getImage(IMAGE);
    carExplosion1 = assetManager().getImage(IMAGE1);
    carExplosion2 = assetManager().getImage(IMAGE2);
    carExplosion3 = assetManager().getImage(IMAGE3);
    carExplosion4 = assetManager().getImage(IMAGE4);
    layer = graphics().createImageLayer(car);
    car.addCallback(new ResourceCallback<Image>() {
      @Override
      public void done(Image image) {
        layer.setOrigin(image.width() / 2f, image.height() / 2f);
        layer.setTranslation(x, y);
        groupLayer.add(layer);
      }

      @Override
      public void error(Throwable err) {
        log().error("Error loading image!", err);
      }
    });
  }

  private float fire = 0;
  private float angle = 0;
  
  public void update(final float x, final float y) {
    if(explode) {
      increaseFire();
      angle = 10;
      explode = false;
    }
    if(angle != 0) {
      angle--;
    }
    this.x += x;
    this.y += y;
    layer.setRotation(angle);
    limitMovement();
    layer.setTranslation(this.x, this.y);
  }
  
  
  
  private void increaseFire() {
    fire++;
    if(fire > 400f) {
      layer.setImage(carExplosion4);
      if(gameListener != null) {
        gameListener.onEnd();
      }
    } else if(fire > 300f) {
      layer.setImage(carExplosion3);
    } else if(fire > 200f) {
      layer.setImage(carExplosion2);
    } else if(fire > 100) {
      layer.setImage(carExplosion1);
    } 
  }

  private void limitMovement() {
    if(this.y < 0) {
      this.y = 0;
    }
    if(this.y >= limitY) {
      this.y = limitY;
    }
    if(this.x < 0) {
      this.x = 0;
    }
    if(this.x >= limitX) {
      this.x = limitX;
    }
  }

  public boolean isInCollision(float x2, float y2, int w, int h) {
    int wc = (w + layer.image().width())/2;
    int hc = (h + layer.image().height())/2;
    float xc = Math.abs(x - x2);
    float yc = Math.abs(y - y2);
    if(xc < wc && yc < hc) {
      return true;
    }
    return false;
  }
  
  public void explode() {
    explode = true;
  }
  
  public void setGameListener(GameListener gameListener) {
    this.gameListener = gameListener;
  }
}
