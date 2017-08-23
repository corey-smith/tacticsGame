package com.game.ui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tactics.game.SkirmishView;
import com.tactics.unit.Unit;
import com.tactics.unit.action.Action;

public class Ui {

    SkirmishView game;
	Stage stage;
	Skin skin;
	ActionButtonBuilder actionButtonBuilder;
	
	/**
	 * Class to handle user interfaces
	 */
	public Ui(SkirmishView skirmishView) {
		this.game = skirmishView;
		createUI();
		actionButtonBuilder = new ActionButtonBuilder(skin, stage, skirmishView);
	}
	
	/**
	 * Update function called from main class
	 */
	public void update() {
		this.actionButtonBuilder.update();
	}
	
	public void render() {
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	/**
	 * Creates stage UI stuff
	 */
	private void createUI() {
		stage = new Stage(new ScreenViewport());
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
	}
	
	/**
	 * Remove all UI elements
	 */
	public void clearHud() {
		stage.clear();
	}
	
	/**
	 * Creates all UI elements depending on the selected unit
	 * @param selectedUnit
	 */
	public void createHud(Unit selectedUnit) {
	    stage.clear();
		ArrayList<Action> actions = selectedUnit.getActions();
		actionButtonBuilder.createActionButtons(actions, Gdx.graphics.getWidth());
	}
	
	public Stage getStage() {
		return this.stage;
	}
	
}
