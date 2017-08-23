package com.game.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.tactics.game.SkirmishView;
import com.tactics.unit.action.Action;

public class ActionButtonBuilder {

	Skin skin;
	Stage stage;
	SkirmishView game;
	int actionsWidth;
	final int actionButtonSize = 24;
	final int actionBuffer = 8;
	HashMap<ImageButton, Action> actionsButtonsMap;
	//window to display stats about the action button being hovered over
	Window actionWindow = null;
	ImageButton hoverButton;
	Long actionHoverStartTime;
	Long actionHoverCurTime;
	int actionHoverSeconds = 1;
	
	/**
	 * Class to build out the action buttons for the currently selected unit
	 * @param skin
	 * @param stage
	 */
	public ActionButtonBuilder(Skin skin, Stage stage, SkirmishView skirmishView) {
		this.skin = skin;
		this.stage = stage;
		this.game = skirmishView;
		//1/3 of the width is the size allocated for actions buttons
		this.actionsWidth = Gdx.graphics.getWidth()/3;
		this.actionsButtonsMap = new HashMap<ImageButton, Action>();
	}
	
	/**
	 * Update method called from UI's update
	 */
	public void update() {
		//decide whether enough time has elapsed to display action button window
		if(hoverButton != null) {
			actionHoverCurTime = System.currentTimeMillis();
			if((actionHoverCurTime - actionHoverStartTime)/1000 >= actionHoverSeconds && actionWindow == null) {
				actionWindow = getActionWindow(hoverButton);
				stage.addActor(actionWindow);
			}
		}
	}
	
	/**
	 * Builds out the hashmap linking buttons to actions
	 * given an arraylist of actions (should be the selected unit's actions)
	 * @param actions
	 * @param screenWidthBase 
	 */
	protected void createActionButtons(ArrayList<Action> actions, float screenWidthBase) {
		int centerScreenX = (int) (screenWidthBase/2);
		//width of all of the buttons together, including buffers
		int buttonsWidth = (actions.size()*(actionButtonSize + actionBuffer)) - actionBuffer;
		
		int curXPos = centerScreenX - (buttonsWidth/2);
		for(Action action : actions) {
			ImageButton actionBtn = createActionButton(action, curXPos, actionBuffer);
		    stage.addActor(actionBtn);
		    curXPos += (actionButtonSize + actionBuffer);
		}
	}
	
	/**
	 * Creates a single action button given a screen location, and action
	 * @param action
	 * @param xPos
	 * @param yPos
	 * @return - resulting action (ImageButton) button
	 */
	private ImageButton createActionButton(Action action, int xPos, int yPos) {
		ImageButton.ImageButtonStyle imageButtonStyle = null;
		if(action.getName().equalsIgnoreCase("quick")) {
			imageButtonStyle = skin.get("quick", ImageButton.ImageButtonStyle.class);
		} else {
			imageButtonStyle = skin.get("precision", ImageButton.ImageButtonStyle.class);
		}
		ImageButton curBtn = new ImageButton(imageButtonStyle);
		this.actionsButtonsMap.put(curBtn, action);
		curBtn.setSize(actionButtonSize, actionButtonSize);
	    curBtn.setX(xPos);
	    curBtn.setY(yPos);
	    addListeners(curBtn);
	    return curBtn;
	}
	
	/**
	 * Adds all of the listeners to each action button
	 * @param curBtn
	 */
	public void addListeners(ImageButton curBtn) {
	    curBtn.addListener(leftClickListener);
	    curBtn.addListener(rightClickListener);
	    curBtn.addListener(enterListener);
	    curBtn.addListener(exitListener);
	}
	
	/**
	 * Left click on action button
	 */
	private ClickListener leftClickListener = new ClickListener(Buttons.LEFT){
    	public void clicked(InputEvent event, float x, float y) {
    		Action selectedAction = actionsButtonsMap.get(event.getListenerActor());
    		game.getSelectedUnit().setAction(selectedAction);
    		//uncheck all other buttons
    		Iterator<Entry<ImageButton, Action>> buttonIter = actionsButtonsMap.entrySet().iterator();
    		while(buttonIter.hasNext()) {
    			Entry<ImageButton, Action> curEntry = buttonIter.next();
    			ImageButton curBtn = curEntry.getKey();
    			if(!curBtn.equals(event.getListenerActor())) {
    				curBtn.setChecked(false);
    			}
    		}
    	}
	};
	
	/**
	 * Right click on action button
	 */
	private ClickListener rightClickListener = new ClickListener(Buttons.RIGHT){
		//doesn't do anything besides override other input listener
    	public void clicked(InputEvent event, float x, float y) { }
	};
	
	/**
	 * Show buttons information on enter for action buttons
	 */
	private ClickListener enterListener = new ClickListener(){
		public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
			super.enter(event, x, y, pointer, fromActor);
			actionHoverStartTime = System.currentTimeMillis();
			hoverButton = (ImageButton) event.getListenerActor();
		}
	};
	
	/**
	 * Show buttons information on exit for action buttons
	 */
	private ClickListener exitListener = new ClickListener(){
		public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
			super.exit(event, x, y, pointer, toActor);
			actionHoverStartTime = null;
			hoverButton = null;
			stage.getActors().removeValue(actionWindow, false);
			actionWindow = null;
		}
	};
	
	private Window getActionWindow(ImageButton curBtn) {
		Action curAction = actionsButtonsMap.get(curBtn);
		Window returnWindow = new Window(curAction.getName(), skin);
		returnWindow.setPosition(curBtn.getX() + curBtn.getWidth(), curBtn.getY());
		Label costLabel = new Label("Cost: " + curAction.getCost(), skin);
		Label dmgLabel = new Label("Damage: " + curAction.getBaseDamage(), skin);
		Label accLabel = new Label("Accuracy: " + curAction.getBaseAccuracy(), skin);
		returnWindow.add(costLabel).align(Align.left).expand().row();
		returnWindow.add(dmgLabel).align(Align.left).expand().row();
		returnWindow.add(accLabel).align(Align.left).expand().row();
		return returnWindow;
	}
	
}
