package org.lucci.lmu.deployementUnit;

public class DUBuilder {
	//TODO: set folder
	private static final String DEP_FOLDER = "";
	private int distance;
	private DeploymentUnit root;
	
	public DUBuilder(int distance) {
		this.distance = distance;
		root = new DeploymentUnit();
	}

	public int getDistance() {
		return distance;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	
	

}
