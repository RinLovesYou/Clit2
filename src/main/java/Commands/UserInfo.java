package Commands;

import Main.info;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.Error;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "userinfo",
        description = "Gets Info about a User.",
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
public class UserInfo extends Command {


    public UserInfo(EventWaiter waiter)
    {
        this.name = "userinfo";
        this.help = "info about user";
        this.arguments = "mention a user";
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


        String pfp;
        if(info.getUser().getEffectiveAvatarUrl() == null) {
            pfp = event.getSelfMember().getUser().getEffectiveAvatarUrl();
        } else {
            pfp = info.getUser().getEffectiveAvatarUrl();
        }
        boolean bot = info.getUser().isBot();
        String creation = String.valueOf(info.getTimeCreated().toLocalDate());
        Guild guild = info.getGuild();
        String userid = info.getUser().getId();
        String jointime = String.valueOf(info.getTimeJoined().toLocalDate());
        String name = info.getAsMention();
        String realname = info.getEffectiveName() + "#" + info.getUser().getDiscriminator();
        String status = String.valueOf(info.getOnlineStatus().toString().toLowerCase());
        String rich;
        String roles;




        if(info.getActivities().isEmpty()){

            rich = "Not playing anything";
        } else {
            rich = info.getActivities().get(0).getName();
        }
        StringBuilder builder1 = new StringBuilder();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setFooter(realname, info.getUser().getEffectiveAvatarUrl());
        builder.setImage(pfp);
        builder.setTitle("Userinfo");
        builder.setColor(Color.CYAN);
        builder.addField("User", name, true);
        builder.addField("Online Status", status, false);
        builder.addField("Is Bot", String.valueOf(bot), false);
        builder.addField("Rich Presence", rich, false);


        for(int i = 0; i < info.getRoles().size(); i++) {

            Role role = info.getRoles().get(i);
            roles = role.getName();

            builder1.append(roles + ", ");
            builder1.toString();


        }
        builder.addField("Roles", String.valueOf(builder1), false);
        builder.addField("Creation Date", creation, false);
        builder.addField("Joined Guild", jointime, false);
        event.getChannel().sendMessage(builder.build()).queue();
    }

}
