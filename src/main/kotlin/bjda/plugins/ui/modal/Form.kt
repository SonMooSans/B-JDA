package bjda.plugins.ui.modal

import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.hook.event.ModalListener
import bjda.ui.component.action.Action
import bjda.ui.core.hooks.Delegate
import bjda.ui.core.hooks.IHook
import bjda.ui.core.init
import bjda.ui.types.AnyComponent
import bjda.ui.types.Init
import bjda.utils.LambdaList
import bjda.utils.build
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.Modal
import net.dv8tion.jda.internal.interactions.component.ModalImpl

/**
 * Same usage as Component API but specified for Modal
 *
 * Cannot update modal with state or forceUpdate
 *
 * Modal ID is generated per instance
 *
 * Note: use it as Hook to enable AutoReply
 */
open class Form(val id: String = UIEvent.createId(),
           props: Init<Props>
): ModalListener {

    class Props {
        lateinit var title: String
        lateinit var render: LambdaList<ModalRow>

        lateinit var onSubmit: (ModalInteractionEvent) -> Unit

        fun row(items: LambdaList<Action>): ModalRow {
            return ModalRow(items.build())
        }

        fun row(item: Action): ModalRow {
            return ModalRow(listOf(item))
        }

        fun ModalInteractionEvent.value(id: String): String {
            return getValue(id)!!.asString
        }

        fun ModalInteractionEvent.value(item: Action): String {
            return value(item.id)
        }

        class ModalRow(val items: List<Action>) {
            fun build(): List<ItemComponent> {
                return items.map {
                    it.build()
                }
            }
        }
    }

    val props = Props().init(props)

    /**
     * Listen submit events
     */
    fun listen() {
        UIEvent.listen(id, this)
    }

    /**
     * Build a modal and set up its listeners
     * @param listen If disabled, Submit events of this modal will not be listened
     */
    fun create(listen: Boolean = true): ModalImpl {
        if (listen) {
            this.listen()
        }

        with (props) {
            val rows = render.build().map {
                ActionRow.of(it.build())
            }

            return ModalImpl(id, title, rows)
        }
    }

    fun destroy() {
        UIEvent.modals.remove(id)
    }

    override fun onSubmit(event: ModalInteractionEvent) {
        props.onSubmit(event)
    }

    companion object {
        fun AnyComponent.form(props: Init<Props>): Delegate<Modal> {
            val form = Form(props = props)
            val hook = ModalHook(form)

            return Delegate { this use hook }
        }

        class ModalHook(private val form: Form) : IHook<Modal> {
            override fun onCreate(component: AnyComponent, initial: Boolean): Modal {
                if (initial) {
                    form.listen()
                }

                return form.create(listen = false)
            }

            override fun onDestroy() {
                form.destroy()
            }
        }
    }
}