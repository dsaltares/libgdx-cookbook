package com.cookbook.box2d;

import java.util.Comparator;
import java.util.PriorityQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

/*
 * @brief Deferred Raycaster implemented with a priority queue whose elements 
 * are processed within a budget-time frame per update
 * 
 */

public class RayCastManager {
	
	private class RayCastRequest {
		final public int priority;
		final public Vector2 point1;
		final public Vector2 point2;
		final public RayCastCallback callback;
		
		public RayCastRequest(int priority, Vector2 point1, Vector2 point2, RayCastCallback callback) {
			this.priority = priority;
			this.point1 = point1;
			this.point2 = point2;
			this.callback = callback;
		}
	}
	
	private static final String TAG = "RaycastManager";
	private float budgetTime;
	private World world;
	private PriorityQueue<RayCastRequest> requestQueue;
	
	/**
	 * @param budgetTime limit time (in seconds) for processing requests each update tick.  
	 */
	public RayCastManager(World world, float budgetTime) {
		this.world = world;
		this.budgetTime = budgetTime;
		this.requestQueue = new PriorityQueue<RayCastRequest>(1, new Comparator<RayCastRequest>() {
			@Override
			public int compare(RayCastRequest o1, RayCastRequest o2) {
				return o2.priority - o1.priority; // Reverse because head will be the least
			}
			
		});
	}
	
	public boolean addRequest(int priority, Vector2 point1, Vector2 point2, RayCastCallback callback) {
		return requestQueue.add(new RayCastRequest(priority, new Vector2(point1), new Vector2(point2), callback));
	}
	
	public void update() {
		long startTime = TimeUtils.nanoTime();
		
		Gdx.app.log(TAG, " -- Begining of Update tick (" + requestQueue.size() + ") --");

		RayCastRequest rr = requestQueue.poll();
		while(rr != null && TimeUtils.timeSinceNanos(startTime) < budgetTime * 1000000000f){ //budgetTime * seconds to nano
			world.rayCast(rr.callback, rr.point1, rr.point2);
			Gdx.app.log(TAG, " " + rr.point1 + " - " + rr.point2 + " processed at (" + (TimeUtils.timeSinceNanos(startTime) / 1000000000f) + ") with priority: " + rr.priority);
			rr = requestQueue.poll();
		}
		
		Gdx.app.log(TAG, " -- End of Update tick --");
		
	}
	
}
