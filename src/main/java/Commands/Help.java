package Commands;

import Audio.MusicManager;
import Main.info;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.jagrosh.jdautilities.menu.Paginator;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "Help",
        description = "Shows Help",
        requirements = "Needs Permission to send Messages"
)

@RequiredPermissions(Permission.MESSAGE_WRITE)

@Author("Rin#6969")
public class Help extends Command {



    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    private final Paginator.Builder pbuilder;
    public Help(EventWaiter waiter) {
        this.name = "help";
        this.help = "Shows a list of Commands";
        this.arguments = "Use Reactions to navigate";
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.guildOnly = false;
        this.ownerCommand = false;
        pbuilder = new Paginator.Builder().setColumns(1)
                .setItemsPerPage(5)
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .useNumberedItems(false)
                .setFinalAction(m -> {
                    m.editMessage(sendInvite(m.getTextChannel())).queue();
                    m.clearReactions().queue();
                    m.delete().queueAfter(10, TimeUnit.SECONDS);

                })
                .setEventWaiter(waiter)
                .setTimeout(1, TimeUnit.MINUTES);
    }

    public MessageEmbed sendInvite(TextChannel c) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.decode("#6ab04c"));
        builder.setAuthor("Invite me to your server!", "https://discord.com/api/oauth2/authorize?client_id=738960057219285076&permissions=8&scope=bot", c.getGuild().getSelfMember().getUser().getEffectiveAvatarUrl());
        builder.setTitle("Click this to invite me to your Server!", "https://discord.com/api/oauth2/authorize?client_id=738960057219285076&permissions=8&scope=bot");
        return builder.build();
    }

    @Override
    protected void execute(CommandEvent event) {
        int page = 1;
        if(!event.getArgs().isEmpty())
        {
            try
            {
                page = Integer.parseInt(event.getArgs());
            }
            catch(NumberFormatException e)
            {
                event.reply(event.getClient().getError()+" `"+event.getArgs()+"` is not a valid integer!");
                return;
            }
        }

        pbuilder.clearItems();
        event.getClient().getCommands().stream()
                .map(command -> "**" + info.PREFIX+command.getName() + "** \n" + command.getArguments())
                .forEach(pbuilder::addItems);

        Paginator p = pbuilder.setColor(event.isFromType(ChannelType.TEXT) ? event.getSelfMember().getColor() : Color.black)
                .setUsers(event.getAuthor())
                .setText(" ")
                .setLeftRightText("bruh", "bruh2")
                .build();
        p.paginate(event.getChannel(), page);



    }
}
