import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup
import bjda.ui.component.*
import bjda.ui.core.*
import bjda.ui.core.Component.Companion.minus
import bjda.ui.core.Component.Companion.rangeTo

val VoteCommands = SuperCommandGroup.create("todo", "Todo Commands",
    CreateTodo(), TodoSettings()
)

private class CreateTodo: SuperCommand(name = "create", description = "Create a Todo List") {
    override fun run() {
        UI(
            TodoApp()
        ).reply(event)
    }
}

private class TodoSettings: SuperCommand(name = "settings", description = "Manager Settings") {
    override fun run() {
        val app = UI(
            Pager()-{
                + TodoApp()
                + Text()..{
                    content = "Todo Settings"
                }
                + Embed()..{
                    title = "User Settings"
                }
            }
        )

        app.reply(event)
    }
}