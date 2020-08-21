package AudioCommands;


import Audio.MusicManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@CommandInfo(
        name = "skip",
        description = "Play Music from Links or Search Terms!",
        requirements = "The Bot needs permission to join VC"
)

@RequiredPermissions(Permission.VOICE_CONNECT)

@Author("Rin#6969")
public class Skip extends Command {



    private AudioPlayerManager playerManager;


    public Skip() {
        this.name = "skip";
        this.help = "Skip the current Track";
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

        Guild guild = event.getGuild();
        String message = event.getMessage().getContentRaw();
        User user = event.getAuthor();

        if (!guild.getAudioManager().isConnected() && !guild.getAudioManager().isAttemptingToConnect()) {
            event.getChannel().sendMessage("Bruh").queue();

        }
        MusicManager manager = getGuildAudioPlayer(event.getGuild());
        if(manager.player.getPlayingTrack() == null) {
            event.getChannel().sendMessage("Error: No tracke play").queue();
        } else {
            skipTrack(event.getTextChannel());
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

    private void skipTrack(TextChannel channel) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

        channel.sendMessage("Skipped to next track.").queue();
    }

}
