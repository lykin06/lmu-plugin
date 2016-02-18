package org.lucci.lmu.deployementUnit;

import java.util.ArrayList;
import java.util.List;

import org.lucci.lmu.AssociationRelation;
import org.lucci.lmu.Entity;
import org.lucci.lmu.Model;

public class DeploymentUnit {
	private String name;
	private List<DeploymentUnit> dependencies;
	private boolean visited;

	public DeploymentUnit(String name){
		this.name = name;
		this.dependencies = new ArrayList<>();
		this.visited = false;
	}
	
	public DeploymentUnit(String name, List<DeploymentUnit> dependencies){
		this.name = name;
		this.dependencies = dependencies;
		this.visited = false;
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
	
	private void mark(){
		this.visited = true;
	}
	
	private boolean isMarked(){
		return this.visited;
	}
	
	/**
	 * Depth First Search to link all the deps
	 */
	public void DFSLinking(DeploymentUnit dU, Model model){
		dU.mark();
		Entity parent= new Entity();
		parent.setName(dU.getName());
		model.addEntity(parent);
		for(DeploymentUnit dep : dU.getDependencies()){
			if(!dep.isMarked()){
				Entity entity = new Entity();
				entity.setName(dep.getName());
				model.addEntity(entity);
				model.addRelation(new AssociationRelation(parent,entity));
				DFSLinking(dep, model);
			}
		}
	}
}
