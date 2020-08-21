package AudioCommands;


import Audio.MusicManager;
import Main.main;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.awt.*;
import java.util.concurrent.BlockingQueue;

@CommandInfo(
        name = "queue",
        description = "queue",
        requirements = "The Bot needs permission to join VC"
)

@RequiredPermissions(Permission.VOICE_CONNECT)

@Author("Rin#6969")
public class Queue extends Command {



    private AudioPlayerManager playerManager;


    public Queue() {
        this.name = "queue";
        this.help = "queuequeuequeue";
        this.arguments = "No further Arguments";
        this.botPermissions = new Permission[]{Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};
        this.guildOnly = false;
        this.ownerCommand = false;

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }


    @Override
    protected void execute(CommandEvent event) {
        User mo = event.getSelfUser().getJDA().getUserById("607758113172357130");

        Guild guild = event.getGuild();
        String message = event.getMessage().getContentRaw();
        User user = event.getAuthor();

        if (!guild.getAudioManager().isConnected() && !guild.getAudioManager().isAttemptingToConnect()) {

            event.getChannel().sendMessage("Error: not playing anything").queue();

        }
        MusicManager manager = getGuildAudioPlayer(event.getGuild());
        if(manager.scheduler.getQueue(event.getGuild()).isEmpty()) {
            event.getChannel().sendMessage("Error: not playing anything").queue();
        } else {

            BlockingQueue cock = manager.scheduler.getQueue(event.getGuild());
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Current Queue")
                    .setFooter("'yeah you like that?' - Mo", mo.getEffectiveAvatarUrl());
            builder.setColor(Color.CYAN);
            for (int i = 0; i < cock.size(); i++) {
                AudioTrack track = (AudioTrack) cock.toArray()[i];
                builder.addField(" **->** ", "["+track.getInfo().title+"]"+"("+"https://youtube.com/watch?v="+track.getInfo().identifier+")", false);
            }
            event.getChannel().sendMessage(builder.build()).queue();

        }





    }

    private synchronized MusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        MusicManager musicManager = Play.musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new MusicManager(playerManager);
            Play.musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }


}
