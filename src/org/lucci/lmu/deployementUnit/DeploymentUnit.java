package org.lucci.lmu.deployementUnit;

import java.util.List;

public class DeploymentUnit {
	private String path;
	private String name;
	private List<DeploymentUnit> dependencies;

	public DeploymentUnit(String path, List<DeploymentUnit> dependencies){
		this.path = path;
		this.dependencies = dependencies;
	}
	
	public DeploymentUnit(String path, String name, List<DeploymentUnit> dependencies) {
		this.path = path;
		this.name = name;
		this.dependencies = dependencies;
	}
	
	public DeploymentUnit() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public List<DeploymentUnit> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<DeploymentUnit> dependencies) {
		this.dependencies = dependencies;
	}
	
	public static void main(String[] args){
		
	}
	
}
