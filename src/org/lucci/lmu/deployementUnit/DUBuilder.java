package org.lucci.lmu.deployementUnit;

import java.util.ArrayList;

/**
 * Contains the graph of dependencies
 * @author louis
 *
 */
public class DUBuilder {
	private int distance;
	private DeploymentUnit root;
	
	public DUBuilder(int distance) {
		this.distance = distance;
		this.root = new DeploymentUnit();
	}
	
	public DUBuilder(int distance, DeploymentUnit root) {
		this.distance = distance;
		this.root = root;
	}

	public int getDistance() {
		return distance;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public static void main(String[] args){
		DUBuilder dub = new DUBuilder(1, new DeploymentUnit("root"));
	}
	
	
	
	

}
