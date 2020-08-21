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
import com.jagrosh.jdautilities.menu.Slideshow;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.kodehawa.lib.imageboards.DefaultImageBoards;
import net.kodehawa.lib.imageboards.ImageBoard;
import net.kodehawa.lib.imageboards.boards.Board;
import net.kodehawa.lib.imageboards.boards.CustomBoard;
import net.kodehawa.lib.imageboards.boards.DefaultBoards;
import net.kodehawa.lib.imageboards.entities.BoardImage;
import net.kodehawa.lib.imageboards.entities.Rating;
import net.kodehawa.lib.imageboards.entities.impl.DanbooruImage;
import net.kodehawa.lib.imageboards.entities.impl.KonachanImage;
import net.kodehawa.lib.imageboards.entities.impl.Rule34Image;
import net.kodehawa.lib.imageboards.entities.impl.YandereImage;
import okhttp3.OkHttpClient;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "hentai",
        description = "anime people have sex.",
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
public class Hentai extends Command {

    private Slideshow.Builder pbuilder;
    private EventWaiter waiter;
    public static CustomBoard sankaku = new CustomBoard("https", "capi-v2.sankakucomplex.com", "posts", null, "posts");
    public static ImageBoard<YandereImage> SANKAKUCHANNEL = new ImageBoard<>(sankaku, ImageBoard.ResponseFormat.JSON, YandereImage.class);


    public Hentai(EventWaiter waiter) {
        this.name = "hentai";
        this.help = "sex";
        this.arguments = "search query";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION};
        this.guildOnly = false;
        this.ownerCommand = false;
        this.waiter = waiter;

    }


    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");
        pbuilder = new Slideshow.Builder()
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .setFinalAction(m -> {
                    m.clearReactions().queue();
                    m.delete().queue();

                })
                .setEventWaiter(waiter)
                .setTimeout(1, TimeUnit.MINUTES);

        if (event.getTextChannel().isNSFW()) {

            String search = event.getMessage().getContentRaw().replaceFirst("cli;hentai ", "");

            int page = 1;
            
            DefaultImageBoards.DANBOORU.search(search).blocking().stream()
                    .map(DanbooruImage::getFile_url)
                    .forEach(pbuilder::addItems);





            Slideshow p = pbuilder.setColor(event.isFromType(ChannelType.TEXT) ? event.getSelfMember().getColor() : Color.black)
                    .setUsers(event.getAuthor())
                    .setText(" ")
                    .build();
            try {
                p.display(event.getChannel());
            } catch(Exception e) {
               event.reply(e.getMessage());
            }

        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("ERROR");
            builder.setColor(Color.RED);
            builder.addField("Reason", "This channel is not NSFW!", false);
            event.getChannel().sendMessage(builder.build()).queue();
        }
    }
}

