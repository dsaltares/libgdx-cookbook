package com.cookbook.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.Agent;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.cookbook.ai.Caveman.CavemanState;

public class Dinosaur implements Agent {

	private static final String TAG = "Dinosaur";
	
	private StateMachine<Dinosaur> fsm;
	private float energy;
	private Caveman caveman;
	
	public enum DinosaurState implements State<Dinosaur> {
		HOME() {
			@Override
			public void enter(Dinosaur dinosaur) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void update(Dinosaur dinosaur) {
				dinosaur.increaseEnergy(.8f);
				
				if(dinosaur.energy == 100)
					dinosaur.getFSM().changeState(GO_FOR_A_WALK);
				
			}

			@Override
			public void exit(Dinosaur dinosaur) {
				// TODO Auto-generated method stub
				dinosaur.say("Ready for a new day");
			}

			@Override
			public boolean onMessage(Telegram telegram) {
				// TODO Auto-generated method stub
				return false;
			}
		},
		
		GO_FOR_A_WALK() {
			@Override
			public void enter(Dinosaur dinosaur) {
				dinosaur.say("Let's breathe some fresh air");
			}

			@Override
			public void update(Dinosaur dinosaur) {
		
				dinosaur.decreaseEnergy(.05f);
				// 1 in 1000
				if (MathUtils.randomBoolean(0.001f) && dinosaur.cavemanInState(CavemanState.HUNTING)) {
					MessageDispatcher.getInstance().dispatchMessage(
							0.0f, // no delay
							dinosaur, dinosaur.caveman, MessageType.GRRRRRRRR, null);
					
					dinosaur.say("GRRRRRRRRR");
				}
				
				if(dinosaur.energy == 0) {
					dinosaur.say("Enough for today");
					dinosaur.getFSM().changeState(GO_HOME);
				}
			}

			@Override
			public void exit(Dinosaur dinosaur) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean onMessage(Telegram telegram) {
				// TODO Auto-generated method stub
				return false;
			}
		},
		
		GO_HOME() {

			private double beginningTime;
			
			@Override
			public void enter(Dinosaur dinosaur) {
				beginningTime = TimeUtils.millis();
				dinosaur.say("On my way to home");
			}

			@Override
			public void update(Dinosaur dinosaur) {
				if(TimeUtils.millis() - beginningTime > 2000) {
					dinosaur.say("Good to arrive so soon");
					dinosaur.getFSM().changeState(HOME);
				}
			}

			@Override
			public void exit(Dinosaur dinosaur) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean onMessage(Telegram telegram) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
		
		
	}
	
	public Dinosaur(Caveman caveman) {
		fsm = new DefaultStateMachine<Dinosaur> (this, DinosaurState.HOME);
		energy = MathUtils.random(0, 100);
		this.caveman = caveman;
	}
	
	public StateMachine<Dinosaur> getFSM() {
		return fsm;
	}
	
	public void update(float delta) {
		fsm.update();
	}
	
	public void increaseEnergy(float value) {
		energy = MathUtils.clamp(energy+value, 0, 100);
	}
	
	public void decreaseEnergy(float value) {
		energy = MathUtils.clamp(energy-value, 0, 100);
	}
	
	private boolean cavemanInState(CavemanState state) {
		return caveman.getFSM().isInState(state);
	}
	
	private void say(String thought) {
		Gdx.app.log(TAG, thought);
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return fsm.handleMessage(msg);
	}
}
