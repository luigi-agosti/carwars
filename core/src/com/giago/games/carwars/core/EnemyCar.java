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

import java.util.Random;

import forplay.core.Graphics;
import forplay.core.GroupLayer;
import forplay.core.Image;
import forplay.core.ImageLayer;
import forplay.core.ResourceCallback;

public class EnemyCar {
  public static final String IMAGE = "images/enemy.png";
  private ImageLayer layer;
  private float x;
  private float delta;
  private int velocity = 1;
  private float y;
  private int limit;
  private Random randomizer = new Random();
  
  public EnemyCar(final GroupLayer groupLayer, final Graphics graphics, final float x, final float delta) {
    this.x = x;
    this.y = graphics.width()/2;
    this.delta = delta;
    Image image = assetManager().getImage(IMAGE);
    layer = graphics().createImageLayer(image);
    image.addCallback(new ResourceCallback<Image>() {
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
    
    limit = graphics.width();
  }
  
  public void setVelocity(int velocity) {
    this.velocity = velocity;
  }

  public void update(float c) {
    if(velocity < 0) {
      if(x > 0) {
        x += velocity;
      } else {
        x = limit + 100;
        changeVelocity();
      }
    } else {
      if(x < limit) {
        x += velocity;
      } else {
        x = -100;
        changeVelocity();
      }
    }  
    y = c + delta;
    layer.setTranslation(x, y);
  }

  public void changeVelocity() {
    velocity = randomizer.nextInt(2) + 1;
    if(randomizer.nextBoolean()) {
      velocity *= -1;
    }
  }
  
  public float x() {
    return x;
  }
  
  public float y() {
    return y;
  }
  
  public int w() {
    return layer.image().width();
  }
  
  public int h() {
    return layer.image().height();
  }
}
