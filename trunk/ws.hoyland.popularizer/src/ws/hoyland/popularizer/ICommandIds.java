package ws.hoyland.popularizer;

/**
 * Interface defining the application's command IDs.
 * Key bindings can be defined for specific commands.
 * To associate an action with a command, use IAction.setActionDefinitionId(commandId).
 *
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {

    public static final String CMD_HOME = "ws.hoyland.popularizer.home";
    public static final String CMD_VISIT = "ws.hoyland.popularizer.visit";
    
}
