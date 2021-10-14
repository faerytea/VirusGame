package com.github.farytea.virusgame.engine.android;

import androidx.annotation.NonNull;

import com.github.farytea.virusgame.engine.core.Coord;
import com.github.farytea.virusgame.engine.core.GameEngine;
import com.github.farytea.virusgame.engine.core.Player;

public interface BotStrategy {
    @NonNull
    Coord[] move(@NonNull GameEngine engine, @NonNull Player me);
}
