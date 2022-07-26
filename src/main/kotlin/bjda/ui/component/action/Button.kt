package bjda.ui.component.action

import bjda.ui.core.apply
import bjda.ui.types.Apply
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.internal.interactions.component.ButtonImpl

class Button(props: Apply<Props>) : Action {
    private val props = Props().apply(props)

    constructor(id: String, props: Apply<Props>) : this(props) {
        this.props.id = id
    }

    class Props {
        var id: String? = null
        var style: ButtonStyle = ButtonStyle.PRIMARY
        var label: String? = null
        var emoji: Emoji? = null
        var url: String? = null
        var disabled: Boolean = false
    }

    override fun build(): ItemComponent {
        with (props) {
            return ButtonImpl(id, label, style, url, disabled, emoji)
        }
    }
}