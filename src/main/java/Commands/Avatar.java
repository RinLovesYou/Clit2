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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.kodehawa.lib.imageboards.DefaultImageBoards;
import net.kodehawa.lib.imageboards.entities.BoardImage;

import java.awt.*;

@CommandInfo(
        name = "avatar",
        description = "avvatar gettttt",
        requirements = {
                "The bot has all necessary permissions.",
        }
)
@Error(
        value = "If arguments are provided, but they are not an integer.",
        response = "[PageNumber] is not a valid integer!"
)
@RequiredPermissions({Permission.MESSAGE_EMBED_LINKS})
@Author("Rin#6969")
public class Avatar extends Command {


    public Avatar(EventWaiter waiter)
    {
        this.name = "avatar";
        this.help = "avatar";
        this.arguments = "avataqretga get";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION};
        this.guildOnly = false;
        this.ownerCommand = false;

    }


    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");
        Member info;
        if(event.getMessage().getMentionedMembers().isEmpty() && args.length == 1) {
            info = event.getMember();
        } else {
            try {
                info = event.getMessage().getMentionedMembers().get(0);
            } catch(Exception e) {
                try {
                    info = event.getGuild().getMemberById(args[1]);
                } catch(Exception ex) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Error");
                    builder.setColor(Color.RED);
                    builder.addField("Couldn't find User!", "Please ensure the User is in your Guild/Double check their ID", false);
                    builder.addField("Error message", ex.getMessage(), false);
                    builder.setFooter("Clinet 2", event.getSelfMember().getUser().getEffectiveAvatarUrl());
                    event.getChannel().sendMessage(builder.build()).queue();
                    return;
                }
            }
        }


        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(info.getEffectiveName()+"'s Avatar", info.getUser().getEffectiveAvatarUrl()+"?size=1024")
                .setImage(info.getUser().getEffectiveAvatarUrl()+"?size=1024");

        event.getChannel().sendMessage(builder.build()).queue();
    }

}
