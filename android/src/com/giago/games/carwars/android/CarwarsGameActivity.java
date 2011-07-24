/**
 * Copyright 2010 The ForPlay Authors
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
package com.giago.games.carwars.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.giago.games.carwars.core.CarwarsGame;
import com.giago.games.carwars.core.GameListener;

import forplay.android.GameActivity;
import forplay.core.ForPlay;

public class CarwarsGameActivity extends GameActivity {

  private CarwarsGame game;
  
  private int score;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);    
    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    platform().assetManager().setPathPrefix("com/giago/games/carwars/resources");
    game = new CarwarsGame();
    game.setGameListener(new GameListener() {
      
      @Override
      public void onEnd() {
        game.onPause();
        AlertDialog.Builder builder = new AlertDialog.Builder(CarwarsGameActivity.this);
        builder.setTitle("Game over! Your score is : " + score);
        builder.setPositiveButton("New game", new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            game.reset();
          }
        });
        builder.setNegativeButton("Quit", new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            CarwarsGameActivity.this.finish();
          }
        });
        builder.show();
      }
      
      @Override
      public void onScoreChange(int score) {
        updateScore(score);
      }
      
    });
    ForPlay.run(game);
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    game.onResume();
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    game.onPause();
  }
  
  private void updateScore(int score) {
    this.score = score;
  }
  
}
