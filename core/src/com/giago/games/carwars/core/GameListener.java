package com.giago.games.carwars.core;

public interface GameListener {
  
  void onEnd();
  
  void onScoreChange(int scoreChange);

}
