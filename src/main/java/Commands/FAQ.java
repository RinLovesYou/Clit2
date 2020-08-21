package Commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.Error;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

@CommandInfo(
        name = "info",
        description = "botinfo",
        requirements = {
                "The bot has all necessary permissions.",
        }
)

@RequiredPermissions({Permission.MESSAGE_EMBED_LINKS})
@Author("Rin#6969")
public class FAQ extends Command {


    public FAQ(EventWaiter waiter)
    {
        this.name = "info";
        this.help = "info about clinet 2";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION};
        this.guildOnly = false;
        this.ownerCommand = false;

    }


    @Override
    protected void execute(CommandEvent event) {
        User clit = event.getSelfUser();

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Info about Clinet 2")
                .setThumbnail(clit.getEffectiveAvatarUrl());

        event.getChannel().sendMessage(builder.build()).queue();
    }

}
