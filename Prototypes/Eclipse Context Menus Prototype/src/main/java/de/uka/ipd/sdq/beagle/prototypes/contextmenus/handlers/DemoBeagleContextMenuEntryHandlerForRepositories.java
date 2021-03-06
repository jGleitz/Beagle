package de.uka.ipd.sdq.beagle.prototypes.contextmenus.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handles the context menu entries, that start an analysis of an entire project.
 *
 * @author Roman Langrehr
 */
public class DemoBeagleContextMenuEntryHandlerForRepositories extends AbstractHandler {

	/**
	 * Regular Expression for file extensions for which the context menu entry should be
	 * displayed.
	 */
	private static final String FILE_EXTENSION_MATCHER = "repository|repository_diagram";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

		final Object firstElement = structuredSelection.getFirstElement();
		if (firstElement instanceof IFile) {
			final IFile clickedFile = (IFile) firstElement;
			final IPath clickedFilePath = clickedFile.getFullPath();
			if (clickedFilePath.getFileExtension().matches(FILE_EXTENSION_MATCHER)) {
				final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
				MessageDialog.openInformation(window.getShell(), "Beagle is alive!",
						"Belive it, or not. But Beagle ist alive!\n" + "You want to analyse: The whole repository: "
								+ clickedFilePath.toString());
			} else {
				MessageDialog.openInformation(shell, "Error",
						"The handler was not executet with a file with a proper file extension.\n"
								+ "This should not be possible!\n" + "selection.getFirstElement() returned a path to: "
								+ clickedFilePath.toString());
			}
		} else {
			MessageDialog.openInformation(shell, "Error",
					"The handler was not executet with a file.\n" + "This should not be possible!\n"
							+ "selection.getFirstElement() returned " + firstElement.toString());
		}
		return null;
	}

}
