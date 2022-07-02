package bjda.plugins.supercommand

import bjda.plugins.supercommand.entries.PermissionEntry
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl
import java.awt.Color

abstract class SuperCommand (
    override val name: String,
    val description: String = "No Description",
    override val guildOnly: Boolean? = null,
    override val permissions: DefaultMemberPermissions? = null
): SuperNode, PermissionEntry {
    lateinit var event: SlashCommandInteractionEvent
    private val options = ArrayList<OptionValue>()

    fun option(type: OptionType, name: String, description: String = "No Description"): OptionValue {
        val value = OptionValue(name, type, description)
        options.add(value)

        return value
    }

    internal fun execute(event: SlashCommandInteractionEvent) {
        this.event = event

        for (option in options) {
            option.update(event)
        }

        try {
            run()
        } catch (e: Throwable) {
            error(e.message)
        }
    }

    fun error(message: String?) {
        event.replyEmbeds(
            EmbedBuilder()
                .setTitle(message?: "ERROR")
                .setColor(Color.RED)
                .build()
        ).setEphemeral(true).queue()
    }

    abstract fun run()

    override fun build(listeners: Listeners): CommandDataImpl {
        val data = CommandDataImpl(name, description).setPermissions()

        data.addOptions(options.map {
            it.data
        })

        listeners[Info(name = name)] = this

        return data
    }

    internal fun buildSub(group: String, subgroup: String? = null, listeners: Listeners): SubcommandData {
        val data = SubcommandData(name, description)

        data.addOptions(options.map {
            it.data
        })

        listeners[Info(group, subgroup, name)] = this

        return data
    }

    class OptionValue(
        name: String,
        type: OptionType,
        description: String) {

        private var default: () -> Any? = { null }
        private var value: Any? = null
        internal var data = OptionData(type, name, description)

        fun required(value: Boolean): OptionValue {
            data = data.setRequired(value)

            return this
        }

        fun default(value: () -> Any?): OptionValue {
            this.default = value
            return this
        }

        fun autoComplete(value: Boolean): OptionValue {
            data = data.setAutoComplete(value)
            return this
        }

        fun intRange(range: Pair<Long, Long>): OptionValue {
            val (min, max) = range
            data.setRequiredRange(min, max)

            return this
        }

        fun doubleRange(range: Pair<Double, Double>): OptionValue {
            val (min, max) = range
            data.setRequiredRange(min, max)

            return this
        }

        fun channel(vararg types: ChannelType): OptionValue {
            if (data.type != OptionType.CHANNEL)
                error("Option Type must be channel")

            data.setChannelTypes(*types)

            return this
        }

        fun choices(vararg choice: Pair<String, String>): OptionValue {
            data.addChoices(choice.map {(key, value)->
                Choice(key, value)
            })

            return this
        }

        fun update(event: SlashCommandInteractionEvent) {
            val mapping = event.getOption(data.name)

            value = if (mapping == null) {
                this.default()
            } else {
                when (mapping.type) {
                    OptionType.INTEGER -> mapping.asLong
                    OptionType.NUMBER -> mapping.asDouble
                    OptionType.BOOLEAN -> mapping.asBoolean
                    OptionType.STRING -> mapping.asString
                    OptionType.ATTACHMENT -> mapping.asAttachment
                    //Role, User, Channels are all mentionable
                    OptionType.MENTIONABLE, OptionType.ROLE, OptionType.CHANNEL, OptionType.USER -> mapping.asMentionable
                    else -> error("Unknown option type ${mapping.type}")
                }
            }
        }

        operator fun<T> getValue(parent: Any, property: Any): T {
            return value as T
        }
    }
}