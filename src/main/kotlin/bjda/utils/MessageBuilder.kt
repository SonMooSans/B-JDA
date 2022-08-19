package bjda.utils

import bjda.ui.component.TextStyle
import bjda.ui.core.internal.RenderData
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction
import net.dv8tion.jda.api.utils.AttachmentOption
import net.dv8tion.jda.internal.requests.restaction.interactions.MessageEditCallbackActionImpl
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyCallbackActionImpl
import java.awt.Color
import java.io.File
import java.io.InputStream
import java.time.OffsetDateTime

fun message(init: MessageBuilder.() -> Unit): MessageBuilder {
    return MessageBuilder().apply(init)
}

fun IReplyCallback.reply(init: ReplyMessageBuilder.() -> Unit): ReplyCallbackActionImpl {
    val action = this.deferReply() as ReplyCallbackActionImpl

    action.applyMessage(
        ReplyMessageBuilder(action).apply(init).build()
    )

    return action
}

fun IMessageEditCallback.edit(init: MessageBuilder.() -> Unit): MessageEditCallbackActionImpl {
    val action = deferEdit() as MessageEditCallbackActionImpl

    action.applyMessage(
        EditMessageBuilder(action).apply(init).build()
    )

    return action
}

open class ReplyMessageBuilder(val base: ReplyCallbackAction) : MessageBuilder() {
    fun ephemeral(value: Boolean) = base.setEphemeral(value)

    fun file(data: InputStream, name: String, vararg options: AttachmentOption) = base.addFile(data, name, * options)
    fun file(file: File, name: String, vararg options: AttachmentOption) = base.addFile(file, name, * options)
    fun file(file: File, vararg options: AttachmentOption) = base.addFile(file, * options)
    fun file(file: ByteArray, name: String, vararg options: AttachmentOption) = base.addFile(file, name, * options)
}

open class EditMessageBuilder(val base: MessageEditCallbackAction) : MessageBuilder() {
    fun file(data: InputStream, name: String, vararg options: AttachmentOption) = base.addFile(data, name, * options)
    fun file(file: File, name: String, vararg options: AttachmentOption) = base.addFile(file, name, * options)
    fun file(file: File, vararg options: AttachmentOption) = base.addFile(file, * options)
    fun file(file: ByteArray, name: String, vararg options: AttachmentOption) = base.addFile(file, name, * options)

    fun retainFiles(vararg attachments: Message.Attachment): MessageEditCallbackAction {
        return base.retainFiles(* attachments)
    }

    fun retainFiles(attachments: Collection<Message.Attachment>): MessageEditCallbackAction {
        return base.retainFiles(attachments)
    }
}


open class MessageBuilder: RenderData() {
    fun content(text: String) {
        setContent(text)
    }

    fun text(
        content: String? = null,
        language: String? = null,
        type: TextStyle = TextStyle.Normal
    ) {
        when (type) {
            TextStyle.Normal -> append(content)
            TextStyle.Line -> appendLine(content)
            TextStyle.CodeLine -> appendCodeLine(content)
            TextStyle.CodeBlock -> appendCodeBlock(content, language)
        }
    }

    fun embed(
        url: String? = null,
        title: String? = null,
        description: String? = null,
        type: EmbedType? = null,
        timestamp: OffsetDateTime? = null,
        color: Color = Color.BLACK,
        thumbnail: MessageEmbed.Thumbnail? = null,
        provider: MessageEmbed.Provider? = null,
        author: MessageEmbed.AuthorInfo? = null,
        videoInfo: MessageEmbed.VideoInfo? = null,
        footer: MessageEmbed.Footer? = null,
        image: MessageEmbed.ImageInfo? = null,
        fields: List<MessageEmbed.Field>? = null,
    ) {
        val embed = MessageEmbedImpl(
            url, title, description, type, timestamp, color.rgb, thumbnail, provider, author, videoInfo, footer, image, fields
        )

        addEmbeds(embed)
    }

    fun embeds(vararg embeds: MessageEmbed) {
        addEmbeds(* embeds)
    }

    fun embeds(embeds: Collection<MessageEmbed>) {
        addEmbeds(embeds)
    }

    fun row(components: Collection<ItemComponent>) {
        addActionRow(ActionRow.of(components))
    }

    fun row(vararg components: ItemComponent) {
        addActionRow(ActionRow.of(* components))
    }
    fun rows(rows: Collection<ActionRow>) {
        addActionRow(rows)
    }

    fun rows(vararg rows: ActionRow) {
        addActionRow(* rows)
    }
}