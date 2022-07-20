package bjda.plugins.ui.hook

import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.hook.event.SelectListener
import bjda.ui.core.hooks.Delegate
import bjda.ui.types.AnyComponent
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction

/**
 * Create a Select Event Listener and returns its id
 */
class MenuSelect(
    id: String = UIEvent.createId(),
    private val handler: (SelectMenuInteraction) -> Unit
) : EventHook(id), SelectListener {
    override fun listen(id: String) {
        UIEvent.listen(id, this)
    }

    override fun onSelect(event: SelectMenuInteraction) {
        handler(event)
    }

    override fun destroy(id: String) {
        UIEvent.menus.remove(id)
    }

    companion object {
        /**
         * Create and Use the hook and return its id as a delegate
         */
        fun AnyComponent.onSelect(id: String = UIEvent.createId(), handler: (SelectMenuInteraction) -> Unit): Delegate<String> {
            val hook = MenuSelect(id, handler)

            return Delegate { this use hook }
        }
    }
}