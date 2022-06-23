package bjda.plugins.ui.hook

import bjda.plugins.ui.AutoReply
import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.event.ButtonListener
import bjda.ui.types.AnyComponent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class ButtonClick(
    id: String = UIEvent.createId(),
    private val handler: (ButtonInteractionEvent) -> AutoReply
) : EventHook(id), ButtonListener {

    override fun onCreate(component: AnyComponent): String {
        UIEvent.listen(id, this)

        return super.onCreate(component)
    }

    override fun onClick(event: ButtonInteractionEvent) {
        handler(event).reply(ui, event)
    }
}