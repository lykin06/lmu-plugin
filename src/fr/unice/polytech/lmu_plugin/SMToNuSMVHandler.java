package fr.unice.polytech.lmu_plugin;

import java.util.jar.JarFile;

import javax.swing.JOptionPane;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;

public class SMToNuSMVHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		if (!(selection instanceof StructuredSelection))
			return null;

		Object selected = ((StructuredSelection) selection).getFirstElement();

		// The type should be guaranteed by the "isVisibleWhen"
		assert (selected instanceof JarFile);

		// do something
		System.out.println("selected: " + selected.toString());
		System.out.println("selection: " + selection.toString());
		System.out.println("class: " + selection.getClass());
		
		// counter.getNbStatemachines() + " state machines\n" +
		// counter.getNbStates() + " states\n"
		// + counter.getNbTransitions() + " transitions",
		// "State Machines", JOptionPane.INFORMATION_MESSAGE);
		return null;
	}
}
