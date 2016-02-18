package org.lucci.lmu.deployementUnit;

import java.util.ArrayList;
import java.util.List;

public class DeploymentUnit {
	private String name;
	private List<DeploymentUnit> dependencies;

	public DeploymentUnit(String name){
		this.name = name;
		this.dependencies = new ArrayList<>();
	}
	
	public DeploymentUnit(String name, List<DeploymentUnit> dependencies){
		this.name = name;
		this.dependencies = dependencies;
	}
	
	public DeploymentUnit() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<DeploymentUnit> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<DeploymentUnit> dependencies) {
		this.dependencies = dependencies;
	}

	public DeploymentUnit addDependency(DeploymentUnit newDep){
		return dependencies.add(newDep) ? newDep : null;
	}
	
	public void addDependencies(List<DeploymentUnit> newDeps){
		for( DeploymentUnit dep : newDeps){
			addDependency(dep);
		}
	}
}
